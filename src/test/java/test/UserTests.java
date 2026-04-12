package test;

import com.github.javafaker.Faker;
import requestBuilder.UserRequestBuilder;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.BeforeClass;

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
        password = "654321!";

    }

    public void testUserRegistration() {
        Response response = UserRequestBuilder.registerUserRequest(firstName, lastName, registeredEmail, password, "5328c91e-fc40-11f0-8e00-5000e6331276");
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(),200);
        // Call the API to register the user using userPayload
    }

    public void testUserLogin() {
        // Call the API to login using userPayload.getEmail() and userPayload.getPassword()
    }

    public void testUserApproval() {
        // Call the API to approve the user registration
    }
}
