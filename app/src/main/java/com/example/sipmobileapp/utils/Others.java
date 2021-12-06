package com.example.sipmobileapp.utils;

public class Others {

    public static boolean hasEnglishLetter(String input) {
        StringBuilder output = new StringBuilder();
        char[] chars = input.toCharArray();
        for (Character character : chars) {
            if (!String.valueOf(character).equals(".")) {
                output.append(character);
            }
        }
        return output.toString().matches(".*[a-zA-Z]+.*");
    }

    public static boolean hasThreeDots(String input) {
        int count = 0;
        char[] chars = input.toCharArray();
        for (Character character : chars) {
            if (String.valueOf(character).equals(".")) {
                count++;
            }
        }
        return count == 3;
    }
}
