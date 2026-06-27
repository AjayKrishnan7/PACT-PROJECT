package com.ust.support;

import com.ust.config.Constants;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static com.ust.config.Constants.RETAIL_API_KEY;
import static com.ust.config.Constants.baseUrl;
import static com.ust.support.apiConfig.NoOpsToken;
import static com.ust.support.apiConfig.expiredToken;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.oauth2;

public class SpecFactory {

    public static RequestSpecification authed(String token)
    {
        return new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setBasePath("/api/secure/orders")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .setAuth(oauth2(token))
            .build();
    }

//    public static RequestSpecification notauthed()
//    {
//        return new RequestSpecBuilder()
//                .setBaseUri(baseUrl)
//                .setBasePath("/api/secure/orders")
//                .setContentType(ContentType.JSON)
//                .setAccept(ContentType.JSON)
//                .build();
//    }

    public static ResponseSpecification okJson() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();
    }

   public static RequestSpecification deleteJsonReq(String token)
   {
     return  new RequestSpecBuilder()
               .setBaseUri(baseUrl)
               .setBasePath("/api/cart")
               .setAccept("application/json")
               .setContentType(ContentType.JSON)
               .setAuth(oauth2(token))
               .build();
   }

    public static ResponseSpecification deleteJsonRes()
    {
        return new ResponseSpecBuilder()
                .expectStatusCode(204)
                .build();

    }

    public static ResponseSpecification postJson()
    {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(201)
                .build();

    }


    public static RequestSpecification missTokenReq() {

        return  new RequestSpecBuilder()

                .setBaseUri(baseUrl)
                .setBasePath("/api/secure/orders")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification missTokenRes() {

        return  new ResponseSpecBuilder()

                .expectContentType(ContentType.JSON)
                .expectStatusCode(401)
                .build();
    }

    public static RequestSpecification  invalidTokenReq() {

        return   new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath("/api/secure/orders")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setAuth(oauth2("abcd"))
                .build();
    }

    public static ResponseSpecification invalidTokenRes() {

        return  new ResponseSpecBuilder()

                .expectContentType(ContentType.JSON)
                .expectStatusCode(401)
                .build();
    }



    public static RequestSpecification NoOPSTokenReq() {

        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath("/api/secure/orders")
                .setContentType(ContentType.JSON)
                .setAuth(oauth2(NoOpsToken()))
                .build();
    }


    public static ResponseSpecification NoOPSTokenRes() {

        return  new ResponseSpecBuilder()

                .expectContentType(ContentType.JSON)
                .expectStatusCode(403)
                .build();
    }


    public static RequestSpecification expiredTokenReq() {

        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath("/api/secure/orders")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setAuth(oauth2((expiredToken())))
                .build();
    }


    public static ResponseSpecification expiredTokenRes() {

        return  new ResponseSpecBuilder()

                .expectContentType(ContentType.JSON)
                .expectStatusCode(401)
                .build();
    }

    public static RequestSpecification API()
    {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath("/api/partner/orders")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("X-API-KEY",RETAIL_API_KEY)
                .build();
    }





    //FULL FLOW


    public static RequestSpecification Logreq(String token)
    {
      return   new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath("/api/login")
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .setAuth(oauth2(token))
                .build();
    }

    public static ResponseSpecification Logres ()
    {
     return   new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }


    public static RequestSpecification addToCartreq(String token)
    {

    return    new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath("/api/cart/items")
                .setAccept("application/json")
                .setContentType(ContentType.JSON)
                .setAuth(oauth2(token))
                .build();
    }


    public static RequestSpecification placeOrder(String token)
    {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath("/api/orders")
                .setAccept("application/json")
                .setContentType(ContentType.JSON)
                .setAuth(oauth2(token))
                .build();
    }


    public static RequestSpecification reqXml()
    {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath("/api/legacy/products/101.xml")
                .setAccept("application/xml")
                .build();

    }
}
