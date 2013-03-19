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
    protected String tag;
    protected StringResourceManager resourceManager;
    volatile protected TaskStatus taskStatus;
    Thread thread = null;

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

    static public Long getPercentRate(Long max) {
        BigDecimal decimal = BigDecimal.valueOf(max / 10);
        return decimal.toBigInteger().longValue();
    }

    /**
     * Пауза таска. Остановка произайдет не сразу, а через некоторое время из-за выполнение тяжелого процесса в другом потоке
     */
    public void pause() {
        if (thread != null) {
            setChanged();
            publishProgress(taskStatus.setStatus(TaskStatus.STATUS_PAUSED));

        }
    }

    /**
     * Возобновление таска. По Факту создастся новый поток
     */
    public void resume() {
        createTreadTask();
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
        setChanged();
        notifyObservers(taskStatus);
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
        setChanged();
        notifyObservers(taskStatus);
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
        onPostExecute(heavyTask());
    }
}

