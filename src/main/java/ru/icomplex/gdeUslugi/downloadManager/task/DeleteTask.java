package ru.icomplex.gdeUslugi.downloadManager.task;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;

import java.io.File;

/**
 * User: artem
 * Date: 13.03.13
 * Time: 13:13
 * <p/>
 * Рекомендую использовать ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask.DeleteTask
 */
@Deprecated
public class DeleteTask extends TaskAbstract {

    private String filePath;

    protected DeleteTask(StringResourceManager resourceManager, String tag, String filePath) {
        super(resourceManager, tag);
        this.filePath = filePath;
    }

    @Override
    public TaskStatus heavyTask() {
        taskStatus.setStatus(TaskStatus.STATUS_START);
        taskStatus.setMessage("delete " + getFileName(filePath));

        publishProgress(taskStatus);

        File errorFile = new File(filePath);
        if (filePath != null && !filePath.isEmpty() && errorFile.delete()) {
            taskStatus.setStatus(TaskStatus.STATUS_FINISH);
        } else {
            taskStatus.setMessage(resourceManager.getErrorDelete());
        }
        return taskStatus;
    }
}
