package requestBuilder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static requestBuilder.Routes.BASE_URL;
import static requestBuilder.UserRequestBuilder.userLogin;

public class AdminRequestBuilder {

    static public String adminToken;
    static public String registeredUserId;

    public static Response adminLogin(){
            Response response =  userLogin("admin@gmail.com", "@12345678");  // get from database at a later stage
            adminToken = response.jsonPath().getString("data.token");
            return response;

    }

    public static Response approveUser() {

        String apiPath = "/APIDEV/admin/users/" + UserRequestBuilder.registeredUserId + "/approve";
        System.out.println("Approval API" + apiPath);
        return RestAssured.given()
                .baseUri(BASE_URL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .log().all()
                .put()
                .then().extract().response();

    }


}
