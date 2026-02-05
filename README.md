# PatchApp

PatchApp generates and applies compressed binary patch files.

## Arguments

1. **mode**: Program mode (`1`, `2`, or `3`).
   - `1`: Generate a patch from `file1` (source) to `file2` (target).
   - `2`: Apply `file1.ubp` to `file1`.
   - `3`: Exit.
2. **file1**: Source/original file.
3. **file2**: Target/modified file (required only for mode `1`).

## Usage

```bash
java PatchApp [mode] [file1] [file2]
```

Without arguments, PatchApp runs in interactive mode and prompts for values.

## Examples

Generate a patch file:

```bash
java PatchApp 1 file1.txt file2.txt
```

This writes the patch to:

```text
file1.txt.ubp
```

Apply a patch file:

```bash
java PatchApp 2 file1.txt
```

This reads `file1.txt.ubp` and writes output to `file1-patched.txt`.
