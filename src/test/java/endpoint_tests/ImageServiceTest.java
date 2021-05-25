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
import org.junit.jupiter.api.Test;
import Fakes.Fakes;
import repository.Repository;
import util.EmailSender;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

class ImageServiceTest {

    private class FakeEmailSender implements EmailSender {
        public User user;
        @Override
        public void sendAuthEmail(User user) {
            this.user = user;
        }
    }

    @Test
    public void itShouldUploadAnImage_GivenTheImageDoesntExist() throws IOException {

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
        String givenjson = repository.upload(inputStream, fileMetaData, fakeUser.getUsername());

        //then
        JSONObject json = new JSONObject();
        json.put("status", "success")
                .put("fileName", "coolImage.jpg");
        String expectedjson = json.toString();
        repository.delete("coolImage.jpg", fakeUser.getUsername());

        Assertions.assertEquals(expectedjson, givenjson);
    }

    @Test
    public void itShouldFailToUploadAnImage_GivenTheImageDoesExist() throws IOException {

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
        String givenjson = repository.upload(inputStream, fileMetaData, fakeUser.getUsername());

        //then
        JSONObject json = new JSONObject();
        json.put("status", "failed")
                .put("exception", "Image already exists in this directory");
        String expectedjson = json.toString();
        repository.delete("coolImage.jpg", fakeUser.getUsername());

        Assertions.assertEquals(expectedjson, givenjson);
    }

    @Test
    public void itShouldDeleteAnImage_GivenTheImageDoesExist() throws IOException {

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
        String givenjson = repository.delete("coolImage.jpg", fakeUser.getUsername());

        //then
        JSONObject json = new JSONObject();
        json.put("status", "success")
                .put("fileName", "coolImage.jpg");
        String expectedjson = json.toString();

        Assertions.assertEquals(expectedjson, givenjson);
    }

    @Test
    public void itShouldNotDeleteAnImage_GivenTheImageDoesntExist() {

        //given
        Dao<Image> fakeImageDao = new FakeImageDao();
        Dao<User> fakeUserDao = new FakeUserDao();
        FakeEmailSender fakeEmailSender = new FakeEmailSender();
        RepositoryInterface repository = Repository.getInstance(fakeImageDao, fakeUserDao, fakeEmailSender);
        User fakeUser = Fakes.getFakeVerifiedUser();
        repository.register(fakeUser);

        //when
        String givenjson = repository.delete("coolImage.jpg", fakeUser.getUsername());

        //then
        JSONObject json = new JSONObject();
        json.put("status", "failed")
                .put("exception", "Image doesn't exist");
        String expectedjson = json.toString();

        Assertions.assertEquals(expectedjson, givenjson);
    }

    @Test
    public void itShouldPrepareTheUploadOfAnImage() throws IOException {

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
        Document doc = new Document("ok", "lol");
        Image image1 = fakeImageDao.findOne(doc);
        String givenjson = repository.prepareDownload(image1.getFilepath(), "jpeg");

        //then
        repository.delete("coolImage.jpg", fakeUser.getUsername());

        Assertions.assertTrue(givenjson.contains("success"));
    }

    @Test
    public void itShouldResetTheChangesOfAnImage() throws IOException {

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
        String givenjson = repository.reset("coolImage.jpg", fakeUser.getUsername());

        //then
        repository.delete("coolImage.jpg", fakeUser.getUsername());

        Assertions.assertTrue(givenjson.contains("success"));
    }

}