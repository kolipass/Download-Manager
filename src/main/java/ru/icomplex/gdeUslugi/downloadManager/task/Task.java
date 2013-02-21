package ru.icomplex.gdeUslugi.downloadManager.task;



import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;

import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: artem
 * Date: 09.11.12
 * Time: 18:59
 */
public abstract class Task extends Observable implements Runnable {
    private final String tag = getClass().getSimpleName();
    protected TaskStatus taskStatus;
    protected StringResourceManager resourceManager;

    // Pause this download.
    public void pause() {
        publishProgress(taskStatus.setStatus(TaskStatus.STATUS_PAUSED));
    }

    // Resume this download.
    public void resume() {
        createTreadTask();
    }

    // Cancel this download.
    public void cancel() {
        publishProgress(taskStatus.setStatus(TaskStatus.STATUS_CANCELED));
    }

    abstract void publishProgress(TaskStatus taskStatus);

    abstract TaskStatus heavyTask();

    abstract void onPostExecute(TaskStatus taskStatus);

    public void createTreadTask() {
        if (taskStatus.getStatus()!=TaskStatus.STATUS_WORKING)
        try {
            Thread thread = new Thread(this);
            Thread.sleep(200);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void run() {
        onPostExecute(heavyTask());
    }

}

