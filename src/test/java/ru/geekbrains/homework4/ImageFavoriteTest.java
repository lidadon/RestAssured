package ru.geekbrains.homework4;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static ru.geekbrains.Endpoints.*;
import static ru.geekbrains.homework4.ImageUploadTests.requestSpecificationWithAuthWithBase64;

public class ImageFavoriteTest extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/spongebob.png";
    private String imageId;
    private MultiPartSpecification base64MultiPartSpec;
    static String encodedFile;
    private Response response;
    private String deleteHash;
    private String successId;
    private String errorId;

    @BeforeEach
    void setUp() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

        response = given(requestSpecificationWithAuthWithBase64, positiveResponseSpec)
                .post(IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response();

        imageId = response.jsonPath().getString("data.id");
        deleteHash = response.jsonPath().getString("data.deleteHash");
    }

    @Test
    void imageFavoriteWithAuthTest() {
        successId = given(requestWithAuth, positiveResponseSpec)
                .post(FAVORITE_IMAGE, imageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("success");
    }

    @Test
    void imageFavoriteUnAuthTest() {
        errorId = given(requestUnAuth, unAuthResponseSpec)
                .post(FAVORITE_IMAGE, imageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("success");
    }

    private byte[] getFileContent() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
