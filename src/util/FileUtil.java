package util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import util.LogUtil;

public class FileUtil {

    public static void applyPatchToFile(String file1, String file2) {
        System.out.println( new Date().toString() + "Loading patch file...");
        Map<Integer, Integer> patch = readMapFromFile(file2);
        System.out.println(new Date().toString() + "Loading file to patch...");
        List<Integer> file = readBytesFromFile(file1);

        applyPatchToList(file, patch);
        writeListToFile(file, file1+".patched");
    }


    public static void generatePatchFile(String file1, String file2) {
        try {
            // Open the files
            FileInputStream fis1 = new FileInputStream(file1);
            FileInputStream fis2 = new FileInputStream(file2);

            System.out.println(new Date().toString() + " A patch file will be generated.");

            // Create arrays to store the bytes from each file
            System.out.println(new Date().toString() + " Loading files...");
            List<Integer> bytes1 = readBytesFromFile(file1);
            System.out.println(new Date().toString() + " File 1 loaded.");
            List<Integer> bytes2 = readBytesFromFile(file2);
            System.out.println(new Date().toString() + " File 2 loaded.");



            // Create a HashMap to store the different bytes
            System.out.println(new Date().toString() + " Comparing files...");
            Map<Integer, Integer> differentBytes = compareLists(bytes1, bytes2);
            System.out.println(new Date().toString() + " Files compared.");
            // Print the different bytes
            //System.out.println("Different bytes: " + differentBytes);


            //Save the different bytes map to a file
            System.out.println(new Date().toString() + " Saving patch file...");
            saveMapToFile(differentBytes, file1 + ".ubp");
            System.out.println(new Date().toString() + " Patch file saved.");


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, Integer> compareLists(List<Integer> list1, List<Integer> list2) {
        Map<Integer, Integer> differentBytes = new HashMap<>();

        // Determine the length of the longer list

        int shorterListLength = Math.min(list1.size(), list2.size());


        // Compare the elements in the lists and add the different elements to the HashMap
        for (int i = 0; i < shorterListLength; i++) {
            if( i < list1.size() && i < list2.size()) {
                if (!list1.get(i).equals(list2.get(i))) {
                    differentBytes.put(i, list2.get(i));
                }
            }
            if(i >= list1.size()) {
                differentBytes.put(i, list2.get(i));
            }
            if(i >= list2.size()) {
                differentBytes.put(i, list1.get(i));
            }
        }
        int longerListLength = Collections.max(differentBytes.keySet());
        // Include the value of the of the hashmap in the HashMap
        differentBytes.put(-1, longerListLength);

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
    public static List<Integer> applyPatchToList(List<Integer> list, Map<Integer, Integer> patchHashMap) {


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
        return list;
    }

    public static void saveMapToFile(Map<Integer, Integer> map, String filePath) {
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

}
