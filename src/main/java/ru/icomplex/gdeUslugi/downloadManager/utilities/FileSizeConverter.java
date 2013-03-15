package ru.icomplex.gdeUslugi.downloadManager.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: artem
 * Date: 04.03.13
 * Time: 14:17
 * Конвертор размеров файлов в кило и мега байты
 */
public abstract class FileSizeConverter {
    public static double longToMb(Long l) {
        return (double) l / 1024L / 1024L;
    }

    public static double longToKb(Long l) {
        return (double) l / 1024L / 1024L;
    }

    /**
     * Получаем размер в кб или в мегабайтах в зависимости от размера
     *
     * @param size исходный не округленный размер
     * @return верется строка округленная вверх с постфиксом величины
     */
    public static String getSize(long size) {
        BigDecimal scale = new BigDecimal(size);

        scale = scale.divide(new BigDecimal(1024));

        String postfix = "Kb";
        if (scale.longValue() > 1024) {
            scale = scale.divide(new BigDecimal(1024));
            postfix = "Mb";
        }
        return scale.setScale(1, RoundingMode.HALF_UP) + postfix;
    }
}
