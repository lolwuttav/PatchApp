import util.FileUtil;

import java.util.Scanner;

public class PatchApp {

    public static void main(String[] args) {
        Request request = parseRequest(args);

        if (request == null) {
            runInteractive();
            return;
        }

        runRequest(request);
    }

    private static Request parseRequest(String[] args) {
        if (args.length == 0) {
            return null;
        }

        if (args.length < 2) {
            System.out.println("Usage: java PatchApp [mode] [file1] [file2]");
            return new Request(3, null, null);
        }

        int mode;
        try {
            mode = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid mode. Must be 1, 2, or 3.");
            return new Request(3, null, null);
        }

        String file1 = args[1];
        String file2 = args.length >= 3 ? args[2] : null;
        return new Request(mode, file1, file2);
    }

    private static void runInteractive() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Please select a mode:");
                System.out.println("1. Generate patch file");
                System.out.println("2. Apply patch file");
                System.out.println("3. Exit");

                int mode = scanner.nextInt();
                scanner.nextLine();

                if (mode == 3) {
                    System.out.println("Exiting...");
                    return;
                }

                System.out.print("Enter the first file: ");
                String file1 = scanner.nextLine();

                String file2 = null;
                if (mode == 1) {
                    System.out.print("Enter the second file: ");
                    file2 = scanner.nextLine();
                }

                runRequest(new Request(mode, file1, file2));
            }
        }
    }

    private static void runRequest(Request request) {
        switch (request.mode) {
            case 1:
                if (request.file2 == null || request.file2.isBlank()) {
                    System.out.println("Mode 1 requires [file2].");
                    return;
                }
                FileUtil.generatePatchFile(request.file1, request.file2);
                break;
            case 2:
                FileUtil.applyPatchToFile(request.file1, request.file1 + FileUtil.PATCH_FILE_EXTENSION);
                break;
            case 3:
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid mode selected.");
        }
    }

    private static final class Request {
        private final int mode;
        private final String file1;
        private final String file2;

        private Request(int mode, String file1, String file2) {
            this.mode = mode;
            this.file1 = file1;
            this.file2 = file2;
        }
    }
}
