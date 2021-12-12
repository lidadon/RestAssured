package ru.geekbrains.homework4;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
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
import static ru.geekbrains.Endpoints.*;

public class ImageUploadTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/spongebob.png";
    private final String PATH_TO_IMAGE_WEB = "https://slovnet.ru/wp-content/uploads/2018/11/26-9.png";
    static String encodedFile;
    String imageId;

    MultiPartSpecification base64MultiPartSpec;
    MultiPartSpecification multiPartSpecWithFile;

    static RequestSpecification requestSpecificationWithAuthAndMultiPartImage;
    static RequestSpecification requestSpecificationWithAuthWithBase64;
    static RequestSpecification requestSpecificationWithAuthFromWeb;
    static RequestSpecification requestSpecificationWithAuthEmptyTest;
    static RequestSpecification requestSpecificationWithAuthTypeImageWithoutAFileTest;
    static RequestSpecification requestSpecificationWithAuthBase64WithFileTest;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        multiPartSpecWithFile = new MultiPartSpecBuilder(new File(PATH_TO_IMAGE))
                .controlName("image")
                .build();

        requestSpecificationWithAuthAndMultiPartImage = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("title", "sponge")
                .addMultiPart(multiPartSpecWithFile)
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

        requestSpecificationWithAuthFromWeb = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart("type", "url")
                .addMultiPart("image", PATH_TO_IMAGE_WEB)
                .build();

        requestSpecificationWithAuthEmptyTest = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .build();

        requestSpecificationWithAuthTypeImageWithoutAFileTest = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart("type", "png")
                .build();

        requestSpecificationWithAuthBase64WithFileTest = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart("type", "base64")
                .addMultiPart("image", PATH_TO_IMAGE)
                .build();
    }

    @Test
    void uploadFileTest() {
        imageId = given(requestSpecificationWithAuthWithBase64, positiveResponseSpec)
                .post(IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getDeletehash();
    }

    @Test
    void uploadFileImageTest() {
        imageId = given(requestSpecificationWithAuthAndMultiPartImage, positiveResponseSpec)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getDeletehash();
    }

    @Test
    void uploadUrlImageTest() {
        imageId = given(requestSpecificationWithAuthFromWeb, positiveResponseSpec)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getDeletehash();
    }

    @Test
    void uploadEmptyTest() {
        imageId = given(requestSpecificationWithAuthEmptyTest, negativeResponseSpec)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getError();
    }

    @Test
    void uploadTypeImageWithoutAFileTest() {
        imageId = given(requestSpecificationWithAuthTypeImageWithoutAFileTest, negativeResponseSpec)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getError();
    }

    @Test
    void uploadBase64WithFileTest() {
        imageId = given(requestSpecificationWithAuthBase64WithFileTest, negativeResponseSpec)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getError();
    }

    @AfterEach
    void tearDown() {
        given(requestWithAuth)
                .delete(DELETE_IMAGE, username, imageId)
                .prettyPeek()
                .then()
                .statusCode(200);
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
