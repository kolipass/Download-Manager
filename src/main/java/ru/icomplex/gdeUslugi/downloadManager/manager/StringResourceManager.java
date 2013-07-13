package ru.icomplex.gdeUslugi.downloadManager.manager;

/**
 * Менеджер строковых ресурсов. Нужен для того. чтобы не таскать везде контекст.
 * Здесь будут обертки к функциям получения ресурса по
 */
public class StringResourceManager {
    public String getParseUrlError() {
        return "Не может быть разобрано как адрес:";
    }

    public String getErrorOccurs() {
        return "ошибка возникает при открытии соединения.";
    }

    public String getErrorResponse() {
        return "Неполадки на сервере: ";
    }
    public String getInternetError() {
        return "Проблемы с доступом к сети";
    }

    public String getErrorUnzipping() {
        return "Произошла ошибка разпаковки. Попробуйте снова";
    }

    public String getErrorDelete() {
        return "Произошла ошибка удаления. Попробуйте снова";
    }

    public String getErrorDb() {
        return "Произошла ошибка работы с БД. Попробуйте снова";
    }

    public String getCorrectFileExist() {
        return "Запрашиваемый файл уже загружен";
    }

    public String getWriteError() {
        return "Проблемы записи на диск";
    }

    public String getFileCorruptedOnServer() {
        return "Файл поврежден на сервере: ";
    }

    public String getFileIsCorrupted() {
        return "Загружаемый файл поврежден(MD5 не совпадают), загрузка будет удлена. Попробуйте снова";
    }

    public String getZipFilePaphIsEmpty() {
        return "Путь к зип файлу не корректен: ";
    }

    public String getSpecifiedLocalPathIsIncorrect() {
        return "Указанный путь не корректен";
    }

    public String getServerError() {
        return "Профелактические работы на сервере. Пожалуйста повторите позднее";
    }
}
