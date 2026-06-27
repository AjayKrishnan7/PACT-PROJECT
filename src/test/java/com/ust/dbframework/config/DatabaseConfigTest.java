package com.ust.dbframework.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
class DatabaseConfigTest {
    @Test
    void usesDefaultPostgresPortWhenCloudUrlOmitsPort() {
        DatabaseConfig config = DatabaseConfig.fromDatabaseUrl(
                "postgresql://cloud_user:cloud_password@db.example.com/training?sslmode=require"
        );
        assertEquals(
                "jdbc:postgresql://db.example.com:5432/training?sslmode=require",
                config.jdbcUrl()
        );
    }
    @Test
    void preservesExplicitPort() {
        DatabaseConfig config = DatabaseConfig.fromDatabaseUrl(
                "postgresql://local_user:local_password@localhost:5544/training"
        );
        assertEquals("jdbc:postgresql://localhost:5544/training", config.jdbcUrl());
    }
}
