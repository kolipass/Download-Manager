package ru.icomplex.gdeUslugi.downloadManager.task;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;

import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: artem
 * Date: 27.02.13
 * Time: 13:47
 * Таск, который загружает, а потом распоковывает
 */
public class DownloadAndUnzipTask extends TaskAbstract implements Observer {
    //Текущая задача
    TaskAbstract currentTask = null;
    private String path;
    private String url;
    private String md5;
    private Long size;
    private String tag;
    private String unpackingCatalog;


    public DownloadAndUnzipTask(StringResourceManager resourceManager, String tag, String path, String url, String md5,
                                Long size, String unpackingCatalog) {
        this.resourceManager = resourceManager;
        this.tag = tag;
        this.taskStatus = new TaskStatus(tag);
        this.path = path;
        this.url = url;
        this.md5 = md5 != null && !md5.isEmpty() ? md5 : "";
        this.size = size;

        this.unpackingCatalog = unpackingCatalog;

    }

    public TaskStatus getTaskStatus() {
        return currentTask != null ? currentTask.getTaskStatus() : taskStatus;
    }

    @Override
    public void pause() {
        setChanged();
        if (currentTask != null) {
            currentTask.pause();
            publishProgress(taskStatus.setStatus(TaskStatus.STATUS_PAUSED));
        }
    }

    @Override
    public void resume() {
        if (currentTask != null) {
            currentTask.resume();
        }
    }

    @Override
    public void cancel() {
        if (currentTask != null) {
            currentTask.cancel();
        }
        publishProgress(taskStatus.setStatus(TaskStatus.STATUS_CANCELED));
    }

    @Override
    TaskStatus heavyTask() {
        return downloadAndUnzip();
    }

    TaskStatus downloadAndUnzip() {
        currentTask = new DownloadFileTask(resourceManager, tag, path, url, md5, size);
        currentTask.addObserver(this);

        publishProgress(currentTask.heavyTask());

        if (currentTask.getTaskStatus().getStatus() == TaskStatus.STATUS_FINISH) {
            String zipLocation = ((DownloadFileTask) currentTask).getFilePath();
            currentTask = new UnzipTask(resourceManager, tag, zipLocation, unpackingCatalog);
            currentTask.addObserver(this);
            publishProgress(currentTask.heavyTask());
        }
        return currentTask.getTaskStatus();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof TaskAbstract) {
            if (arg instanceof TaskStatus) {
                try {
                    taskStatus = (TaskStatus) arg;
                    publishProgress(taskStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
