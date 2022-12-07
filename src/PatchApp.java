import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static util.FileUtil.applyPatchToFile;
import static util.FileUtil.generatePatchFile;

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

                applyPatchToFile(FILE1, FILE1 + ".ubp");
            }


        }



        }






}
