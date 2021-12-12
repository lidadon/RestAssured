package ru.geekbrains.homework4;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;


public abstract class BaseTest {
    static ResponseSpecification positiveResponseSpec;
    static ResponseSpecification negativeResponseSpec;
    static ResponseSpecification unAuthResponseSpec;
    static RequestSpecification requestWithAuth;
    static RequestSpecification requestUnAuth;

    static Properties properties = new Properties();
    static String token;
    static String username;

    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "https://api.imgur.com/3";
        getProperties();
        token = properties.getProperty("token");
        username = properties.getProperty("username");

        positiveResponseSpec = new ResponseSpecBuilder()
                .expectBody("status", equalTo(200))
                .expectBody("success", is(true))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

        negativeResponseSpec = new ResponseSpecBuilder()
                .expectBody("status", equalTo(400))
                .expectBody("success", is(false))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(400)
                .build();

        unAuthResponseSpec = new ResponseSpecBuilder()
                .expectBody("status", equalTo(401))
                .expectBody("success", is(false))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(401)
                .build();

        requestWithAuth = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .build();

        requestUnAuth = new RequestSpecBuilder()
                .build();
    }

    private static void getProperties() {
        try (InputStream output = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}