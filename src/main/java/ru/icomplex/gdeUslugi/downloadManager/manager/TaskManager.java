package ru.icomplex.gdeUslugi.downloadManager.manager;

import ru.icomplex.gdeUslugi.downloadManager.task.TaskAbstract;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskStatus;
import ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask.DecoratedTaskAbstract;

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
    private static volatile TaskManager instance;
    Map<String, TaskAbstract> taskMap;

    private TaskManager() {
        this.taskMap = new HashMap<String, TaskAbstract>();
    }

    public static TaskManager getInstance() {
        TaskManager localInstance = instance;
        if (localInstance == null) {
            synchronized (TaskManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TaskManager();
                }
            }
        }
        return localInstance;
    }

    /**
     * Запуст загрузки
     *
     * @param tag тег загрузки
     */
    public void start(String tag) {
        TaskAbstract task = taskMap.get(tag);
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
    public void start(String tag, TaskAbstract task) {
        if (!tag.isEmpty() && task != null) {
            if (taskMap != null) {
                if (!taskMap.containsKey(tag)) {
                    addTask(tag, task);
                }
                start(tag);
            }
        }
    }

    public void addTask(String tag, TaskAbstract task) {
        task.addObserver(this);
        taskMap.put(tag, task);
    }

    public void pause(String tag) {
        TaskAbstract task = getTask(tag);
        if (task != null) {
            task.pause();
        }
    }

    public TaskAbstract getTask(String tag) {
        TaskAbstract task = null;
        if (tag != null && !tag.isEmpty()) {
            task = taskMap.get(tag);
        }
        return task;
    }

    /**
     * Получить уровень таска
     *
     * @param tag
     * @return 0 если такого таска нет, если таск не декоррирован
     */
    public int getTaskMaxLevel(String tag) {
        int level = 0;
        TaskAbstract taskAbstract = getTask(tag);
        if (taskAbstract != null && taskAbstract instanceof DecoratedTaskAbstract) {
            DecoratedTaskAbstract task = (DecoratedTaskAbstract) taskAbstract;
            level = task.taskLevel();
        }
        return level;
    }

    public void resume(String tag) {
        TaskAbstract task = getTask(tag);
        if (task != null) {
            task.resume();
        }
    }

    public void cancel(String tag) {
        TaskAbstract task = getTask(tag);
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void update(Observable task, Object taskStatus) {
        if (task != null && task instanceof TaskAbstract) {
            System.out.println(taskStatus);
            setChanged();
            notifyObservers(taskStatus);
        }
    }
}
