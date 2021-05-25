package endpoint_tests;

import Dao.Dao;
import Dao.FakeImageDao;
import Dao.FakeUserDao;
import Interfaces.RepositoryInterface;
import entity.Image;
import entity.User;
import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import Fakes.Fakes;
import repository.Repository;
import util.EmailSender;
import util.UserUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

class ClientAreaServiceTest {

    private class FakeEmailSender implements EmailSender {
        public User user;
        @Override
        public void sendAuthEmail(User user) {
            this.user = user;
        }
    }

    @Test
    @Order(0)
    public void ItShouldReturnTheOverview() {
        // given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser = Fakes.getFakeVerifiedUser();
        repository.register(fakeUser);
        UserUtil.initProperties();

        //when
        String actualJsonString = repository.getOverview(fakeUser.getUsername());

        // then
        User temp = fakeUserDao.findOne(new Document());
        JSONObject jso = new JSONObject();
        jso.put("photos", 0)
                .put("disc_space", UserUtil.calculateDiscSpace(fakeImageDao.findAll(new Document()))
                        + " / " + UserUtil.getMaxDiscSpaceForUserGB(temp) + " GB")
                .put("subscription", temp.getSubscriptionStatus().toString())
                .put("notifications", 0);
        String expectedJson = jso.toString();

        Assertions.assertEquals(expectedJson, actualJsonString);

    }

    @Test
    public void ItShouldReturnOnePhoto() throws IOException {

        //given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser = Fakes.getFakeVerifiedUser();
        repository.register(fakeUser);
        File img = new File("coolImage.jpg");
        BufferedImage image = ImageIO.read(img);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image,"jpg", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
        FormDataContentDisposition fileMetaData = FormDataContentDisposition.name("coolImage.jpg").fileName("coolImage.jpg").build();

        //when
        repository.upload(inputStream, fileMetaData, fakeUser.getUsername());
        String actualJson = repository.getPhotos(fakeUser.getUsername());

        // then
        repository.delete("coolImage.jpg", fakeUser.getUsername());
        System.out.println(actualJson);

        Assertions.assertTrue(actualJson.contains("coolImage.jpg"));
    }

    @Test
    public void ItShouldReturnNoPhotos() {

        //given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser = Fakes.getFakeVerifiedUser();
        repository.register(fakeUser);

        //when
        String actualJson = repository.getPhotos(fakeUser.getUsername());

        // then
        repository.delete("coolImage.jpg", fakeUser.getUsername());

        Assertions.assertEquals( "[]", actualJson);
    }

}