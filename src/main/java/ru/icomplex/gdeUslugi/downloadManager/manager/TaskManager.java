package ru.icomplex.gdeUslugi.downloadManager.manager;

import ru.icomplex.gdeUslugi.downloadManager.task.Task;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * User: artem
 * Date: 19.02.13
 * Time: 12:03
 * Менеджер задач.
 */
public class TaskManager extends Observable implements Observer {
    Map<String, Task> taskMap;


    public TaskManager() {
        this.taskMap = new HashMap<>();
    }

    /**
     * Запуст загрузки
     *
     * @param tag тег загрузки
     */
    public void start(String tag) {
        Task task = taskMap.get(tag);
        if (task != null && task.getTaskStatus().getStatus() != TaskStatus.STATUS_WORKING) {
            task.createTreadTask();
        }
    }

    /**
     * Запуст загрузки
     *
     * @param tag  тег загрузки
     * @param task Задача, которую стартуем.
     */
    public void start(String tag, Task task) {
        if (!tag.isEmpty() && task != null) {
            if (taskMap != null) {
                if (!taskMap.containsKey(tag)) {
                    addTask(tag, task);
                }
                start(tag);
            }
        }
    }

    public void addTask(String tag, Task task) {
        task.addObserver(this);
        taskMap.put(tag, task);
    }

    public void pause(String tag) {
        Task task = getTask(tag);
        if (task != null) {
            task.pause();
        }
    }

    private Task getTask(String tag) {
        Task task = null;
        if (tag != null && !tag.isEmpty()) {
            task = taskMap.get(tag);
        }
        return task;
    }

    public void resume(String tag) {
        Task task = getTask(tag);
        if (task != null) {
            task.resume();
        }
    }

    public void cancel(String tag) {
        Task task = getTask(tag);
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        System.out.println(o);

        notifyObservers(o);
    }
}
