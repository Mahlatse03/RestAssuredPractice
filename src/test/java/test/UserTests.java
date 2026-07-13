package test;

import com.github.javafaker.Faker;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requestBuilder.AdminRequestBuilder;
import requestBuilder.UserRequestBuilder;
import io.restassured.response.Response;
import utils.DatabaseConnection;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;


public class UserTests {

    static String firstName;
    static String lastName;
    static String password;
    static String registeredEmail;
    static String newRoleId;

    static Faker faker = new Faker();

    @BeforeClass
    public static void setupData() throws SQLException {
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        registeredEmail = "Group2"+faker.internet().emailAddress();
        password = "7654321!";
        newRoleId = "admin";

        DatabaseConnection.dbConnection("playtest@gmail.com");

        System.out.println("First name:" + firstName);
        System.out.println("Last name: " + lastName);
        System.out.println("Registered email: " + registeredEmail);

    }

   @Test (priority = 1)
    public void testUserRegistration() {
        Response response = UserRequestBuilder.registerUserRequest(firstName, lastName, registeredEmail, password, "5328c91e-fc40-11f0-8e00-5000e6331276");
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(),201);
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

    @Test (priority = 3)
    public void testUserApproval() {
            requestBuilder.AdminRequestBuilder.approveUser()
                .then()
                    .log().all()
                    .assertThat()
                    .statusCode(200)
                    .body("success", equalTo(true));

    }

    @Test (priority = 4)
    public void testUserLogin() {
        //not assigning to response as we are not using it, just validating the response
            UserRequestBuilder.userLogin(DatabaseConnection.getEmailAddress, DatabaseConnection.getPassword)
                .then()
                    .log().all()
                    .assertThat()
                    .statusCode(200)
                    .body("success", equalTo(true));
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

    @Test (priority = 6)
    public void testUpdateUserRole() {
        requestBuilder.AdminRequestBuilder.updateUserRole(newRoleId)
        .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.role", equalTo(newRoleId)); // use (.body("role", equalTo(newRoleId)); to get class to participate
    }

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


}
