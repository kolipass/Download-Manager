package ru.icomplex.gdeUslugi.downloadManager.manager;

import ru.icomplex.gdeUslugi.downloadManager.task.DownloadFileTask;
import ru.icomplex.gdeUslugi.downloadManager.task.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * User: artem
 * Date: 19.02.13
 * Time: 12:03
 * Менеджер загрузок.
 */
public class DownloadManager implements Observer {
    Map<String, Task> taskMap;


    public DownloadManager() {
        this.taskMap = new HashMap<>();
    }

    /**
     * Запуст загрузки
     *
     * @param tag  тег загрузки
     * @param url  урл загрузки
     * @param path путь
     * @param md5  хэш
     * @param size размер
     */
    public void start(String tag, String url, String path, String md5, String size) {
        if (taskMap != null && !taskMap.containsKey(tag)) {
            Task task = new DownloadFileTask(new StringResourceManager(), tag, path, url, md5, size);
            task.addObserver(this);
            taskMap.put(tag, task);
            task.createTreadTask();


            try {
                Thread.sleep(2000);
                System.out.println("change status pause");
                task.pause();

                Thread.sleep(2000);
                System.out.println("change status resume");
                task.resume();

                Thread.sleep(2000);
                System.out.println("change status cancel");
                task.cancel();

                Thread.sleep(2000);
                System.out.println("change status resume");
                task.resume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        System.out.println(o);
    }
}
