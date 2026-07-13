package requestBuilder;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.DatabaseConnection;

import static io.restassured.RestAssured.given;
import static payloadBuilder.userPayload.updateUserRolePayload;
import static commons.Routes.BASE_URL;
import static requestBuilder.UserRequestBuilder.userLogin;

public class AdminRequestBuilder {

    static public String adminToken;

    public static Response adminLogin(){
      //Response response = userLogin(DatabaseConnection.getEmailAddress, DatabaseConnection.getPassword); //getting from DDB
        //Calling the userLogin requestbuilder - reusability!
        Response response =  userLogin("admin@gmail.com", "@12345678");  // get from database at a later stage
        adminToken = response.jsonPath().getString("data.token");
            return response;

    }

    public static Response approveUser() {

       /* String apiPath = "/APIDEV/admin/users/" + UserRequestBuilder.registeredUserId + "/approve";
        System.out.println("Approval API" + apiPath);
        return RestAssured.given()
                    .baseUri(BASE_URL)
                    .basePath(apiPath)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + adminToken)
                    .log().all()
                .when() //optional
                    .put()
                .then()
                    .extract().response();*/

        String apiPath = "/APIDEV/admin/users/{userId}/approve";
        return given()
                .baseUri(BASE_URL)
                .basePath(apiPath)
                .pathParams("userId", UserRequestBuilder.registeredUserId)
                .header("Content-Type", "application/json") //More flexible for custom or uncommon headers, Manually sets the HTTP header
                .header("Authorization", "Bearer " + adminToken)
                .log().all()
            .when()
                .put()
            .then()
                .extract().response();

    }

    public static Response updateUserRole(String newRoleId) {

        String apiPath = "/APIDEV/admin/users/" + UserRequestBuilder.registeredUserId + "/role";
        System.out.println("Update user role API" + apiPath);
        return given()
                    .baseUri(BASE_URL)
                    .basePath(apiPath)
                    .contentType(ContentType.JSON) //Uses an enum (ContentType.JSON) instead of a raw string, safer and clearer in intent
                    .accept(ContentType.JSON)
                    .header("Authorization", "Bearer " + adminToken)
                    .body(updateUserRolePayload(newRoleId))
                .when()  //optional
                    .put()
                .then()
                    .extract().response();
        // Code to send a POST request to create an instructor
    }

    public static Response getCourses(String level, String search) {
        String apiPath = "/APIDEV/courses";
        return given()
                    .baseUri(BASE_URL)
                    .basePath(apiPath)
                    .queryParams("level", level, "search", search)
                    .header("Content-Type", "application/json")
                    .log().all()
                .when()
                    .get()
                .then()
                    .extract().response();
    }


}
