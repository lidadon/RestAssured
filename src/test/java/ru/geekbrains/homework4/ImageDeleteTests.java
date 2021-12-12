package ru.geekbrains.homework4;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.geekbrains.dto.PostImageResponse;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static ru.geekbrains.Endpoints.DELETE_IMAGE;
import static ru.geekbrains.Endpoints.IMAGE;
import static ru.geekbrains.homework4.ImageUploadTests.requestSpecificationWithAuthWithBase64;

public class ImageDeleteTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/spongebob.png";
    private String imageId;
    private MultiPartSpecification base64MultiPartSpec;
    static String encodedFile;
    private Response response;
    private String deleteHash;
    private String successId;
    private String errorId;

    static RequestSpecification requestSpecificationWithAuthTest;
    static RequestSpecification requestSpecificationUnAuthTest;

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
    void imageDeleteWithAuthTest() {
        successId = given(requestWithAuth, positiveResponseSpec)
                .delete(DELETE_IMAGE, username, imageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("success");
    }

    @Test
    void imageDeleteUnAuthTest() {
        errorId = given(requestUnAuth, unAuthResponseSpec)
                .delete(DELETE_IMAGE, username, imageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getError();
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
