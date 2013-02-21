package ru.icomplex.gdeUslugi.downloadManager;

import ru.icomplex.gdeUslugi.downloadManager.manager.DownloadManager;

public class App {
    public static void main(String[] args) {
        String url = "http://gdekvartira.su/download/abakan_small_1361339108.zip";
        String path = "./Download";
        String md5 = "4f009498a7c937bd66b42d1e00b27db0";
        String size = "394079";
        DownloadManager manager = new DownloadManager();
        manager.start("Abakan", url, path, md5, size);

    }
}
