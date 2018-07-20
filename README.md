# Resource Pack Converter

Please note that this code worked for the packs we use (ranging from 1.8 to 1.12 versions), it might not convert everything; some things will need to be handled by hand, some items may have been mapped wrong, some things aren't possible anymore, etc.
Feel free to open PRs with fixes and additions.

While this program will copy your resource packs before converting them, we still recommend backing them up, just in case!

## Usage
The program will look for any valid resource packs in the current directory and is easily run by doing this.

    java -jar ResourcePackConverter.jar

You can set the input directory using one of the following parameters.
`-i <path>`, `--input <path>` or `--input-dir <path>`.

    java -jar ResourcePackConverter.jar --input input/


