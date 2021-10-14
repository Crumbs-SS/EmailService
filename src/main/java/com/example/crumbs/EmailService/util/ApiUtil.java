package com.example.crumbs.EmailService.util;

public class ApiUtil {
    private ApiUtil(){
        throw new IllegalStateException("Utility class");
    }
    public static String getClientURL(){
        return "https://crumbs-ss.link";
    }
}
