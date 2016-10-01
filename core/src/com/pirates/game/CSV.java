package com.pirates.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CSV {
    private static Pattern word = Pattern.compile("[_a-zA-Z]+");
    private static Pattern comment = Pattern.compile("(\\s*#.*)*", Pattern.MULTILINE);

    public static Map<String, Float> readFile(String fileName) {
        FileHandle file = Gdx.files.internal(fileName);
        Scanner scanner;
        try {
            scanner = new Scanner(file.file());
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Could not load CSV file " + fileName);
            return null;
        }

        HashMap<String, Float> map = new HashMap<String, Float>();
        while (scanner.hasNext()) {
            //scanner.findInLine("(\\w+)\\s+(\\d+)");
            scanner.skip(comment);
            String var = scanner.next(word).toLowerCase();
            float value = scanner.nextFloat();
            System.out.println(var + " " + value);
            map.put(var, value);
        }
        return map;
    }
}
