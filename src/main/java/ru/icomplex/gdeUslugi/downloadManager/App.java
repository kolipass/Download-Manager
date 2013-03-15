package ru.icomplex.gdeUslugi.downloadManager;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.manager.TaskManager;
import ru.icomplex.gdeUslugi.downloadManager.task.TaskBuilder;

public class App {
    public static void main(String[] args) {
        String url = "http://www.gdeuslugi.ic/files/export/mobiledb_moskva.zip";
        String path = "./Download/";
        String md5 = "c3cc846b76b28d7e41ea5f538fedc808";
        Long size = 6370305L;
        TaskManager manager = TaskManager.getInstance();
        String tag = "Abakan";


//        TaskAbstract download = new DownloadFileTask(new StringResourceManager(), tag, path, url, md5, size);
//        String zipFile = ((DownloadFileTask) download).getFilePath();
//
//        download = new UnzipAfterTask(download, new StringResourceManager(), tag, zipFile, path + "unpack/");
//        download = new DeleteFileAfterTask(download, zipFile, tag);
//
        manager.start(tag, TaskBuilder.downloadUnzip(TaskBuilder.getParams(new StringResourceManager(),
                tag, path, url, md5, size, path + "unpack/", true)));

    }
}
