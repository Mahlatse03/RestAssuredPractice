package requestBuilder;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static payloadBuilder.userPayload.loginUserPayload;
import static payloadBuilder.userPayload.registerUserPayload;
import static requestBuilder.Routes.BASE_URL;


public class UserRequestBuilder {

    static String userToken;
    static String registeredUserId;

    public static Response registerUserRequest(String firstName, String lastName, String email, String password, String groupId) {

        String apiPath = "/APIDEV/register";
        Response response = given()
                .baseUri(BASE_URL)
                .basePath(apiPath)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(registerUserPayload(firstName, lastName, email, password,groupId))
                .post()
                .then().extract().response();
        registeredUserId = response.jsonPath().getString("data.id");
        return response;
        // Code to send a POST request to create an instructor
    }

    public static Response userLogin(String email, String password) {

        String apiPath = "/APIDEV/login";
        Response response = given()
                .baseUri(BASE_URL)
                .basePath(apiPath)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(loginUserPayload(email, password))
                .post()
                .then().extract().response();
        userToken = response.jsonPath().getString("data.token");
        return response;
        // Code to send a POST request to create an instructor
    }

}
