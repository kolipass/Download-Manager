package ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskAbstract;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskStatus;

import java.util.Observable;
import java.util.Observer;

/**
 * User: artem
 * Date: 15.03.13
 * Time: 10:03
 * <p/>
 * Динамически наделяемый новыми свойствами таск. Последовательное выполнение свойств
 */
public abstract class PreExecutableTaskDecoratorAbstract extends TaskAbstract implements Observer {
    TaskAbstract preExecutableTask;

    protected PreExecutableTaskDecoratorAbstract(StringResourceManager resourceManager, String tag, TaskAbstract preExecutableTask) {
        super(resourceManager, tag);
        this.preExecutableTask = preExecutableTask;
    }

    /**
     * Тут будет выполняться задача пеализующего класса
     *
     * @return
     */
   protected abstract TaskStatus currentHeavyTask();

    /**
     * Сначало выполняем поставленную задачу, если она есть. Если ее нет, или она выполнилась корректно, то выполняем свою задачу
     *
     * @return
     */

    @Override
    public final TaskStatus heavyTask() {
        if (preExecutableTask != null) {
            preExecutableTask.addObserver(this);
            publishProgress(preExecutableTask.heavyTask());
            this.taskStatus = preExecutableTask.getTaskStatus();
            if (!taskStatus.isCorrectComplate()) {
                return taskStatus;
            }
        }
        return currentHeavyTask();

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
