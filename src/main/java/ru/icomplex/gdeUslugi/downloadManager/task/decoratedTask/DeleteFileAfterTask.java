package ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskAbstract;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskStatus;

import java.io.File;

/**
 * User: artem
 * Date: 13.03.13
 * Time: 13:13
 */
public class DeleteFileAfterTask extends PreExecutableTaskDecoratorAbstract {

    private String filePath;

    public DeleteFileAfterTask(StringResourceManager resourceManager, String tag, TaskAbstract preExecutableTask, String filePath) {
        super(resourceManager, tag, preExecutableTask);
        this.filePath = filePath;
    }


    @Override
    protected TaskStatus currentHeavyTask() {
        taskStatus.setStatus(TaskStatus.STATUS_START);
        taskStatus.setMessage("delete " + getFileName(filePath));

        File errorFile = new File(filePath);
        if (errorFile.isFile()) {
            taskStatus.setMax(1);
            taskStatus.setCurrent_progress(0);
            publishProgress(taskStatus);
        }

        if (filePath != null && !filePath.isEmpty() && errorFile.delete()) {
            taskStatus.setCurrent_progress(1);
            taskStatus.setStatus(TaskStatus.STATUS_FINISH);
        } else {
            taskStatus.setMessage(resourceManager.getErrorDelete());
        }
        return taskStatus;
    }
}
