package test;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import requestBuilder.UserRequestBuilder;

import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;

public class ResponseSchemaTest {

    @Test
    public void validateRegisterResponseSchema() {
        // Arrange: create test data (reuse existing faker pattern)
        String firstName = "TestFirst";
        String lastName = "TestLast";
        String email = "schema_test_" + System.currentTimeMillis() + "@example.com";
        String password = "Pass123!";
        String groupId = "5328c91e-fc40-11f0-8e00-5000e6331276";

        // Act: call the register API
        Response response = UserRequestBuilder.registerUserRequest(firstName, lastName, email, password, groupId);

        // Assert: status code
        assertEquals(response.getStatusCode(), 201, "Expected HTTP 201 for user registration");

        // Assert: JSON schema validation
        String schemaPath = Paths.get("src/test/resources/schemas/register_user_schema.json").toAbsolutePath().toString();
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(Paths.get(schemaPath).toFile()));
    }
}
