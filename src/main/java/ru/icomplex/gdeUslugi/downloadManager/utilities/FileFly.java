package ru.icomplex.gdeUslugi.downloadManager.utilities;

import java.io.File;

/**
 * Подсчитываем колличество файлов в каталоге рекурсивно
 */

public class FileFly {

    private int c = 0;

    public int fileFly(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] s = file.listFiles();
                if (s != null) {
                    for (File value : s) {
                        if (value.isDirectory())
                            fileFly(value.getPath());
                        c++;
                    }
                }
            } else {
                c++;
            }
        }
        return c;
    }        /**
     * Получим имя файла
     *
     * @param url путь из которого выудим последнюю часть после /
     * @return верну строку, как раз такую, как надо)
     */

    static public String getFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}