package com.example.sipmobileapp.utils;

public class Converter {

    public static String letterConverter(String input) {
        return input.replaceAll("ي", "ی");
    }

    public static String perToEnDigitConverter(String input) {
        return input.replaceAll("۰", "0").
                replaceAll("۱", "1").
                replaceAll("۲", "2").
                replaceAll("۳", "3").
                replaceAll("۴", "4").
                replaceAll("۵", "5").
                replaceAll("۶", "6").
                replaceAll("۷", "7").
                replaceAll("۸", "8").
                replaceAll("۹", "9").
                replaceAll(" ", "");
    }
}
