package ru.icomplex.gdeUslugi.downloadManager;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.manager.TaskManager;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskAbstract;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskBuilder;
import ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask.DeleteTask;

public class App {
    public static void main(String[] args) {
        String url = "http://www.gdeuslugi.ru/files/export/mobiledb_moskva.zip";
        String path = "./Download/";
        String md5 = "26f7561bffba3cb03d50afe07bb8dc4b";
        Long size = 2106663L;
        TaskManager manager = TaskManager.getInstance();
        String tag = "Abakan";

        TaskAbstract download =
                TaskBuilder.downloadUnzip(TaskBuilder.getParams(new StringResourceManager(),
                        tag, path, url, md5, size, path + "unpack/", false));
        download = new DeleteTask(new StringResourceManager(), tag, download, path + "unpack/");
        manager.start(tag, download);

    }
}
