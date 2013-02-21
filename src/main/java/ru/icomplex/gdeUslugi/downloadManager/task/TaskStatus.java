package ru.icomplex.gdeUslugi.downloadManager.task;

/**
 * Модель описания текущего статуса данной задачи
 */
public class TaskStatus {
    public final static int STATUS_START = 1;
    public final static int STATUS_WORKING = 2;
    public final static int STATUS_PAUSED = 3;
    public final static int STATUS_FINISH = 4;
    public final static int STATUS_ERROR = -1;
    public final static int STATUS_CANCELED = -2;
    public final static int STATUS_NONE = 0;
    private Integer status = 0;
    private String message;
    /**
     * Индификатор задачи
     */
    private String key;
    private long max = 0;
    private long current_progress = 0;

    TaskStatus(int status, String message, String key, long max, long current_progress) {

        this.status = status;
        this.message = message;
        this.key = key;
        this.max = max;
        this.current_progress = current_progress;
    }

    public TaskStatus(String key) {
        this.key = key;

        this.status = STATUS_NONE;
        this.message = "";
        this.max = 0;
        this.current_progress = 0;
    }

    public static String statusToString(int status) {
        switch (status) {
            case STATUS_START: {
                return "STATUS_START";
            }
            case STATUS_WORKING: {
                return "STATUS_WORKING";
            }
            case STATUS_FINISH: {
                return "STATUS_FINISH";
            }
            case STATUS_PAUSED: {
                return "STATUS_PAUSED";
            }
            case STATUS_CANCELED: {
                return "STATUS_CANCELED";
            }
            case STATUS_ERROR: {
                return "STATUS_ERROR";
            }
            case STATUS_NONE: {
                return "STATUS_NONE";
            }

            default:
                return "none";
        }
    }

    public int getStatus() {
        return status;
    }

    /**
     * Устанавливает статус. Изменить статус извне после финиша нельзя.
     *
     * @param status устанавливаемый статус
     * @return возвращает объект
     */

    public TaskStatus setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getCurrent_progress() {
        return current_progress;
    }

    public void setCurrent_progress(long current_progress) {
        this.current_progress = current_progress;
    }

    @Override
    public String toString() {
        return "TaskStatus{" +
                "status=" + statusToString(status) +
                ", message='" + message + '\'' +
                ", key='" + key + '\'' +
                ", max=" + max +
                ", current_progress=" + current_progress +
                '}';
    }
}
