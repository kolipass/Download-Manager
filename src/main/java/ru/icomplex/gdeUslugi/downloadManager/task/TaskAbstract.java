package ru.icomplex.gdeUslugi.downloadManager.task;


import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;

import java.math.BigDecimal;
import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: artem
 * Date: 09.11.12
 * Родоначальник тасков. Тяжелая задача будет выполнена в новом потоке. Таск генерирует события.
 */
public abstract class TaskAbstract extends Observable implements Runnable {
    private final Object GUI_INITIALIZATION_MONITOR = new Object();
    protected String tag;
    protected StringResourceManager resourceManager;
    volatile protected TaskStatus taskStatus;
    protected volatile boolean currentlyStarted = false;
    Thread thread = null;
    private boolean pauseThreadFlag = false;

    protected TaskAbstract(StringResourceManager resourceManager, String tag) {
        this.resourceManager = resourceManager;
        this.taskStatus = new TaskStatus(tag);
        this.tag = tag;
    }

    /**
     * Получим имя файла
     *
     * @param url путь из которого выудим последнюю часть после /
     * @return верну строку, как раз такую, как надо)
     */

    static public String getFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * Для ограничения колличества вызовов обновления на задачах с "многотысячным" процессом.
     * например наш таск загрузки публикует обновление своего состояния каждый скаченный килобайт.
     * Чтобы ограничить процентом, используется данная фнкиция
     *
     * @param max Максимальная величина
     * @return единица обновления
     */

    static public Long getPercentRate(Long max) {
        BigDecimal decimal = BigDecimal.valueOf(max / 100);
        return decimal.toBigInteger().longValue();
    }

    /**
     * Пауза таска. Остановка произайдет не сразу, а через некоторое время из-за выполнение тяжелого процесса в другом потоке
     */
    public void pause() {
        if (thread != null || currentlyStarted) {
            try {
                publishProgress(taskStatus.setStatus(TaskStatus.STATUS_PAUSED));
                pauseThread();
                setChanged();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Возобновление таска. По Факту создастся новый поток
     */
    public void resume() {
        resumeThread();
    }

    /**
     * Отмена таска. Остановка произайдет не сразу, а через некоторое время из-за выполнение тяжелого процесса в другом потоке
     */
    public void cancel() {
        publishProgress(taskStatus.setStatus(TaskStatus.STATUS_CANCELED));
    }

    /**
     * Данный метод расскажет подписанным слушателям об изменениях.
     *
     * @param taskStatus текущий статус
     */
    protected void publishProgress(TaskStatus taskStatus) {
        if (taskStatus != null) {
            setChanged();
            notifyObservers(taskStatus);
        }
    }

    /**
     * "Тяжелая работа" выполняется здесь
     *
     * @return конечный статус
     */
    public abstract TaskStatus heavyTask();

    /**
     * Метод по окончанию выполнения таска
     *
     * @param taskStatus текущий статус
     */
    protected void onPostExecute(TaskStatus taskStatus) {
        publishProgress(taskStatus);
    }

    /**
     * Запуск единственно верный. "тяжелой работы" в новом потоке
     */

    public void createTreadTask() {

        if (taskStatus.getStatus() != TaskStatus.STATUS_WORKING)
            try {
                thread = new Thread(this);
//                Thread.sleep(200);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    /**
     * Внутренняя реализация интерфейса Runnable. Для запуска таска использовать createTreadTask()!
     */
    @Override
    public final void run() {
        currentlyStarted = true;
        onPostExecute(heavyTask());
        currentlyStarted = false;
    }

    /**
     * Обязательно вызывать перед каждой итеррацией в тяжелом таске, иначе пауза не отработает
     */

    protected void checkForPaused() {
        synchronized (GUI_INITIALIZATION_MONITOR) {
            while (pauseThreadFlag) {
                try {
                    GUI_INITIALIZATION_MONITOR.wait();
                } catch (Exception e) {
                }
            }
        }
    }

    private void pauseThread() throws InterruptedException {
        pauseThreadFlag = true;
    }

    private void resumeThread() {
        synchronized (GUI_INITIALIZATION_MONITOR) {
            pauseThreadFlag = false;
            GUI_INITIALIZATION_MONITOR.notify();
        }
    }

    public boolean isCurrentlyStarted() {
        return currentlyStarted;
    }

    public void setCurrentlyStarted(boolean currentlyStarted) {
        this.currentlyStarted = currentlyStarted;
    }
}

