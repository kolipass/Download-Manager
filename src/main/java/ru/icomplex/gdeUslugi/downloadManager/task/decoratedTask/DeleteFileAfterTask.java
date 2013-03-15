package ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask;

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

    public DeleteFileAfterTask(TaskAbstract afterTask, String filePath, String key) {
        super(afterTask);
        this.filePath = filePath;
        this.taskStatus = new TaskStatus(key);
    }

    @Override
    TaskStatus currentHeavyTask() {
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
