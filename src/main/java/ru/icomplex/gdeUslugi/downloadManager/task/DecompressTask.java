package ru.icomplex.gdeUslugi.downloadManager.task;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DecompressTask extends Task {

    private String zipFile;
    private String location;
    private String key;


    public DecompressTask(String key, String zipFile, String location) {
        this.key = key;
        this.zipFile = zipFile;
        this.location = location;
        _dirChecker("");
    }

    private void _dirChecker(String dir) {
        File f = new File(location + dir);

        if (f.isDirectory()) {
            f.mkdirs();
        }
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
            publishProgress(current);
        }
    }

    protected void publishProgress(int progress) {
//        downloader.progressUpdate(key, progress);
    }

    @Override
    void publishProgress(TaskStatus taskStatus) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    TaskStatus heavyTask() {
        try {
            extractFolder(zipFile, location);
//            return zipFile;
        } catch (Exception e)
        {
            e.printStackTrace();
//            downloader.unzipCrush(key,
//                    e.getMessage());
            // Log.e("Decompress", "unzip", e);
        }
        return null;
    }

    @Override
    void onPostExecute(TaskStatus taskStatus) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void onPostExecute(String unused) {
//        Log.d(tag, "onPostExecute" + String.valueOf(unused));
        if (unused != null) {
//            downloader.unzipComplete(key, unused);
        }
    }

    public void broke() {
//        Log.d(tag, "cancel " + this.setCancel(true));
    }
}
