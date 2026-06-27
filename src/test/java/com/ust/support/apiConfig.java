package com.ust.support;

import com.ust.config.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.ust.config.Constants.*;
import static com.ust.support.SpecFactory.okJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public class apiConfig {

 public static String LoginToken()
 {

   return  given()
             .baseUri(Constants.baseUrl)
             .contentType("application/x-www-form-urlencoded; charset=UTF-8")
             .auth().preemptive().basic(OAUTH_CLIENT_ID,OAUTH_CLIENT_SECRET)
             .formParam("grant_type", "client_credentials")
         .when()
             .post("/api/oauth/token")
         .then()
            .spec(okJson())
             .body("access_token", not(emptyString()))
             .body("expires_in", greaterThan(0))
             .body("token_type", equalToIgnoringCase("Bearer"))
             .extract().path("access_token");
 }

    public static String NoOpsToken()
    {

        return
                given()
                        .baseUri(baseUrl)
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .auth().preemptive().basic(OAUTH_VIEWER_CLIENT_ID, OAUTH_VIEWER_CLIENT_SECRET)
                        .formParam("grant_type", "client_credentials")
                        .when()
                        .post("/api/oauth/token")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("access_token");

    }


    public static String expiredToken()
    {


        return   given()
                .baseUri(baseUrl)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .auth().preemptive().basic(OAUTH_EXPIRED_CLIENT_ID, OAUTH_EXPIRED_CLIENT_SECRET)
                .formParam("grant_type", "client_credentials")
                .when()
                .post("/api/oauth/token")
                .then()
                .statusCode(200).toString();

    }


}
