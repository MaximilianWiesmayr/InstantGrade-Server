package endpoint;

import entity.User;
import enums.AccountType;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import repository.Repository;
import util.EmailUtil;
import util.Fakes;

class LoginServiceTest {

    @BeforeAll
    public static void setup() {
        Repository.setIsInTestMode();
    }

    @Test
    @Order(0)
    public void testSuccessfulRegister() {
        LoginService loginService = new LoginService();

        User fakeUser = Fakes.getFakeUnverifiedUser();
        String actualJsonString = loginService.register(fakeUser);

        JSONObject json = new JSONObject();
        json.put("status", "success");
        json.put("username", fakeUser.getUsername());
        String expectedJsonString = json.toString();

        Assertions.assertEquals(expectedJsonString, actualJsonString);
    }

    @Test
    @Order(1)
    public void testUnsuccessfulRegister() {
        LoginService loginService = new LoginService();

        User fakeUser = Fakes.getFakeUnverifiedUser();
        String actualJsonString = loginService.register(fakeUser);

        JSONObject json = new JSONObject();
        json.put("status", "failed");
        json.put("exception", "Email or Username already exists");
        String expectedJsonString = json.toString();

        Assertions.assertEquals(expectedJsonString, actualJsonString);
    }

    @Test
    @Order(2)
    public void testVerification() {
        LoginService loginService = new LoginService();

        User fakeUser = Fakes.getFakeUnverifiedUser();
        String verificationCode = EmailUtil.encrypt(fakeUser.getUsername());
        String actualJsonString = loginService.verify(verificationCode);
        JSONObject json = new JSONObject();
        json.put("status", "success");
        String expectedJsonString = json.toString();

        Assertions.assertEquals(expectedJsonString, actualJsonString);
    }

}