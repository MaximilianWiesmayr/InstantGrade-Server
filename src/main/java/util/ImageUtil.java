package util;

import entity.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Sebastian Schiefermayr
 */
public class ImageUtil {
    public static JSONObject parseImageJSON(Image img) {
        return new JSONObject()
                .put("customName", img.getCustomName())
                .put("factoryName", img.getFactoryName())
                .put("owner", img.getOwner())
                .put("filepath", img.getFilepath())
                .put("thumbnailPath", img.getThumbnailPath())
                .put("metadata", img.getMetadata());
    }

    public static String parseImageList(ArrayList<Image> list) {
        JSONArray jso = new JSONArray();
        for (Image img : list) {
            jso.put(parseImageJSON(img));
        }
        return jso.toString();
    }

    /**
     * @param daysBack - Days until deletion
     * @param dir_path - Directory Path
     *                 This Method gets Triggered by a CronJob
     */
    public static void deleteFilesOlderThanNdays(final int daysBack, final String dir_path) {

        final File directory = new File(dir_path);
        if (directory.exists()) {
            System.out.println(directory.listFiles()[0].getPath());
            final File[] dirs = directory.listFiles();
            long purgeTime = System.currentTimeMillis() - (daysBack * 24L * 60L * 60L * 1000L);
            if (dirs != null && dirs.length > 0) {
                for (File folder : dirs) {
                    for (File image : folder.listFiles()) {
                        if (image.lastModified() < purgeTime) {
                            System.out.println("Inside File Delete");
                            SystemUtil.logToFile("File_deletion_log", image.getPath() + " deleted.");
                        }
                    }
                }
            }
        }
    }
}
