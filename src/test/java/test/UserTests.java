package test;

import com.github.javafaker.Faker;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requestBuilder.UserRequestBuilder;
import io.restassured.response.Response;

@Test
public class UserTests {

    static String firstName;
    static String lastName;
    static String password;
    static String registeredEmail;

    static Faker faker = new Faker();

    @BeforeClass
    public static void setupData() {
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        registeredEmail = faker.internet().emailAddress();
        password = "7654321!";

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
   //  requestBuilder.AdminRequestBuilder.adminToken = response.jsonPath().getString("data.token");

    }

    @Test (priority = 3)
    public void testUserApproval() {
            Response response = requestBuilder.AdminRequestBuilder.approveUser();
            response.then().log().all();
            Assert.assertEquals(response.getStatusCode(),200);
        // Call the API to approve the user registration
    }

    @Test (priority = 4)
    public void testUserLogin() {
            Response response = UserRequestBuilder.userLogin(registeredEmail, password);
            response.then().log().all();
            Assert.assertEquals(response.getStatusCode(),200);
        // Call the API to login using userPayload.getEmail() and userPayload.getPassword()
    }


}
