package Interfaces;

import entity.User;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.InputStream;

public interface RepositoryInterface {
    String register(User user);
    String login(User user);
    String verify(String verifivationCode);
    String getOverview(final String username);
    String getPhotos(final String username);
    String upload(InputStream imageStream, FormDataContentDisposition fileMetaData, String owner);
    String delete(String name, String owner);
}
