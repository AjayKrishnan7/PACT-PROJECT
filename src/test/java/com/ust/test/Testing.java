package com.ust.test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.ust.support.SpecFactory.*;
import static com.ust.support.apiConfig.LoginToken;
import static io.restassured.RestAssured.given;
import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class Testing {

    static String accessToken;

    @BeforeAll
    static void setup()
    {
        accessToken  = LoginToken()  ;
    }



    @Test
    @DisplayName("Exercise 1")
    void ex1()
    {
        var order= Map.of("items", List.of(101,107),"currency","INR");

        given()
                .spec(authed(accessToken))
                .body(order)
            .when()
                .post()
            .then()
                .spec(postJson())
                .body("items.size()",greaterThanOrEqualTo(2))
                .body("status", equalTo("CREATED"))
                .body( "payment",equalTo("Pending"));

           // .body(matchesJsonSchemaInClasspath("schemas/json/order.schema.json"));

    }

    @Test
    @DisplayName("Exercise 3 : Missing Auth Token")
    void ex3()
    {
        given()
                .spec(missTokenReq())
                .when()
                .get("/{id}",5001)
                .then()
                .spec(missTokenRes());

        System.out.println("Missing Token");
    }


    @Test
    @DisplayName("Exercise 3 : Invalid Token ")
    void ex31()
    {
        given()
                .spec(invalidTokenReq())
                .when()
                .get("/{id}",5001)
                .then()
                .spec(invalidTokenRes());
        System.out.println("Invalid Token");

    }

    @Test
    @DisplayName("Exercise 3 : No OPS(VIEWERS) BUT VALID")
    void ex32()
    {
        var order=Map.of("items",List.of(101,107), "currency","INR");

        given()
                .spec((NoOPSTokenReq()))
                .body(order)
                .when()
                .post()
                .then()
                .spec(NoOPSTokenRes());
        System.out.println("NO OPS BUT VALID");

    }

    @Test
    @DisplayName("Exercise 3 : Token Expired")
    void ex33()
    {
        given()
                .spec(expiredTokenReq())
                .when()
                .get("/{id}",5001)
                .then()
                .spec(expiredTokenRes());

    }


    @Test
    @DisplayName("API KEY Header")
    void ex34()
    {
        given()
                .spec(API())
                .get("/{id}",5001)
                .then()
                .spec(okJson());

    }



    @Test
    @DisplayName("M2: product detail matches product XML schema")
    void CorrectProductXmlSchema() {
        Response response =
                given()
                        .spec(reqXml())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.XML)
                        .body(matchesXsdInClasspath("schemas/products.xsd"))
                        .extract().response();
    }



    @Test
    @DisplayName("M2: XML Failed TestCase")
    void FailedProductXmlSchema() {
        Response response =
                given()
                        .spec(reqXml())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.XML)
                        .body(matchesXsdInClasspath("schemas/json/products.xsd"))
                        .extract().response();
    }


    //FULL FLOW

    @Test
    @DisplayName("FULL FLOW")

    void FLOW() {
        var login = Map.of("email", "customer@example.com", "password", "Password@123");

//            String token=
         given()
                .spec(Logreq(accessToken))
                .body(login)
            .when()
                .post()
                .then()
            .spec(Logres())
                .extract();


         //DELETE

            given()
                    .spec(deleteJsonReq(accessToken))
                    .when()
                    .delete()
                    .then()
                    .spec(deleteJsonRes());


            //ADD TO CART


            var order = Map.of("productId", 101, "quantity", 1, "size", "UK 7", "color", "Navy", "fulfilment", "Home delivery");

                    given()
                            .spec(addToCartreq(accessToken))
                            .body(order)
                        .when()
                            .post()
                        .then()
                            .spec(postJson())  // Check Headers for getting the status

                            .body("productId", equalTo(order.get("productId"))) //Post only one item return
                            .body("quantity", equalTo(order.get("quantity"))) // also check payload for understanding what value does user give and response for what it gives
                            .body("size", equalTo(order.get("size")))
                            .body("color", equalTo(order.get("color")))
                            .body("fulfilment", equalTo(order.get("fulfilment")))

//                        .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"))
                            .extract();

            //PLACE ORDER



            var orders = Map.of("paymentMethod", "Credit card", "deliverySlot", "Tomorrow 9 AM - 12 PM", "address", "UST Campus, Bengaluru", "coupon", "", "shipping", 199, "discount", 0);

            var idValue =              //why given response given, we can give var?
                    given()
                            .spec(placeOrder(accessToken))
                            .body(orders)
                        .when()
                            .post()
                        .then()
                            .spec(postJson())

                            .log().all()
                            .body("paymentMethod", equalTo(order.get("paymentMethod")))
                            .body("deliverySlot", equalTo(order.get("deliverySlot")))
                            .body("address", equalTo(order.get("address")))
                            .body("coupon", equalTo(order.get("coupon")))
                            .body("shipping", equalTo(order.get("shipping")))
                            .body("discount", equalTo(order.get("discount")))


                        //    .body(matchesJsonSchemaInClasspath("schemas/order.schema.json"))
                            .extract().path("id");

            System.out.println("ID IS : " + idValue);


//            given()
//                    .spec(getreqplaceord)
//                    .when()
//                    .get()
//                    .then()
//                    .statusCode(200)
//                    .body("id", equalTo(idValue));



    }



    @Test
    @DisplayName("M2: product detail matches product XML schema")
    void productDetailtailmatchesProductXmlSchema() {
        Response response =
                given()
                        .spec(reqXml())
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.XML)
                        .body(matchesXsdInClasspath("schemas/products.xsd"))
                        .extract().response();
    }


}



