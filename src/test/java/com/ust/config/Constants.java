package com.ust.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Constants {

    public static String baseUrl=System.getProperty(
            "baseUrl",
            System.getenv().getOrDefault(
                    "BASE_URL",
                    "http://localhost:4000"));




    static String config(String value, String fallback) {

        String systemValue = System.getProperty(value);

        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envValue = System.getenv(value);

        return envValue == null || envValue.isBlank()
                ? fallback
                : envValue;
    }

    static Dotenv dotenv = Dotenv.load();

    public static final String OAUTH_VIEWER_CLIENT_ID = dotenv.get("OAUTH_VIEWER_CLIENT_ID");
    public static final String OAUTH_VIEWER_CLIENT_SECRET =  dotenv.get("OAUTH_VIEWER_CLIENT_SECRET");
    public static final String OAUTH_CLIENT_SECRET =  dotenv.get("OAUTH_CLIENT_SECRET");
    public static final String OAUTH_CLIENT_ID =  dotenv.get("OAUTH_CLIENT_ID");
    public static final String OAUTH_EXPIRED_CLIENT_ID = dotenv.get("OAUTH_EXPIRED_CLIENT_ID");
    public static final String OAUTH_EXPIRED_CLIENT_SECRET = dotenv.get("OAUTH_EXPIRED_CLIENT_SECRET");
    public static final String RETAIL_API_KEY = dotenv.get("RETAIL_API_KEY");


}

