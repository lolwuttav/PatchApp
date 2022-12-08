package util;

import java.io.*;
import java.util.*;

public class FileUtil {

    public static final String PATCH_FILE_EXTENSION = ".ubp";
    private static String MD51;
    private static String MD52;

    public static void applyPatchToFile(String file1, String file2) {

        Map<Integer, Integer> patch = readMapFromFile(file2);

        List<Integer> file = readBytesFromFile(file1);

        MD51 = MD5.getFileMD5Checksum(file1);
        MD52 = MD5.getFileMD5Checksum(file2);

        try {
            applyPatchToList(file, patch);
        } catch (NullPointerException e) {
            System.out.println("Patch failed, aborting");
            System.out.println(e.getStackTrace().toString());
            return;
        }

        String[] parts = file1.split("\\.");
        String part1 = parts[0];
        String part2 = parts[1];
        writeListToFile(file, part1 + "-patched" + "." + part2);
    }


    public static void generatePatchFile(String file1, String file2) {
        try {
            // Open the files
            FileInputStream fis1 = new FileInputStream(file1);
            FileInputStream fis2 = new FileInputStream(file2);

            MD51 = MD5.getFileMD5Checksum(file1);
            MD52 = MD5.getFileMD5Checksum(file2);

            // Get the MD5 checksums
            if(MD51.equals(MD52)) {
                System.out.println("The files are the same, no patch will be generated.");
                return;
            }

            List<Integer> bytes1 = readBytesFromFile(file1);
            List<Integer> bytes2 = readBytesFromFile(file2);


            Map<Integer, Integer> differentBytes = compareLists(bytes1, bytes2);

            //Save the different bytes map to a file
            writeMapToFile(differentBytes, file1 + PATCH_FILE_EXTENSION);
            System.out.println(new Date().toString() + " Patch file saved to " + file1 + PATCH_FILE_EXTENSION);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, Integer> compareLists(List<Integer> list1, List<Integer> list2) {
        Map<Integer, Integer> differentBytes = new HashMap<>();

        int longerListLength = Math.max(list1.size(), list2.size());

        // Compare the elements in the lists and add the different elements to the HashMap
        for (int i = 0; i < longerListLength; i++) {
            if (i < list1.size() && i < list2.size()) {
                if (!list1.get(i).equals(list2.get(i))) {
                    differentBytes.put(i, list2.get(i));
                }
            }
            if (i >= list1.size()) {
                differentBytes.put(i, list2.get(i));
            }
            if (i >= list2.size()) {
                differentBytes.put(i, list1.get(i));
            }
        }

        differentBytes.put(-1, longerListLength);

        // Add the checksum of the list to the HashMap
        addIntListToMap(toIntList(MD5.getListMD5Checksum(list2)), differentBytes);

        return differentBytes;
    }

    public static List<Integer> readBytesFromFile(String fileName) {
        List<Integer> bytes = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(fileName);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            int byte1 = bis.read();
            while (byte1 != -1) {
                bytes.add(byte1);
                byte1 = bis.read();
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return bytes;
    }
    public static void applyPatchToList(List<Integer> list, Map<Integer, Integer> patchHashMap) {


        //get method name
        int maxKey = patchHashMap.get(-1);
        System.out.println("Max key: " + maxKey + " LargestList: " + patchHashMap.get(-1));
        if(list.size() > maxKey) {
            int difference = list.size() - maxKey;
            System.out.println(new Date().toString() + " Bytes to delete: " + difference);
            for(int i = list.size() - 1; i > maxKey; i--) {
                list.remove(i);
            }
        }
        if(list.size() < maxKey) {
            //get difference between list size and max key
            int difference = maxKey - list.size();
            System.out.println(new Date().toString() + " Bytes to add: " + difference);
            for(int i = 0; i < difference; i++) {
                list.add(0);
            }
        }
        // Iterate over the keys in the HashMap
        for (int key : patchHashMap.keySet()) {
            // Get the value associated with the key (the new value to use)
            int value = patchHashMap.get(key);

            if (key >= 0 && key < list.size()) {
                list.set(key, value);
            }
        }


        // Return the modified list
        //compare the md5 checksums of the patched list and patchHashMap
        String MD5Patched = MD5.getListMD5Checksum(list);
        String MD5Patch = getStringFromMap(patchHashMap);

        if(MD5Patched.equals(MD5Patch)) {
            System.out.println("MD5 checksums match orignal file");
        } else {
            System.out.println("MD5 checksums do not match");
        }
        return;


    }

    public static void writeMapToFile(Map<Integer, Integer> map, String filePath) {
        try {
            // Create a FileOutputStream to save the serialized map to a file
            FileOutputStream fos = new FileOutputStream(filePath);

            // Create an ObjectOutputStream to serialize the map
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Write the map to the ObjectOutputStream
            oos.writeObject(map);

            // Close the streams
            oos.close();
            fos.close();
        } catch (IOException e) {
            System.out.println("Error saving map to file: " + e.getMessage());
        }
    }
    public static Map<Integer, Integer> readMapFromFile(String filePath) {
        Map<Integer, Integer> map = null;

        try {
            FileInputStream fis = new FileInputStream(filePath);

            ObjectInputStream ois = new ObjectInputStream(fis);

            // Read the map from the ObjectInputStream
            map = (Map<Integer, Integer>) ois.readObject();

            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading map from file: " + e.getMessage());
        }


        return map;
    }


    public static void writeListToFile(List<Integer> list, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            for (int i : list) {
                bos.write(i);
            }
        } catch (IOException e) {
            System.out.println("Error writing list to file: " + e.getMessage());
        }
    }

    public static void addIntListToMap(List<Integer> list, Map<Integer, Integer> map) {
        int index = -2;
        for (int n : list) {
            map.put(index, n);
            index--;
        }
    }

    public static String getStringFromMap(Map<Integer, Integer> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getKey() < -1) {
                sb.append(new String(Character.toChars(entry.getValue())));
            }
        }
        return sb.toString();
    }

    public static List<Integer> toIntList(String str) {
        List<Integer> result = new ArrayList<>();
        for (char c : str.toCharArray()) {
            result.add((int) c);
        }
        return result;
    }

    public static String fromIntList(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int n : list) {
            sb.append(new String(Character.toChars(n)));
        }
        return sb.toString();
    }



}
