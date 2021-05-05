package util;


import entity.User;
import entity.Image;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author Sebastian Schiefermayr
 */

public class UserUtil {
    private static Properties properties = new Properties();

    // Loads the config File
    public static void initProperties() {
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
    // Calculates the used Discspace
    public static String calculateDiscSpace(ArrayList<Image> userImages) {
        JSONObject metaStorage;
        double used_disc_space_MB = 0;
        DecimalFormat df2 = new DecimalFormat("#.##");
        for (Image img : userImages) {
            metaStorage = new JSONObject(img.getMetadata());
            used_disc_space_MB += Double.parseDouble(metaStorage.getString("File Size").split(" ")[0]) / (1024 * 1024);
        }
        return df2.format(used_disc_space_MB) + " MB";
    }

    // Returns the maximum Discspace per User in GB
    public static double getMaxDiscSpaceForUserGB(User user) {
        switch (user.getSubscriptionStatus()) {
            case BASIC:
                return (Double.parseDouble(properties.getProperty("user.basic.maxDiscSpaceMB")) / 1024);
            case PRO:
                return (Double.parseDouble(properties.getProperty("user.pro.maxDiscSpaceMB")) / 1024);
            case EXPERT:
                return (Double.parseDouble(properties.getProperty("user.expert.maxDiscSpaceMB")) / 1024);
            default:
                return (Double.parseDouble(properties.getProperty("user.basic.maxDiscSpaceMB")) / 1024);
        }
    }
}
