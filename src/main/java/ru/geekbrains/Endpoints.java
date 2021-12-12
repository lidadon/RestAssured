package ru.geekbrains;

public class Endpoints {
    public static final String IMAGE = "/image";
    public static final String GET_ACCOUNT = "/account/{username}";
    public static final String GET_IMAGE = "/image/{imageHash}";
    public static final String UPLOAD_IMAGE = "/upload";
    public static final String DELETE_IMAGE = "/account/{username}/image/{deleteHash}";
    public static final String FAVORITE_IMAGE = "/image/{deleteHash}/favorite";

}
