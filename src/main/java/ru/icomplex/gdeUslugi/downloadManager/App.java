package ru.icomplex.gdeUslugi.downloadManager;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.manager.TaskManager;
import ru.icomplex.gdeUslugi.downloadManager.task.DownloadAndUnzipTask;

public class App {
    public static void main(String[] args) {
        String url = "http://gdekvartira.su/download/abakan_small_1361339108.zip";
        String path = "./Download/";
        String md5 = "4f009498a7c937bd66b42d1e00b27db0";
        Long size = 394079L;
        TaskManager manager = new TaskManager();
        String tag = "Abakan";
        manager.start(tag, new DownloadAndUnzipTask(new StringResourceManager(), tag, path, url, md5, size, path+"unpack/"));

    }
}
