package util;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MD5 {

    public static String getFileMD5Checksum(String filePath) {
        byte[] buffer = new byte[1024];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(filePath);

            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    md.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            fis.close();
            return bytesToHex(md.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            return "";
        }
    }
    public static String getListMD5Checksum(List<Integer> ints) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (int i : ints) {
                md.update((byte) i);
            }
            return bytesToHex(md.digest());
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
