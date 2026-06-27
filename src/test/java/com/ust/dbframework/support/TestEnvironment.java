package com.ust.dbframework.support;

public final class TestEnvironment {

    private TestEnvironment() {
    }

    public static String optional(String name, String defaultValue) {

        String systemProperty = System.getProperty(name);

        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty;
        }

        String envValue = System.getenv(name);

        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }

    public static String required(String name) {

        String value = optional(name, null);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required environment variable or system property: "
                            + name);
        }

        return value;
    }
}
