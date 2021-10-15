package com.crumbs.emailservice.util;

import lombok.Getter;

public class ApiUtil{

    @Getter private static final String clientURL = "https://crumbs-ss.link";
    @Getter private static final String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
    @Getter private static final String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
    @Getter private static final String from = "crumbsFoodService@gmail.com";

}
