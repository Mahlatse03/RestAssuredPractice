package test;

import com.github.javafaker.Faker;
import commons.Routes;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requestBuilder.AdminRequestBuilder;
import requestBuilder.UserRequestBuilder;
import io.restassured.response.Response;
import utils.DatabaseConnection;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.testng.Assert.assertEquals;


public class UserTests {

    static String firstName;
    static String lastName;
    static String password;
    static String registeredEmail;
    static String newRoleId;

    static Faker faker = new Faker();

    @BeforeClass
    public static void setupData() {
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        registeredEmail = "Group2"+faker.internet().emailAddress();
        password = "7654321!";
        newRoleId = "admin";

//        DatabaseConnection.insertUser(registeredEmail, password);
//        DatabaseConnection.getLoginsFromDB(registeredEmail);


        System.out.println("First name:" + firstName);
        System.out.println("Last name: " + lastName);
        System.out.println("Registered email: " + registeredEmail);


    }

   @Test (priority = 1)
    public void testUserRegistration() throws SQLException {
        Response response = UserRequestBuilder.registerUserRequest(firstName, lastName, registeredEmail, password, "5328c91e-fc40-11f0-8e00-5000e6331276");
        response.then().log().all()
                .extract().response();

        //Add registered user to DB
        DatabaseConnection.insertUser(registeredEmail, password);

        Assert.assertEquals(response.getStatusCode(),201);
        Assert.assertEquals(response.body().jsonPath().get("data.email"), registeredEmail);
        // Call the API to register the user using userPayload
    }

    @Test (priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    public void testAdminLogin() {
        // Call the API to login as admin and store the token for future use
     Response response = requestBuilder.AdminRequestBuilder.adminLogin();
     response.then().log().all();
     Assert.assertEquals(response.getStatusCode(),200);

    }

    @Test (dependsOnMethods = {"testAdminLogin", "testUserRegistration"})
    public void testUserApproval() {
            requestBuilder.AdminRequestBuilder.approveUser()
                .then()
                    .log().all()
                    .assertThat()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("data.approvalStatus", equalTo("approved"));

    }

//    @Test (priority = 4)
//    public void testUserLogin() {
//        //not assigning to response as we are not using it, just validating the response
//      UserRequestBuilder.userLogin(DatabaseConnection.getEmailAddress, DatabaseConnection.getPassword)
//                .then()
//                .log().all()
//                .assertThat()
//                .statusCode(200)
//                .body("success", equalTo(true));
//    }

    @Test (priority = 4)
    public void testUserLogin() throws SQLException {

        //Connect to the database and retrieve the email and password for the registered user
        DatabaseConnection.getLoginsFromDB(registeredEmail);
        //not assigning to response as we are not using it, just validating the response
        UserRequestBuilder.userLogin(DatabaseConnection.getEmailAddress, DatabaseConnection.getPassword)
        //UserRequestBuilder.userLogin(registeredEmail, password)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.token", notNullValue());
    }


    @Test (priority = 5)
    public void testUserLoginWithInvalidCredentials() {
        UserRequestBuilder.userLogin(registeredEmail, "invalidPassword")
            .then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("Invalid email or password"));

    }

//    @Test (priority = 6)
//    public void testUpdateUserRole() {
//        requestBuilder.AdminRequestBuilder.updateUserRole("instructor")
//                .then()
//                .log().all()
//                .assertThat()
//                .statusCode(200)
//                .body("success", equalTo(true))
//                .body("data.role", equalTo("instructor"));
//    }

   // @Test (dependsOnMethods = "testUpdateUserRole")
    @Test(priority = 7)
    public void testGetCourses(){
        // Call the API to get courses and validate the response
       AdminRequestBuilder.getCourses("beginner", "automation")
         .then()
            .log().all()
            .assertThat()
            .statusCode(200)
            .body("success", equalTo(true));
    }

    @Test(priority = 8)
    public void validateRegisterResponseSchema() {
        //generate email address specific for this test to avoid conflicts with other tests
        String schemaValidatorEmail = "Schema"+faker.internet().emailAddress();
        System.out.println("Schema email: " + schemaValidatorEmail);
        // Act: call the register API
       Response response = UserRequestBuilder.registerUserRequest(firstName, lastName, schemaValidatorEmail, password,
               "5328c91e-fc40-11f0-8e00-5000e6331276");

        // Assert: status code
        //Assert.assertEquals(response.getStatusCode(), 201, "Expected HTTP 201 for user registration");

        //using java.nio.file.Files and Paths to read the schema file
        try {
            String savedSchema = Files.readString(
                    Paths.get(Routes.JSON_SCHEMA_PATH, "register_user_schema.json"));
            System.out.println("Loaded JSON schema:\n" + savedSchema);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Assert: JSON schema validation
        String schemaPath = Paths.get(Routes.JSON_SCHEMA_PATH + "register_user_schema.json").toAbsolutePath().toString();
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(Paths.get(schemaPath).toFile()));
    }

}
