package ru.icomplex.gdeUslugi.downloadManager.task;


import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;

import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: artem
 * Date: 09.11.12
 * Родоначальник тасков. Тяжелая задача будет выполнена в новом потоке. Таск генерирует события.
 */
public abstract class TaskAbstract extends Observable implements Runnable {
    private final String tag = getClass().getSimpleName();
    protected TaskStatus taskStatus;
    protected StringResourceManager resourceManager;

    /**
     * Пауза таска. Остановка произайдет не сразу, а через некоторое время из-за выполнение тяжелого процесса в другом потоке
     */
    public void pause() {
        setChanged();
        publishProgress(taskStatus.setStatus(TaskStatus.STATUS_PAUSED));
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
    abstract TaskStatus heavyTask();

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
                Thread thread = new Thread(this);
                Thread.sleep(200);
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
    public final void run() {
        onPostExecute(heavyTask());
    }

    /**
     * Получим имя файла
     *
     * @param url путь из которого выудим последнюю часть после /
     * @return верну строку, как раз такую, как надо)
     */

    String getFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
