package util;


import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * @author Sebastian Schiefermayr
 */

public class UserUtil {
    // Calculates the used Discspace
    public static String calculateDiscSpace(final String username, MongoCollection collection) {
        // TODO
        return null;
    }

    // Counts the Images from the DB
    public static int countAllImagesFromUser(final String username, MongoCollection collection) {
        return (int) collection.countDocuments(new Document("owner", username));
    }

}
