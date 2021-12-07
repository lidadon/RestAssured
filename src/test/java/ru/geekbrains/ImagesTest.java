package ru.geekbrains;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ImagesTest extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/spongebob.png";
    private final String PATH_TO_IMAGE_WEB = "https://slovnet.ru/wp-content/uploads/2018/11/26-9.png";
    static String encodedFile;
    String uploadedImageId;
    String successId;
    String errorId;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
    }

    @Test
    void uploadFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadFileImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/spongebob.png"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
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

    @Test
    void uploadUrlImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("type", "url")
                .multiPart("image", PATH_TO_IMAGE_WEB)
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadEmptyTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .expect()
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.error");
    }

    @Test
    void uploadTypeImageWithoutAFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("type", "png")
                .expect()
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.error");
    }

    @Test
    void uploadBase65WithFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("type", "base64")
                .multiPart("image", new File("src/test/resources/spongebob.png"))
                .expect()
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.error");
    }

    @Test
    void favoriteImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/spongebob.png"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

    successId = given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/"+ uploadedImageId + "/favorite")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("success");
    }

    @Test
    void imageDeleteAuthTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/spongebob.png"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

        successId = given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .when()
                .delete("https://api.imgur.com/3/image/"+ uploadedImageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("success");
    }

    @Test
    void imageDeleteUnAuthTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/spongebob.png"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

        errorId = given()
                .expect()
                .statusCode(401)
                .when()
                .delete("https://api.imgur.com/3/image/"+ uploadedImageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.error");
    }

    @AfterEach
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
