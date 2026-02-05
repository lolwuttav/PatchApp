package util;

import java.io.*;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class FileUtil {

    public static final String PATCH_FILE_EXTENSION = ".ubp";
    private static final int KEY_TARGET_SIZE = -1;
    private static final int KEY_CHECKSUM_START = -2;

    private static final Timer timer = new Timer();
    private static final Timer compareTimer = new Timer();

    public static void applyPatchToFile(String sourceFile, String patchFile) {
        Map<Integer, Integer> patch = decompressMap(patchFile);
        if (patch == null || patch.isEmpty()) {
            System.out.println("Patch is empty or unreadable, aborting");
            return;
        }

        List<Integer> sourceBytes = readBytesFromFile(sourceFile);

        timer.start("Getting MD5 of files");
        MD5.getFileMD5Checksum(sourceFile);
        MD5.getFileMD5Checksum(patchFile);
        timer.stop();

        timer.start("Applying patch to data");
        if (!applyPatchToList(sourceBytes, patch)) {
            System.out.println("Error applying patch, aborting");
            return;
        }
        timer.stop();

        String outputFile = buildPatchedFilePath(sourceFile);
        timer.start("Writing patched file");
        writeListToFile(sourceBytes, outputFile);
        timer.stop();
    }

    public static void generatePatchFile(String sourceFile, String targetFile) {
        timer.start("Getting MD5 of files");
        String sourceMd5 = MD5.getFileMD5Checksum(sourceFile);
        String targetMd5 = MD5.getFileMD5Checksum(targetFile);
        timer.stop();

        if (sourceMd5.equals(targetMd5)) {
            System.out.println("The files are the same, no patch will be generated.");
            return;
        }

        timer.start("Reading files into memory");
        List<Integer> sourceBytes = readBytesFromFile(sourceFile);
        List<Integer> targetBytes = readBytesFromFile(targetFile);
        timer.stop();

        timer.start("Generating patch");
        Map<Integer, Integer> differentBytes = compareLists(sourceBytes, targetBytes);
        timer.stop();

        timer.start("Saving patch to file");
        compressMap(differentBytes, sourceFile + PATCH_FILE_EXTENSION);
        timer.stop();
    }

    public static Map<Integer, Integer> compareLists(List<Integer> source, List<Integer> target) {
        Map<Integer, Integer> differentBytes = new HashMap<>();
        int longerListLength = Math.max(source.size(), target.size());

        compareTimer.start("Comparing lists");
        for (int i = 0; i < longerListLength; i++) {
            Integer sourceByte = i < source.size() ? source.get(i) : null;
            Integer targetByte = i < target.size() ? target.get(i) : null;

            if (!Objects.equals(sourceByte, targetByte)) {
                differentBytes.put(i, targetByte == null ? 0 : targetByte);
            }
        }
        differentBytes.put(KEY_TARGET_SIZE, target.size());
        compareTimer.stop();

        compareTimer.start("Combining data");
        addChecksumToMap(MD5.getListMD5Checksum(target), differentBytes);
        compareTimer.stop();
        return differentBytes;
    }

    public static List<Integer> readBytesFromFile(String fileName) {
        List<Integer> bytes = new ArrayList<>();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName))) {
            int value;
            while ((value = bis.read()) != -1) {
                bytes.add(value);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return bytes;
    }

    public static boolean applyPatchToList(List<Integer> list, Map<Integer, Integer> patchHashMap) {
        Integer targetSize = patchHashMap.get(KEY_TARGET_SIZE);
        if (targetSize == null || targetSize < 0) {
            return false;
        }

        resizeList(list, targetSize);

        for (Map.Entry<Integer, Integer> entry : patchHashMap.entrySet()) {
            int index = entry.getKey();
            if (index >= 0 && index < list.size()) {
                list.set(index, entry.getValue());
            }
        }

        String patchedMd5 = MD5.getListMD5Checksum(list);
        String expectedMd5 = readChecksumFromMap(patchHashMap);
        if (patchedMd5.equals(expectedMd5)) {
            System.out.println("MD5 checksums match original file");
            return true;
        }

        System.out.println("MD5 checksums do not match");
        return false;
    }

    public static void writeListToFile(List<Integer> list, String filePath) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            for (int i : list) {
                bos.write(i);
            }
        } catch (IOException e) {
            System.out.println("Error writing list to file: " + e.getMessage());
        }
    }

    public static void compressMap(Map<Integer, Integer> map, String outputFileName) {
        try (OutputStream outputStream = new FileOutputStream(outputFileName);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(bufferedOutputStream, new Deflater());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(deflaterOutputStream)) {

            System.out.println("Size of map: " + map.size() * 4 / 1024 / 1024 + " MB");
            objectOutputStream.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, Integer> decompressMap(String inputFileName) {
        try (InputStream inputStream = new FileInputStream(inputFileName);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             InflaterInputStream inflaterInputStream = new InflaterInputStream(bufferedInputStream, new Inflater());
             ObjectInputStream objectInputStream = new ObjectInputStream(inflaterInputStream)) {

            Object raw = objectInputStream.readObject();
            if (raw instanceof Map<?, ?> rawMap) {
                Map<Integer, Integer> converted = new HashMap<>();
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    if (entry.getKey() instanceof Integer key && entry.getValue() instanceof Integer value) {
                        converted.put(key, value);
                    }
                }
                return converted;
            }
            return Collections.emptyMap();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private static void resizeList(List<Integer> list, int targetSize) {
        while (list.size() > targetSize) {
            list.remove(list.size() - 1);
        }
        while (list.size() < targetSize) {
            list.add(0);
        }
    }

    private static void addChecksumToMap(String checksum, Map<Integer, Integer> map) {
        int index = KEY_CHECKSUM_START;
        for (char c : checksum.toCharArray()) {
            map.put(index, (int) c);
            index--;
        }
    }

    private static String readChecksumFromMap(Map<Integer, Integer> map) {
        StringBuilder sb = new StringBuilder();
        int key = KEY_CHECKSUM_START;
        while (map.containsKey(key)) {
            sb.append((char) map.get(key).intValue());
            key--;
        }
        return sb.toString();
    }

    private static String buildPatchedFilePath(String sourceFile) {
        int lastDot = sourceFile.lastIndexOf('.');
        if (lastDot > 0) {
            return sourceFile.substring(0, lastDot) + "-patched" + sourceFile.substring(lastDot);
        }
        return sourceFile + "-patched";
    }
}
