package com.ust;

import com.github.tomakehurst.wiremock.http.Fault;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import java.net.URI;

import java.net.http.HttpClient;

import java.net.http.HttpRequest;

import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WireMockServiceVirtulaisationTest {

    @RegisterExtension

    static WireMockExtension wm = WireMockExtension.newInstance()

            .options(wireMockConfig().dynamicPort())

            .build();

    private HttpClient client;

    @BeforeEach
    void pointConsumerAtWireMock() {

        baseURI = wm.baseUrl();

        client = HttpClient.newHttpClient();

    }

    @Test

    @DisplayName("Stub Inventory")
    void returnsConfirmedOrderOverRealHttp() {

        wm.stubFor(
                get(urlPathEqualTo("/orders/123"))
                        .willReturn(
                                okJson("""
                                        {"id":123,"status":"CONFIRMED","total":42.0}
                                        """)
                        )
        );
    }


    @Test
    @DisplayName("Stub Inventory - Success and Out Of Stock")
    void stubInventoryTwoOutcomes() {
        // Success case
        wm.stubFor(get(urlPathEqualTo("/inventory/SKU-9")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {"sku":"SKU-9","qty":5}
                        """)));
        // Out of stock case
        wm.stubFor(get(urlPathEqualTo("/inventory/SKU-0")).willReturn(aResponse()
                .withStatus(409)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {
                          "error":"OUT_OF_STOCK"
                        }
                        """)));
        // Verify success response
        given()
                .when()
                .get("/inventory/SKU-9")
                .then()
                .statusCode(200)
                .body("qty", equalTo(5))
                .body("sku", equalTo("SKU-9"));
        // Verify out of stock response
        given()
                .when()
                .get("/inventory/SKU-0")
                .then()
                .statusCode(409)
                .body("error", equalTo("OUT_OF_STOCK"));
        // Verify SKU-9 was called exactly once
        wm.verify(exactly(1), getRequestedFor(urlPathEqualTo("/inventory/SKU-9")));
    }

    @Test

    @DisplayName("Slow API - Force timeout then succeed")

    void slowApiTimeoutThenSuccess() throws Exception {

        // Stub endpoint with 3 second delay

        wm.stubFor(
                get(urlPathEqualTo("/orders/slow"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withFixedDelay(3000)
                                        .withBody("""
                                              {
                                              "id":999,
                                              "status":"CONFIRMED"
                                              }
                                                """)
                        ));
        String url = wm.baseUrl() + "/orders/slow";
        // Request timeout = 1 second -> should fail

        HttpRequest timeoutRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(1))
                .GET()
                .build();
        assertThrows(
                HttpTimeoutException.class,
                () -> client.send(timeoutRequest, ofString())
        );

        // Request timeout = 5 seconds -> should succeed

        HttpRequest successRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        var response = client.send(successRequest, ofString());
        assertEquals(200, response.statusCode());
        assertEquals(

                """
                {   
                  "id":999,
                  "status":"CONFIRMED"
                }
    
                """.replaceAll("\\s+", ""),

                response.body().replaceAll("\\s+", "")

        );

    }

    @Test
    @DisplayName("Scenario Fullfillment")
    void Fullfillment()
    {
        wm.stubFor(get(urlPathEqualTo("/orders/42"))
                .inScenario("fullfillment")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("CONFIRMED")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(3000)
                        .withBody("""
                                      {
                                      "id":42,
                                      "status":"PENDING"
                                        }
                                 """)));

        wm.stubFor(get(urlPathEqualTo("/orders/42"))
                .inScenario("fullfillment")
                .whenScenarioStateIs("CONFIRMED")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id":42,
                                    "status":"CONFIRMED"
                                }
                                """)));

        given()
                .when()
                .get("/orders/42")
                .then()
                .body("status" , equalTo("PENDING"));

        given()
                .when()
                .get("/orders/42")
                .then()
                .body("status" , equalTo("CONFIRMED"));

        wm.verify(exactly(2),getRequestedFor(urlPathEqualTo("/orders/42")));


    }





}


