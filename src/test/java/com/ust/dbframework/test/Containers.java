package com.ust.dbframework.test;



import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;



@Testcontainers
public class Containers {

    @Container
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.4")
                    .withDatabaseName("sdet_retail")
                    .withUsername("root")
                    .withPassword("Formysql@123");

    @Test
    void containerShouldStart() {

        System.out.println(
                "JDBC URL = "
                        + mysql.getJdbcUrl()
        );

        System.out.println(
                "Username = "
                        + mysql.getUsername()
        );
    }
}
