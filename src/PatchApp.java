import util.FileUtil;

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

    private static boolean argsPresent = false;

    private static int mode = 0;
    public static void main(String[] args) {





        if(args.length != 0){
            argsPresent = true;
            mode = Integer.parseInt(args[0]);
            FILE1 = args[1];
            FILE2 = args[2];
        }

        while (true) {

            mode = 0;
            FILE1 = "";
            FILE2 = "";

            modeScanner = new Scanner(System.in);
            file1Scanner = new Scanner(System.in);
            file2Scanner = new Scanner(System.in);

            if(mode == 0) {
                System.out.println("Please select a mode:");
                System.out.println("1. Generate patch file");
                System.out.println("2. Apply patch file");
                System.out.println("3. Exit");
                modeScanner = new Scanner(System.in);
                mode = modeScanner.nextInt();
            }

            if(mode == 1) {
                if (FILE1.equals("")) {
                    System.out.print("Enter the first file: ");
                    FILE1 = file1Scanner.nextLine();
                }
                if (FILE2.equals("")) {
                    System.out.print("Enter the second file: ");
                    FILE2 = file2Scanner.nextLine();
                }


                FileUtil.generatePatchFile(FILE1, FILE2);
            }



            else if(mode == 2) {
                if(FILE1.equals("")) {
                    System.out.print("Enter the first file: ");
                    FILE1 = file1Scanner.nextLine();
                }

                FileUtil.applyPatchToFile(FILE1, FILE1 + FileUtil.PATCH_FILE_EXTENSION);


                }

            else if(mode == 3) {
                System.out.println("Exiting...");
                break;
            }
            else {
                System.out.println("Invalid mode selected.");
            }

            if(argsPresent) {
                break;
            }


        }



        }







}
