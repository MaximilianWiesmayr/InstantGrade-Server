package util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONArray;
import org.json.JSONObject;
import entity.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    // creates Filepath for Imageupload
    public static String createFilepath(FormDataContentDisposition fileMetaData, String owner) {
        File upload_dir = new File("uploads/" + owner);
        if (!upload_dir.exists()) {
            if (upload_dir.mkdirs()) {
                System.out.println("Directory " + upload_dir.getPath() + " successfully created!");
            } else {
                System.out.println("Directory could not been created.");
            }
        }
        return "uploads/" + owner + "/" + fileMetaData.getFileName();

    }

    // Moves Files to Trash
    public static boolean moveFileToTrash(File fileToTrash, String owner) {
        String trashPath = "trash/" + owner;
        File trashFolder = new File(trashPath);
        if (!trashFolder.exists()) {
            trashFolder.mkdirs();
            System.out.println("folder created");
        }
        System.out.println("ye");
        try {
            Files.move(fileToTrash.toPath(),
                    new File(fileToTrash.getPath().replace("uploads", "trash")).toPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getMetadata(File file) {
        try {
            Metadata m = ImageMetadataReader.readMetadata(file);


            JSONObject metaObject = new JSONObject();
            for (Directory directory : m.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    metaObject.put(tag.getTagName(), tag.getDescription());
                }
                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        System.err.format("ERROR: %s", error);
                    }
                }
            }
            return metaObject.toString();
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean recoverFileFromTrash(File fileFromTrash) {
        try {
            Files.move(new File(fileFromTrash.getPath()).toPath(),
                    new File(fileFromTrash.getPath().replace("trash", "uploads")).toPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
