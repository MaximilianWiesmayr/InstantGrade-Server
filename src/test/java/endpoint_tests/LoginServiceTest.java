package endpoint_tests;

import Dao.Dao;
import Dao.FakeImageDao;
import Dao.FakeUserDao;
import Interfaces.RepositoryInterface;
import entity.Image;
import entity.User;
import enums.AccountType;
import org.bson.Document;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import repository.Repository;
import util.EmailSender;
import util.EmailUtil;
import Fakes.Fakes;

class LoginServiceTest {

    private class FakeEmailSender implements EmailSender {
        public User user;
        @Override
        public void sendAuthEmail(User user) {
            this.user = user;
        }
    }

    @Test
    public void itShouldRegisterANewUser_GivenNeitherUsernameNorEmailIsInDatabase() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
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
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
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
    public void itShouldNotRegisterANewUser_GivenEmailIsInDatabase() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser1 = Fakes.getFakeVerifiedUser();
        User fakeUser2 = Fakes.getFakeUnverifiedUserWithSameEmailButDifferentUsername();

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
    public void itShouldNotRegisterANewUser_GivenUsernameIsInDatabase() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser1 = Fakes.getFakeVerifiedUser();
        User fakeUser2 = Fakes.getFakeUnverifiedUserWithSameUsernameButDifferentEmail();

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
    public void ItShouldVerifyTheUser_GivenTheUserWasFoundInTheDatabase() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser = Fakes.getFakeUnverifiedUser();
        String verificationCode = EmailUtil.encrypt(fakeUser.getUsername());
        repository.register(fakeUser);

        // when
        String actualJsonString = repository.verify(verificationCode);

        // then
        JSONObject json = new JSONObject();
        json.put("status", "success");
        String expectedJsonString = json.toString();

        Assertions.assertEquals(expectedJsonString, actualJsonString);
        Assertions.assertEquals(AccountType.VERIFIED, fakeUserDao.findOne(new Document("username", fakeUser.getUsername())).getAccountType());
    }

    @Test
    public void ItShouldNotVerifyTheUser_GivenTheVerificationCodeIsInvalid() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser = Fakes.getFakeUnverifiedUser();
        String verificationCode = null;
        repository.register(fakeUser);

        // when
        String actualJsonString = repository.verify(verificationCode);

        // then
        JSONObject json = new JSONObject();
        json.put("status", "fail");
        json.put("exception", "Verification Code invalid");
        String expectedJsonString = json.toString();

        Assertions.assertEquals(expectedJsonString, actualJsonString);
        Assertions.assertEquals(AccountType.NOT_VERIFIED, fakeUserDao.findOne(new Document("username", fakeUser.getUsername())).getAccountType());
    }

    @Test
    public void ItShouldNotVerifyTheUser_GivenTheUserWasNotFoundInTheDatabase() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser = Fakes.getFakeUnverifiedUser();
        String verificationCode = EmailUtil.encrypt("nonExistingUser");
        repository.register(fakeUser);

        // when
        String actualJsonString = repository.verify(verificationCode);

        // then
        JSONObject json = new JSONObject();
        json.put("status", "fail");
        json.put("exception", "User not found");
        String expectedJsonString = json.toString();

        Assertions.assertEquals(expectedJsonString, actualJsonString);
        Assertions.assertEquals(AccountType.NOT_VERIFIED, fakeUserDao.findOne(new Document("username", fakeUser.getUsername())).getAccountType());
    }

    @Test
    public void ItShouldLoginAUser_GivenTheUserExistsAndIsVerified() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser1 = Fakes.getFakeVerifiedUser();

        // when
        repository.register(fakeUser1);
        String verificationCode = EmailUtil.encrypt(fakeUser1.getUsername());
        repository.verify(verificationCode);
        String actualJsonString = repository.login(fakeUser1);

        // then
        Assertions.assertTrue(actualJsonString.contains("success"));
    }

    @Test
    public void ItShouldNotLoginAUser_GivenTheUserExistsAndIsNotVerified() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser1 = Fakes.getFakeVerifiedUser();

        // when
        repository.register(fakeUser1);
        String actualJsonString = repository.login(fakeUser1);

        // then
        Assertions.assertTrue(actualJsonString.contains("email not verified"));
    }

    @Test
    public void ItShouldNotLoginAUser_GivenTheUserDoesntExist() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser1 = Fakes.getFakeVerifiedUser();

        // when
        String actualJsonString = repository.login(fakeUser1);

        // then
        Assertions.assertTrue(actualJsonString.contains("Invalid credentials"));
    }

}
