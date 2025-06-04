package com.smileShark.utils;

public class SearchStringUtil {
    public static String handler(String input){
        return  "%" + String.join("%", input.replaceAll("[ '<>&/()（）%_ ]", "").split("")) + "%";
    }
}
