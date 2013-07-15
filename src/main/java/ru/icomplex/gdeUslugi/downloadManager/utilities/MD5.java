package ru.icomplex.gdeUslugi.downloadManager.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class MD5 {
    private final static char[] hexDigits = "0123456789abcdef".toCharArray();

    public static String Hashing(InputStream is) throws IOException,
            NoSuchAlgorithmException {
        byte[] bytes = new byte[4096];
        int read;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        while ((read = is.read(bytes)) != -1) {
            digest.update(bytes, 0, read);
        }

        byte[] messageDigest = digest.digest();

        StringBuilder sb = new StringBuilder(32);

        for (byte b : messageDigest) {
            sb.append(hexDigits[(b >> 4) & 0x0f]);
            sb.append(hexDigits[b & 0x0f]);
        }

        return sb.toString();
    }
}
