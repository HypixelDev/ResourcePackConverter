package net.hypixel.resourcepack;

import joptsimple.OptionSet;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        OptionSet optionSet = Options.PARSER.parse(args);
        new PackConverter(optionSet).run();

        System.out.println("Done, press any key to exit!");
        System.in.read();
    }

}