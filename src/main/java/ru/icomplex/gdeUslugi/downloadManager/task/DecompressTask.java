package ru.icomplex.gdeUslugi.downloadManager.task;

import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DecompressTask extends Task {
    private String zipFile;
    private String location;

    public DecompressTask(StringResourceManager resourceManager, String key, String zipFile, String location) {
        this.resourceManager = resourceManager;
        this.zipFile = zipFile;
        this.taskStatus = new TaskStatus(key);
        this.location = location;
    }

    private void extractFolder(String zipFile, String location)
            throws IOException {
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = location;

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();
        int total = new ZipFile(zipFile).size();
//        downloader.unzipStart(key, " [" + String.valueOf(total) + " files]");
        // Process each entry
        int current = 0;
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            // destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(
                        zip.getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }

            if (currentEntry.endsWith(".zip")) {
                // found a zip file, try to open
                extractFolder(destFile.getAbsolutePath(),
                        zipFile.substring(0, zipFile.length() - 4));
            }
            current++;
            // Log.d(tag, "unzipped: " + String.valueOf(current));
            publishProgress(taskStatus);
        }
    }

    @Override
    TaskStatus heavyTask() {
        try {
            extractFolder(zipFile, location);
            return taskStatus;
        } catch (Exception e) {
            e.printStackTrace();
//            downloader.unzipCrush(key,
//                    e.getMessage());
            // Log.e("Decompress", "unzip", e);
        }
        return null;
    }

    @Override
    void publishProgress(TaskStatus taskStatus) {
        setChanged();
        notifyObservers(taskStatus);
    }

    @Override
    protected void onPostExecute(TaskStatus unused) {
        setChanged();
        notifyObservers(unused);
    }
}
