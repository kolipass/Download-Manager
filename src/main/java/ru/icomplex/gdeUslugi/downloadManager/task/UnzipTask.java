package ru.icomplex.gdeUslugi.downloadManager.task;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.utilities.FolderDelelor;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * * <p/>
 * <p/>
 * Рекомендую использовать ru.icomplex.gdeUslugi.downloadManager.task.decoratedTask.UnzipTask
 */
@Deprecated

public class UnzipTask extends TaskAbstract {
    private static final int MAX_BUFFER_SIZE = 2048;
    private String zipFile;
    private String location;

    public UnzipTask(StringResourceManager resourceManager, String key, String zipFile, String location) {
        super(resourceManager, key);
        this.zipFile = zipFile;
        this.location = location;
    }

    private TaskStatus extractFolder(String zipFile, String location)
            throws IOException {
        if (zipFile == null || zipFile.isEmpty()) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getZipFilePaphIsEmpty());
            return taskStatus;
        }
        if (location == null || location.isEmpty()) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getSpecifiedLocalPathIsIncorrect());
            return taskStatus;
        }

        File file = new File(zipFile);

        ZipFile zip;
        try {
            zip = new ZipFile(file);
        } catch (IOException e) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getWriteError());
            e.printStackTrace();
            return taskStatus;
        }

        new File(location).mkdir();
        Enumeration zipFileEntries = zip.entries();

        int total = zip.size();
        taskStatus.setStatus(TaskStatus.STATUS_START);
        taskStatus.setMessage("unzip " + getFileName(zipFile) + " [" + String.valueOf(total) + "]");
        taskStatus.setMax(total);
        publishProgress(taskStatus);
        // Process each entry
        int current = 0;
        while (zipFileEntries.hasMoreElements()) {
            if (taskStatus.getStatus() == TaskStatus.STATUS_CANCELED || taskStatus.getStatus() == TaskStatus.STATUS_PAUSED || taskStatus.getStatus() == TaskStatus.STATUS_ERROR) {
                return taskStatus;
            }
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(location, currentEntry);

            // create the parent directory structure if needed
            destFile.getParentFile().mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is;
                try {
                    is = new BufferedInputStream(
                            zip.getInputStream(entry));
                } catch (IOException e) {
                    taskStatus.setStatus(TaskStatus.STATUS_ERROR);
                    taskStatus.setMessage(resourceManager.getWriteError());
                    e.printStackTrace();
                    return taskStatus;
                }
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[MAX_BUFFER_SIZE];

                // write the current file to disk
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(destFile);
                } catch (FileNotFoundException e) {
                    taskStatus.setStatus(TaskStatus.STATUS_ERROR);
                    taskStatus.setMessage(resourceManager.getWriteError());
                    e.printStackTrace();
                }
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        MAX_BUFFER_SIZE);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, MAX_BUFFER_SIZE)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }

//            if (currentEntry.endsWith(".zip")) {
//                // found a zip file, try to open
//                extractFolder(destFile.getAbsolutePath(),
//                        zipFile.substring(0, zipFile.length() - 4));
//            }

            taskStatus.setStatus(TaskStatus.STATUS_WORKING);
            taskStatus.setCurrent_progress(current++);
            publishProgress(taskStatus);
        }
        taskStatus.setStatus(TaskStatus.STATUS_FINISH);
        return taskStatus;
    }

    @Override
    public TaskStatus heavyTask() {
        if (zipFile == null || zipFile.isEmpty()) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getZipFilePaphIsEmpty());
            return taskStatus;
        }
        if (location == null || location.isEmpty()) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getZipFilePaphIsEmpty());
            return taskStatus;
        }
        try {
            return extractFolder(zipFile, location);
        } catch (Exception e) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getErrorUnzipping());

            //удалить недораспакованное
            FolderDelelor.deleteFolderRecursively(zipFile);

            e.printStackTrace();
            return taskStatus;
        }
    }
}
