package ru.icomplex.gdeUslugi.downloadManager.utilities;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: artem
 * Date: 21.02.13
 * Time: 17:07
 * Рекурсивное удаление дирриктории и всего содержимого
 */
public final class FolderDelelor {
    public static boolean deleteFolderRecursively(String folderPath) {
        final File file = new File(folderPath);

        final File tempFile = new File(file.getAbsolutePath() + System.currentTimeMillis());
        file.renameTo(tempFile);
        return deleteDirectory(tempFile);

    }

    static private boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }
}
