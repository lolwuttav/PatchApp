import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PatchApp {

    private static Scanner modeScanner;
    private static Scanner file1Scanner;
    private static Scanner file2Scanner;
    private static String FILE1 = "";
    private static String FILE2 = "";
    public static void main(String[] args) {


        while (true) {


            System.out.println("The following modes are available: ");
            System.out.println("1. Generate patch file");
            System.out.println("2. Apply patch file");
            System.out.print("Enter the mode: ");
            modeScanner = new Scanner(System.in);
            file1Scanner = new Scanner(System.in);
            file2Scanner = new Scanner(System.in);

            int mode = modeScanner.nextInt();

            if(mode == 1) {

                System.out.print("Enter the path of the old file: ");
                FILE1 = file1Scanner.nextLine();

                System.out.print("Enter the path of the new file: ");
                FILE2 = file2Scanner.nextLine();


              //  generatePatchFile(file1, file2);
                generatePatchFile(FILE1, FILE2);
            }
            else if(mode == 2) {
                System.out.println("Enter the path of the file to patch: ");
                String FILE1 = file1Scanner.nextLine();

                //System.out.println("Enter the name of the patch file: ");
                //String file2 = scanner.nextLine();

                //apply patch file
                //applyPatchToFile(file1, file2);
                applyPatchToFile(FILE1, FILE1 + ".ubp");
            }


            // Close the scanner

        }



        }

    private static void applyPatchToFile(String file1, String file2) {
        System.out.println( new Date().toString() + "Loading patch file...");
        Map<Integer, Integer> patch = readMapFromFile(file2);
        System.out.println(new Date().toString() + "Loading file to patch...");
        List<Integer> file = readBytesFromFile(file1);
        //System.out.println(patch.toString());

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

    public static String generateChecksum(String filePath) {
        try {
            // Create a FileInputStream to read the file
            FileInputStream fis = new FileInputStream(filePath);

            // Create a MessageDigest to generate the checksum
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Read the first byte from the file
            int b = fis.read();

            // Keep reading bytes from the file and updating the MessageDigest until we reach the end of the file
            while (b != -1) {
                md.update((byte) b);
                b = fis.read();
            }

            // Close the FileInputStream
            fis.close();

            // Convert the byte array from the MessageDigest to a hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b1 : md.digest()) {
                sb.append(String.format("%02x", b1));
            }

            // Return the hexadecimal string
            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Error generating checksum: " + e.getMessage());
            return null;
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

    //method to return a list of integers from a file
    public static List<Integer> readBytesFromFile(String fileName) {
        List<Integer> bytes = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            int byte1 = fis.read();
            while (byte1 != -1) {
                bytes.add(byte1);
                byte1 = fis.read();
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return bytes;
    }

    public static List<Integer> applyPatchToList(List<Integer> list, Map<Integer, Integer> patchHashMap) {


        //get method name
        int maxKey = patchHashMap.get(-1); //Collections.max(patchHashMap.keySet());
        System.out.println("Max key: " + maxKey + " LargestList: " + patchHashMap.get(-1));
        if(list.size() > maxKey) {
            //get difference between list size and max key
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

            // If the key (index) is within the bounds of the list, replace the element at that index with the new value
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
        try {
            FileOutputStream fos = new FileOutputStream(filePath);

            // Write each integer in the list to the FileOutputStream as a byte
            for (int i : list) {
                fos.write(i);
            }

            fos.close();
        } catch (IOException e) {
            System.out.println("Error writing list to file: " + e.getMessage());
        }
    }




}
