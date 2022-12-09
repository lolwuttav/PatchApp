# PatchApp

PatchApp is a slow patcher that generates and applies patch files to update files. The program can take up to 3 arguments, as described below.

## Arguments

1. **mode**: This is the mode of the program, which can be either `1` or `2`. `1` is to generate a patch, `2` is to apply patch to a file.
2. **file1**: This is the original file to patch.
3. **file2**: This is the modified file to generate a patch from. This argument is ignored when running in mode 2.

## Usage

To use the program, you can run it from the command line with the following command:

```
java PatchApp [mode] [file1] [file2]
```

Alternatively, you can run the program without any arguments and the program will prompt you to enter the mode and file names interactively.

Please note that patch files of hugely different files will be massive and might take some time to generate and apply.

## Example

To generate a patch file for the original file `file1.txt` and the modified file `file2.txt`, run the following command:

```
java PatchApp 1 file1.txt file2.txt
```

To apply the patch file `file1.txt.patch` to the original file `file1.txt`, run the following command:

```
java PatchApp 2 file1.txt
```
