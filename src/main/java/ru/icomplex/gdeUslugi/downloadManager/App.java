package ru.icomplex.gdeUslugi.downloadManager;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.manager.TaskManager;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskAbstract;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskBuilder;
import ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask.DeleteFilesAfterTask;

public class App {
    public static void main(String[] args) {
        String url = "http://www.gdeuslugi.ic/files/export/mobiledb_moskva.zip";
        String path = "./Download/";
        String md5 = "8bb87bf7ab4372b55bc78b0bcfac1507";
        Long size = 6370357L;
        TaskManager manager = TaskManager.getInstance();
        String tag = "Abakan";

        TaskAbstract download =
                TaskBuilder.downloadUnzip(TaskBuilder.getParams(new StringResourceManager(),
                        tag, path, url, md5, size, path + "unpack/", false));
        download = new DeleteFilesAfterTask(new StringResourceManager(), tag, download, path + "unpack/");
        manager.start(tag, download);

    }
}
