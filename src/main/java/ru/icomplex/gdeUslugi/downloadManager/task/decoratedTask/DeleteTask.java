package ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskAbstract;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskStatus;
import ru.icomplex.gdeUslugi.downloadManager.utilities.FileFly;

import java.io.File;

/**
 * User: artem
 * Date: 13.03.13
 * Time: 13:13
 * <p/>
 * Будем уметь удалять не только штучный файл, но и каталоги
 */
public class DeleteTask extends DecoratedTaskAbstract {

    /**
     * Рекурсивно удаляемся
     *
     * @param path
     * @return
     */
    long fileCount = 0;
    private String filePath;
    private long percentStorage;

    public DeleteTask(StringResourceManager resourceManager, String tag, TaskAbstract preExecutableTask, String filePath) {
        super(resourceManager, tag, preExecutableTask);
        this.filePath = filePath;
    }

    public void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    percentStorage -= 1;
                    file.delete();
                    taskStatus.setCurrent_progress(taskStatus.getCurrent_progress() + 1);
                    if (percentStorage <= 0) {
                        percentStorage = getPercentRate(fileCount);
                        publishProgress(taskStatus);
                    }

                }
            }
        }
        path.delete();
        percentStorage -= 1;
        taskStatus.setCurrent_progress(taskStatus.getCurrent_progress());
        if (fileCount <= 0) {
            percentStorage = getPercentRate(fileCount);
            publishProgress(taskStatus);
        }
    }

    private void delete(File path) {
        if (path.exists()) {
            if (path.isDirectory()) {
                deleteDirectory(path);
            } else {
                path.delete();
            }
        }
    }

    @Override
    protected TaskStatus currentHeavyTask() {
        taskStatus.setStatus(TaskStatus.STATUS_START);
        taskStatus.setMessage("delete " + getFileName(filePath));
        if (filePath == null) {
            taskStatus.setMessage(resourceManager.getErrorDelete());
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            return taskStatus;
        }
        File file = new File(filePath);
        if (file.exists()) {
            fileCount = new FileFly().fileFly(filePath);
        }
        taskStatus.setMax(fileCount);
        taskStatus.setCurrent_progress(0);
        publishProgress(taskStatus);

        if (fileCount > 0) {
            try {
                percentStorage = getPercentRate(fileCount);
                taskStatus.setStatus(TaskStatus.STATUS_WORKING);
                delete(file);
                taskStatus.setStatus(TaskStatus.STATUS_FINISH);
            } catch (Exception e) {
                taskStatus.setMessage(resourceManager.getErrorDelete());
            }
        }
        return taskStatus;
    }
}
