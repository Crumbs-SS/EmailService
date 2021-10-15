package com.crumbs.emailservice.util;
import lombok.Getter;

public class ApiUtil{

    private ApiUtil(){
        throw new IllegalStateException("Utility class");
    }

    @Getter private static final String CLIENT_URL = "https://crumbs-ss.link";
    @Getter private static final String AWS_SECRET_ACCESS_KEY = System.getenv("AWS_SECRET_ACCESS_KEY");
    @Getter private static final String AWS_ACCESS_KEY_ID = System.getenv("AWS_ACCESS_KEY_ID");
    @Getter private static final String FROM = "crumbsFoodService@gmail.com";

}
