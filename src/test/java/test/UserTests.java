package test;

import com.github.javafaker.Faker;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requestBuilder.AdminRequestBuilder;
import requestBuilder.UserRequestBuilder;
import io.restassured.response.Response;
import utils.DatabaseConnection;

import java.sql.SQLException;

@Test
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

        DatabaseConnection.dbConnection();

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
    public void testAdminLogin() {
        // Call the API to login as admin and store the token for future use
     Response response = requestBuilder.AdminRequestBuilder.adminLogin();
     response.then().log().all();
     Assert.assertEquals(response.getStatusCode(),200);

    }

    @Test (priority = 3)
    public void testUserApproval() {
            Response response = requestBuilder.AdminRequestBuilder.approveUser();
            response.then().log().all();
            Assert.assertEquals(response.getStatusCode(),200);

    }

    @Test (priority = 4)
    public void testUserLogin() {
            Response response = UserRequestBuilder.userLogin(registeredEmail, password);
            response.then().log().all();
            Assert.assertEquals(response.getStatusCode(),200);

    }

    @Test (priority = 5)
    public void testUserLoginWithInvalidCredentials() {
        Response response = UserRequestBuilder.userLogin(registeredEmail, "invalidPassword");
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(),401);
    }

    @Test (priority = 6)
    public void testUpdateUserRole() {
        Response response = requestBuilder.AdminRequestBuilder.updateUserRole(newRoleId);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(),200);
    }

    @Test(priority = 7)
    public void testGetCourses(){
        // Call the API to get courses and validate the response
       Response response = AdminRequestBuilder.getCourses("beginner", "automation");
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(),200);
    }


}
