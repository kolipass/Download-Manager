package ru.icomplex.gdeUslugi.downloadManager.task;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask.DecoratedTaskAbstract;
import ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask.DeleteTask;
import ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask.DownloadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artem
 * Date: 15.03.13
 * Time: 11:06
 */
public class TaskBuilder {
    public static DecoratedTaskAbstract downloadUnzip(Map<String, Object> params) {
        DecoratedTaskAbstract taskAbstract = null;

        String url = (String) params.get("url");
        String path = (String) params.get("path");
        String md5 = (String) params.get("md5");
        Long size = (Long) params.get("size");
        String tag = (String) params.get("tag");
        StringResourceManager resourceManager = (StringResourceManager) params.get("stringResourceManager");

        try {

            taskAbstract = new DownloadTask(resourceManager, tag, null, path, url, md5, size);

            String zipFile = ((DownloadTask) taskAbstract).getFilePath();
            String unpackingCatalog = (String) params.get("unpackingCatalog");

            taskAbstract = new ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask.UnzipTask(resourceManager, tag, taskAbstract, zipFile, unpackingCatalog);

            if ((Boolean) params.get("deleteAfter")) {
                taskAbstract = new DeleteTask(resourceManager, tag, taskAbstract, zipFile);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return taskAbstract;
    }

    public static Map<String, Object> getParams(StringResourceManager resourceManager, String tag, String path, String url, String md5,
                                                Long size, String unpackingCatalog, Boolean deleteAfter) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("stringResourceManager", resourceManager);
        params.put("url", url);
        params.put("path", path);
        params.put("md5", md5);
        params.put("size", size);
        params.put("tag", tag);
        params.put("unpackingCatalog", unpackingCatalog);
        params.put("deleteAfter", deleteAfter);

        return params;
    }
}
