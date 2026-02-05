package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MD5 {

    public static String getFileMD5Checksum(String filePath) {
        byte[] buffer = new byte[4096];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(filePath)) {
                int numRead;
                while ((numRead = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, numRead);
                }
            }

            String checksum = bytesToHex(md.digest());
            System.out.println("MD5: " + checksum + " " + filePath);
            return checksum;
        } catch (IOException | NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String getListMD5Checksum(List<Integer> ints) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (int value : ints) {
                md.update((byte) value);
            }

            String checksum = bytesToHex(md.digest());
            System.out.println("MD5: " + checksum);
            return checksum;
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
