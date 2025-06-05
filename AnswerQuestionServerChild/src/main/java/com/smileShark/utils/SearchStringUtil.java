package com.smileShark.utils;

public class SearchStringUtil {
    public static String handler(String input){
        if(input == null || input.isEmpty()){
            return "%";
        }
        return  "%" + String.join("%", input.replaceAll("[ '<>&/()（）%_ ]", "").split("")) + "%";
    }
}
