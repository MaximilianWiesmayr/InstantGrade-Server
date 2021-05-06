package endpoint;

import Dao.Dao;
import Dao.FakeImageDao;
import Dao.FakeUserDao;
import Interfaces.RepositoryInterface;
import entity.Image;
import entity.User;
import enums.AccountType;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import repository.Repository;
import util.EmailSender;
import util.EmailUtil;
import util.Fakes;

class LoginServiceTest {

    @Test
    public void itShouldRegisterANewUser_GivenNeitherUsernameNorEmailIsInDatabase() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        EmailSender fakeEmailSender = new EmailSender() {
            public User user;
            @Override
            public void sendAuthEmail(User user) {
                this.user = user;
            }
        };
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser = Fakes.getFakeUnverifiedUser();

        // when
        String actualJsonString = repository.register(fakeUser);

        // then
        JSONObject json = new JSONObject();
        json.put("status", "success");
        json.put("username", fakeUser.getUsername());
        String expectedJsonString = json.toString();

        Assertions.assertEquals(expectedJsonString, actualJsonString);
    }

    private class FakeEmailSender implements EmailSender {
        public User user;
        @Override
        public void sendAuthEmail(User user) {
            this.user = user;
        }
    }
    @Test
    void itShouldSendAnEmailToUser_GivenTheUserCanBeRegistered() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser = Fakes.getFakeUnverifiedUser();

        // when
        repository.register(fakeUser);

        // then
        Assertions.assertNotNull(fakeEmailSender.user);
    }

    @Test
    public void itShouldNotRegisterANewUser_GivenUsernameAndEmailAreInDatabase() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        EmailSender fakeEmailSender = new EmailSender() {
            public User user;
            @Override
            public void sendAuthEmail(User user) {
                this.user = user;
            }
        };
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser1 = Fakes.getFakeUnverifiedUser();
        User fakeUser2 = Fakes.getFakeUnverifiedUser();

        // when
        repository.register(fakeUser1);
        String actualJsonString = repository.register(fakeUser2);

        // then
        JSONObject json = new JSONObject();
        json.put("status", "failed");
        json.put("exception", "Email or Username already exists");
        String expectedJsonString = json.toString();

        Assertions.assertEquals(expectedJsonString, actualJsonString);
    }

    @Test
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