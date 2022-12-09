package util;


import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;


public class FileUtil {

    public static final String PATCH_FILE_EXTENSION = ".ubp";
    private static String MD51;
    private static String MD52;

    private static Timer timer = new Timer();
    private static Timer timer2 = new Timer();

    public static void applyPatchToFile(String file1, String file2) {

        Map<Integer, Integer> patch = decompressMap(file2);

        List<Integer> file = readBytesFromFile(file1);

        timer.start("Getting MD5 of files");
        MD51 = MD5.getFileMD5Checksum(file1);
        MD52 = MD5.getFileMD5Checksum(file2);
        timer.stop();

        try {
            timer.start("Applying patch to data");
            if(!applyPatchToList(file, patch)){
                System.out.println("Error applying patch, aborting");
                return;
            }
            timer.stop();
        } catch (NullPointerException e) {
            System.out.println("Patch failed, aborting");
            System.out.println(e.getStackTrace().toString());
            return;
        }

        String[] parts = file1.split("\\.");
        String part1 = parts[0];
        String part2 = parts[1];
        timer.start("Writing patched file");
        writeListToFile(file, part1 + "-patched" + "." + part2);
        timer.stop();
    }


    public static void generatePatchFile(String file1, String file2) {
        try {
            // Open the files
            FileInputStream fis1 = new FileInputStream(file1);
            FileInputStream fis2 = new FileInputStream(file2);

            timer.start("Getting MD5 of files");
            MD51 = MD5.getFileMD5Checksum(file1);
            MD52 = MD5.getFileMD5Checksum(file2);
            timer.stop();

            // Get the MD5 checksums
            if(MD51.equals(MD52)) {
                System.out.println("The files are the same, no patch will be generated.");
                return;
            }


            timer.start("Reading files into memory");
            List<Integer> bytes1 = readBytesFromFile(file1);
            List<Integer> bytes2 = readBytesFromFile(file2);
            timer.stop();


            timer.start("Generating patch");
            Map<Integer, Integer> differentBytes = compareLists(bytes1, bytes2);
            timer.stop();
            //Save the different bytes map to a file
            //writeMapToFile(differentBytes, file1 + PATCH_FILE_EXTENSION);
            timer.start("Saving patch to file");


            compressMap(differentBytes, file1 + PATCH_FILE_EXTENSION);
            timer.stop();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, Integer> compareLists(List<Integer> list1, List<Integer> list2) {
        Map<Integer, Integer> differentBytes = new HashMap<>();

        int longerListLength = Math.max(list1.size(), list2.size());

        timer2.start("Comparing lists");


        // Compare the elements in the lists and add the different elements to the HashMap
        for (int i = 0; i < longerListLength; i++) {
            if (i < list1.size() && i < list2.size()) {
                if (!list1.get(i).equals(list2.get(i))) {
                    differentBytes.put(i, list2.get(i));
                }
            }
            if (i > list1.size()) {
                differentBytes.put(i, list2.get(i));
            }
            if (i > list2.size()) {
                differentBytes.put(i, list1.get(i));
            }
        }
        differentBytes.put(-1, list2.size());
        timer2.stop();



        // Add the checksum of the list to the HashMap
        timer2.start("Combining data");
        addIntListToMap(toIntList(MD5.getListMD5Checksum(list2)), differentBytes);
        timer2.stop();
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
            bis.close();
            fis.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return bytes;
    }
    public static boolean applyPatchToList(List<Integer> list, Map<Integer, Integer> patchHashMap) {


       //Probably not needed anymore
        int maxKey = patchHashMap.get(-1);
        if(list.size() > maxKey) {
            int difference = list.size() - maxKey;
            System.out.println(new Date().toString() + " Bytes to delete: " + difference);
            for(int i = list.size() - 1; i > maxKey - 1; i--) {
                list.remove(i);
            }
        }
        if(list.size() < maxKey) {
            int difference = maxKey - list.size();
            System.out.println(new Date().toString() + " Bytes to add: " + difference);
            for(int i = 0; i < difference; i++) {
                list.add(list.size(), 0);
            }
        }

        for (int key : patchHashMap.keySet()) {
            int value = patchHashMap.get(key);

            if (key >= 0 && key < list.size() - 1) {
                list.set(key, value);
            }
        }


        // Return the modified list
        //compare the md5 checksums of the patched list and patchHashMap
        String MD5Patched = MD5.getListMD5Checksum(list);
        String MD5Patch = getStringFromMap(patchHashMap);

        if(MD5Patched.equals(MD5Patch)) {
            System.out.println("MD5 checksums match original file");
        } else {
            System.out.println("MD5 checksums do not match");
            //return false;
        }
        return true;


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
            bos.close();
            fos.close();
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

    public static void compressMap(Map<Integer, Integer> map, String outputFileName) {
        try (
                // Create the output stream
                OutputStream outputStream = new FileOutputStream(outputFileName);

                // Wrap the output stream in a buffered output stream
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

                // Create the compressor and the deflater output stream
                DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(bufferedOutputStream, new Deflater());

                // Create the object output stream using the specified character encoding
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(deflaterOutputStream);
        ) {

            // Create a new map to store the data
            Map<Integer, Integer> compressedMap = new HashMap<>();

            // Add the data from the input map to the compressed map
            compressedMap.putAll(map);

            // Write the compressed map to the output stream
            objectOutputStream.writeObject(compressedMap);
            objectOutputStream.close();
            deflaterOutputStream.close();
            if(!decompressMap(outputFileName).equals(map)) {
                System.out.println("Map compression failed");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, Integer> decompressMap(String inputFileName) {
        Map<Integer, Integer> map = null;

        try {
            // Create the input stream
            InputStream inputStream = new FileInputStream(inputFileName);

            // Wrap the input stream in a buffered input stream
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            // Create the decompressor and the inflater
            Inflater decompressor = new Inflater();
            InflaterInputStream inflaterInputStream = new InflaterInputStream(bufferedInputStream, decompressor);

            // Read the map from the input stream
            ObjectInputStream objectInputStream = new ObjectInputStream(inflaterInputStream);
            map = (Map<Integer, Integer>) objectInputStream.readObject();


            // Close the streams
            objectInputStream.close();
            inflaterInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static boolean deleteFile(String filePath) {
        // Create a File object for the specified file
        File file = new File(filePath);

        // Delete the file and return the result
        return file.delete();
    }


}
