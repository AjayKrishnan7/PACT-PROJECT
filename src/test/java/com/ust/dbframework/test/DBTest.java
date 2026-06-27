package com.ust.dbframework.test;

import com.ust.dbframework.model.OrderRow;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.ust.dbframework.config.DatabaseConfig;
import com.ust.dbframework.model.OrderRow;
import com.ust.dbframework.support.DbSupport;
import com.ust.dbframework.support.TestEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.ust.config.Constants.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.oauth2;
import static com.ust.support.SpecFactory.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;

public class DBTest {
    static DbSupport database;

    @BeforeAll
    static void setup()
    {
        //apiClient = new OrderApiClient(TestEnvironment.optional(name:"BASE_URL",fallback:"http://localhost:4000x"));
        database = new DbSupport(DatabaseConfig.fromEnvironment());
    }

    @Test
    @DisplayName("Local M1: MySQL is reachable through JDBC")
    void localMysqlIsReachable() throws Exception
    {
        assertTrue(database.isReachable());
    }

    public static String loginToken() {

        return     given()
                .baseUri(baseUrl)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .auth().preemptive().basic("retail-ops-client","2a2729b27b47fe27b6412403d886ef4781bbff36b0e2b58e" )
                .formParam("grant_type", "client_credentials")
                .when()
                .post("api/oauth/token")
                .then()
                .statusCode(200)
                .body("access_token", not(emptyString()))
                .body("expires_in", greaterThan(0))
                .body("token_type", equalToIgnoringCase("Bearer"))
                .extract().path("access_token");
    }

    RequestSpecification authed=
            new RequestSpecBuilder()
                    .setBaseUri(baseUrl)
                    .setBasePath("/api/secure/orders")
                    .setContentType(ContentType.JSON)
                    .setAuth(oauth2(loginToken()))
                    .build();



    @Test
    @DisplayName("Persisted and test")
    void createOrder_isPersisted() throws SQLException {
        var order= Map.of("items", List.of(101,107),"currency","INR");

        var c =
                given()
                        .spec(authed)
                        .body(order)
                        .when()
                        .post()
                        .then()
                        .statusCode(201)
                        .extract();

        System.out.println(c);

        Integer id = c.path("orderId");
        OrderRow  row = DbSupport.findOrder(id);
        assertNotNull(row, "order must be persisted");
        assertEquals("CREATED", row.status());
        assertEquals(0,row.total().compareTo(c.jsonPath()
                .getObject("total", BigDecimal.class)));

    }

}
