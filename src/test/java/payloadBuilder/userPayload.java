package payloadBuilder;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;

public class userPayload {

    public static JSONObject loginUserPayload(String email, String password) {

        JSONObject loginUser = new JSONObject(); //instantiate loginUser object of type JSONObject
        loginUser.put("email", email); //putting key-value pairs in the loginUser object
        loginUser.put("password", password);

        return loginUser;
    }

    //example of how the object will look in void method
    @Test
    public void examplePayload(){
        JSONObject examplePayload = new JSONObject();
        examplePayload.put("key1", "value1");
        examplePayload.put("key2", "value2");

        System.out.println(examplePayload.toJSONString());
    }

    public static JSONObject registerUserPayload(String firstName, String lastName, String email, String password, String groupId) {
        JSONObject registerUser = new JSONObject();
        registerUser.put("firstName", firstName);
        registerUser.put("lastName", lastName);
        registerUser.put("email", email);
        registerUser.put("password", password);
        registerUser.put("confirmPassword", password);
        registerUser.put("groupId", groupId);

        return registerUser;
    }

    public static JSONObject updateUserRolePayload(String roleId) {
        JSONObject updateUserRole = new JSONObject();
        updateUserRole.put("role", roleId);

        return updateUserRole;
    }
}
