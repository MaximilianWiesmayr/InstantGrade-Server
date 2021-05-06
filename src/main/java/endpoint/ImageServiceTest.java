package endpoint;

import entity.Image;
import entity.User;
import jdk.jfr.internal.tool.Main;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import util.Fakes;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ImageServiceTest {

    @Test
    @Order(0)
    public void testUpload() throws ParseException {
        ImageService imageService = new ImageService();
        User fakeUser = Fakes.getFakeVerifiedUser();
        InputStream inputStream = ImageServiceTest.class.getClassLoader().getResourceAsStream("coolImage.jpg");
        FormDataContentDisposition fileMetaData = new FormDataContentDisposition("header");

        String json = imageService.uploadImage(inputStream, fileMetaData, fakeUser.getUsername());
        System.out.println(json);
        Assertions.assertEquals(null, json);
    }

}