package ru.icomplex.gdeUslugi.downloadManager.manager;

/**
 * Менеджер строковых ресурсов. Нужен для того. чтобы не таскать везде контекст.
 * Здесь будут обертки к функциям получения ресурса по
 */
public class StringResourceManager {
    public String getParseUrlError() {
        return "Could not be parsed as a URL";
    }

    public String getErrorOccurse() {
        return "error occurs while opening the connection.";
    }
    public String getErrorResponse() {
        return "Неполадки на сервере";
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
        return "Загружаемый файл поврежден, загрузка будет удлена. Попробуйте снова";
    }
}
