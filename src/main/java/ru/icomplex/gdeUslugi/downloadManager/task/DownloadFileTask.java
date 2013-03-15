package ru.icomplex.gdeUslugi.downloadManager.task;


import ru.icomplex.gdeUslugi.downloadManager.manager.StringResourceManager;
import ru.icomplex.gdeUslugi.downloadManager.utilities.MD5;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

/**
 * Основной класс загрузки данных
 * Поддерживается продолжение загрузки, вертификация файла по мд5 и заранее заданному точному размеру.
 */

public class DownloadFileTask extends TaskAbstract {
    private static final int MAX_BUFFER_SIZE = 1024;
    private String path;
    private String urlString;
    private String md5;
    private long size;
    // number of bytes downloaded
    private long downloaded;
    private String filePath;


    public DownloadFileTask(StringResourceManager resourceManager, String tag, String path, String url, String md5, Long size) {
        this.resourceManager = resourceManager;
        this.taskStatus = new TaskStatus(tag);
        this.path = path;
        this.urlString = url;
        this.md5 = md5 != null && !md5.isEmpty() ? md5 : "";
        this.size = size;

    }

    private long toKb(long size) {
        return size / 1024;
    }

    /**
     * Проверка на корректность имеющегося файла. За одно, если  файл есть, заполняется позиция начала загрузки.
     *
     * @param path     папка, где искать файл
     * @param fileName название папки
     * @return true если данный файл совпадает по хэшу с запрашиваым, иначе false. false так же вернется если файла нет
     */
    private boolean existingFileIsCorrect(String path, String fileName) {
        File dir = new File(path);
        if (dir.mkdirs() || dir.isDirectory()) {

            File localFile = new File(getFilePath(path, fileName));
            System.out.println(localFile.getAbsolutePath());
            if (localFile.exists()) {

                InputStream localFileStream;
                try {
                    localFileStream = new FileInputStream(getFilePath(path, fileName));

                    if (!md5.isEmpty() && md5.equals(MD5.Hashing(localFileStream))) {
                        //уже загружен
                        localFileStream.close();

                        return true;
                    } else {
                        if (size > localFile.length()) {
                            downloaded = localFile.length();
                        }
                    }

                } catch (NoSuchAlgorithmException e) {
                    //Проблемы в мд5
                } catch (IOException e) {
                    //фаил еще не был загружен, все норм.
                }
            }
        } else {
            //Создадим диррикторию
            try {
                Files.createDirectory(dir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    @Override
    public TaskStatus heavyTask() {
        String fileName = getFileName(urlString);
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getParseUrlError() + urlString);
            e.printStackTrace();
            return taskStatus;
        }

        //Если таску изменили состояние еще до начала работы, то выйти, иначе ставим состояние
        if (taskStatus.getStatus() != TaskStatus.STATUS_NONE && taskStatus.getStatus() != TaskStatus.STATUS_PAUSED && taskStatus.getStatus() != TaskStatus.STATUS_CANCELED) {
            return taskStatus;
        }

        taskStatus.setStatus(TaskStatus.STATUS_START);
        taskStatus.setMessage(fileName + " [" + String.valueOf(toKb(size)) + "Kb]");

        publishProgress(taskStatus);


        // проверка на наличие этого файла в уже загруженных
        if (existingFileIsCorrect(path, fileName)) {
            taskStatus.setStatus(TaskStatus.STATUS_FINISH);
            taskStatus.setMessage(resourceManager.getCorrectFileExist());
            return taskStatus;
        }

        // Open connection to URL.
        HttpURLConnection connection;
        //Это соединение нужно для того, чтобы знать полный размер файла
        URLConnection urlConnection;
        try {
            urlConnection = url.openConnection();
            urlConnection.connect();

            connection = (HttpURLConnection) url.openConnection();

            // Specify what portion of file to download.
            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
            // Connect to server.
            connection.connect();

            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                taskStatus.setStatus(TaskStatus.STATUS_ERROR);
                taskStatus.setMessage(resourceManager.getErrorResponse() + connection.getResponseCode());
                return taskStatus;
            }
        } catch (IOException e) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getErrorOccurse());
            e.printStackTrace();
            return taskStatus;
        }


        long lengthFileToDownload = connection.getContentLength();
        long lengthOfFile = urlConnection.getContentLength();

        if (lengthFileToDownload == 0 || (size > 0 && size < lengthFileToDownload)) {
//                Файл поврежден на сервере
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getFileCorruptedOnServer());
            return taskStatus;
        }

        RandomAccessFile file;
        try {
            file = new RandomAccessFile(getFilePath(path, fileName), "rw");
            file.seek(downloaded);
        } catch (FileNotFoundException e) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getWriteError());
            e.printStackTrace();
            return taskStatus;
        } catch (IOException e) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getWriteError());
            e.printStackTrace();
            return taskStatus;
        }

        InputStream stream;
        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getErrorOccurse());
            e.printStackTrace();
            return taskStatus;
        }

        //Сама загрузка
        try {
            taskStatus.setStatus(TaskStatus.STATUS_WORKING);
            taskStatus.setMax(lengthOfFile);
            taskStatus.setCurrent_progress(downloaded);

            while (taskStatus.getStatus() == TaskStatus.STATUS_WORKING) {
        /* Size buffer according to how much of the
           file is left to download. */

                byte buffer[];
                if (size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else if (size - downloaded > 0) {
                    buffer = new byte[(int) (size - downloaded)];
                } else {
                    break;
                }

                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1) {
                    break;
                }

                // Write buffer to file.
                file.write(buffer, 0, read);
                downloaded += read;
                taskStatus.setCurrent_progress(downloaded);
                publishProgress(taskStatus);
            }

            if (taskStatus.getStatus() == TaskStatus.STATUS_CANCELED || taskStatus.getStatus() == TaskStatus.STATUS_PAUSED || taskStatus.getStatus() == TaskStatus.STATUS_ERROR) {
                return taskStatus;
            }
            taskStatus.setStatus(TaskStatus.STATUS_FINISH);

            InputStream downloaded_file = new FileInputStream(getFilePath(path, fileName));
            if (md5.equals(MD5.Hashing(downloaded_file))) {
                downloaded_file.close();
                taskStatus.setStatus(TaskStatus.STATUS_FINISH);
                return taskStatus;
            } else {
                downloaded_file.close();
                File errorFile = new File(getFilePath(path, fileName));
                errorFile.delete();
                taskStatus.setStatus(TaskStatus.STATUS_ERROR);
                taskStatus.setMessage(resourceManager.getFileIsCorrupted());
                return taskStatus;
            }

        } catch (IOException e) {
            taskStatus.setStatus(TaskStatus.STATUS_ERROR);
            taskStatus.setMessage(resourceManager.getErrorOccurse());
            e.printStackTrace();
            return taskStatus;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            // Close file.
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return taskStatus;

    }

    private String getFilePath(String path, String fileName) {
        if (filePath == null || filePath.isEmpty()) {
            filePath = path + "/" + fileName;
        }
        return filePath;
    }

    /**
     * Путь до файла
     *
     * @return Возвращает строку-путь до файла (не гарантирует абсолютность)
     */

    public String getFilePath() {
        return (filePath == null || filePath.isEmpty()) ? getFilePath(path, getFileName(urlString)) : filePath;
    }
}