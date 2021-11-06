package repository;


import Dao.*;
import Interfaces.RepositoryInterface;
import entity.Image;
import entity.User;
import enums.AccountType;
import org.apache.commons.io.FilenameUtils;
import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONObject;
import util.*;
import util.jwt.JWTHelper;

import javax.json.Json;
import java.io.*;
import java.util.ArrayList;

/**
 * @author M. Wiesmayr
 */
public final class Repository implements RepositoryInterface {

    private static RepositoryInterface instance = null;

    private final Dao<Image> imageDao;
    private final Dao<User> userDao;
    private final EmailSender emailSender;

    private JWTHelper jwth = new JWTHelper();

    private Repository() {
            imageDao = new ImageDao();
            userDao = new UserDao();
            emailSender = new InstantGradeEmailSender();
            connectToDB();

    }

    public Repository(Dao<Image> imageDao, Dao<User> userDao, EmailSender emailSender) {
        this.imageDao = imageDao;
        this.userDao = userDao;
        this.emailSender = emailSender;
    }

    /**
     * Inject Repository for testing purposes
     * Warning: Only in Unit Test to be used
     * @param repository
     */
    public static void injectFakeRepository(RepositoryInterface repository){
        instance = repository;
    }

    public static RepositoryInterface getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public static RepositoryInterface getInstance(Dao<Image> imageDao, Dao<User> userDao, EmailSender emailSender) {
        if (instance == null) {
            instance = new Repository(imageDao, userDao, emailSender);
        }
        return instance;
    }

    private void connectToDB() {
        userDao.init();
        imageDao.init();
    }

    /**
     * @author Maximilian Wiesmayr
     */
    // uploads image to Server and Database
    @Override
    public String upload(InputStream imageStream, FormDataContentDisposition fileMetaData, String owner) {
        JSONObject jsonImage = new JSONObject();

        Document doc = new Document("factoryName", fileMetaData.getName());
        doc.put("owner", owner);
        Image tempI = imageDao.findOne(doc);
        String filepath = ImageUtil.createFilepath(fileMetaData, owner);
        File tempFile = new File(filepath);
        if (tempI == null && !tempFile.exists()) {
            saveImageFile(imageStream, tempFile);
            createThumbnail(filepath);
            Image newImage = addImagetoDatabase(fileMetaData, owner, tempFile, filepath);

            jsonImage.put("status", "success");
            jsonImage.put("fileName", newImage.getFactoryName());

        } else {

            jsonImage.put("status", "failed");
            jsonImage.put("exception", "Image already exists in this directory");

        }
        return jsonImage.toString();

    }

    private void saveImageFile(InputStream imageStream, File tempFile){
        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            OutputStream out = new FileOutputStream(tempFile);
            while ((read = imageStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.getStackTrace();
        } finally {
            try {
                imageStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Image addImagetoDatabase(FormDataContentDisposition fileMetaData, String owner, File tempFile, String filepath){
        Image newImage = new Image();
        newImage.setFactoryName(fileMetaData.getFileName());
        newImage.setOwner(owner);
        newImage.setMetadata(ImageUtil.getMetadata(tempFile));
        Document doc2 = new Document("username", owner);
        User newUser = userDao.findOne(doc2);
        newUser.getImageFactoryName().add(newImage.getFactoryName());
        String pathwithoutfile = FilenameUtils.getPath(filepath);
        String filename = FilenameUtils.getName(filepath);
        String fileextension = FilenameUtils.getExtension(filepath);
        filename = filename.split("\\.", 2)[0];
        String[] splitpathwithoutfile = pathwithoutfile.split("/");
        newImage.setThumbnailPath(splitpathwithoutfile[0] + "/" + splitpathwithoutfile[1] + "/thumbnail/" + filename + "_thumb.jpg");
        newImage.setFilepath(splitpathwithoutfile[0] + "/" + splitpathwithoutfile[1] + "/" + filename + "." + fileextension);
        imageDao.insertOne(newImage);

        return newImage;
    }

    private void createThumbnail(String filepath){
        try {
            //Process process = Runtime.getRuntime().exec("python3 -c \"import createThumbnail;createThumbnail.generateThumbnail(\\\"" + "./" + filepath + "\\\")\"");
            Process process = Runtime.getRuntime().exec(new String[] {"python", "createThumbnail.py", "thumb", "./" + filepath, "lol"});
            process.waitFor();
            BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // create new User with email authentication
    @Override
    public String register(User user) {
        JSONObject jsonUser = new JSONObject();
        Document usernamedoc = new Document("username", user.getUsername());
        Document emaildoc = new Document("email", user.getEmail());
        if (userDao.findOne(usernamedoc) == null && userDao.findOne(emaildoc) == null) {

            emailSender.sendAuthEmail(user);

            User newUser = new User();
            newUser.setUsername(user.getUsername());
            newUser.setFirstname(user.getFirstname());
            newUser.setLastname(user.getLastname());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(user.getPassword());
            userDao.insertOne(newUser);
            jsonUser.put("status", "success");
            jsonUser.put("username", user.getUsername());

        } else {

            jsonUser.put("status", "failed");
            jsonUser.put("exception", "Email or Username already exists");

        }
        return jsonUser.toString();
    }

    // login with jwt Token
    @Override
    public String login(User user) {
        JSONObject jsonUser = new JSONObject();
        Document doc = new Document("username", user.getUsername());
        doc.put("password", user.getPassword());
        User tmpU = userDao.findOne(doc);
        if (tmpU == null) {
            jsonUser.put("status", "failed");
            jsonUser.put("exception", "Invalid credentials");
        } else {
            if (tmpU.getAccountType() == AccountType.VERIFIED) {
                // Generate the JWT Token
                /**
                 * @author Sebastian Schiefermayr
                 * */
                tmpU.setAuthToken(jwth.createToken(tmpU.getUsername(), tmpU.getSubscriptionStatus()));
                jsonUser.put("status", "success");
                jsonUser.put("user", SystemUtil.buildUserJSON(tmpU));
                jsonUser.put("token", tmpU.getAuthToken());
            } else {
                jsonUser.put("status", "failed");
                jsonUser.put("exception", "email not verified");
            }

        }

        return jsonUser.toString();
    }

    // verify user with verification Code
    @Override
    public String verify(String verifivationCode) {

        JSONObject jsonstatus = new JSONObject();

        String username = EmailUtil.decrypt(verifivationCode);
        if (username == null) {
            jsonstatus.put("status", "fail");
            jsonstatus.put("exception", "Verification Code invalid");
            return jsonstatus.toString();
        }
        Document doc = new Document("username", username);
        User user = userDao.findOne(doc);
        if (!user.getUsername().equals(username)) {
            jsonstatus.put("status", "fail");
            jsonstatus.put("exception", "User not found");
        } else {
            user.setAccountType(AccountType.VERIFIED);
            userDao.replaceOne(doc, user);
            jsonstatus.put("status", "success");
        }

        return jsonstatus.toString();
    }

    /**
     * ================================================
     *                  CLIENT AREA
     * ================================================
     * @author Sebastian Schiefermayr
     * @since 16.08.2019
     * @version 1.0
     * Handles all the Requests according to the Client Area
     * */

    /**
     * @param username - UNIQUE - Is the Key, to search in the Database.
     * @implNote It could return null, if the User doesn't exists
     */
    @Override
    public String getOverview(final String username) {
        User temp = userDao.findOne(new Document().append("username", username));
        JSONObject jso = new JSONObject();
        try {
            jso
                    .put("photos", countAllImagesFromUser(username))
                    .put("disc_space", UserUtil.calculateDiscSpace(getUserImages(username)))
                    .put("subscription", temp.getSubscriptionStatus().toString())
                    .put("notifications", 0);
        } catch (Exception e){
            e.printStackTrace();
        }
        return jso.toString();
    }

    // Counts the Images from the DB
    private int countAllImagesFromUser(final String username) {
        return (int) imageDao.countDocuments("owner", username);
    }

    private ArrayList<Image> getUserImages(String username) {
        return imageDao.findAll(new Document("owner", username));
    }

    // Gets all the Photos from a user in JSON
    @Override
    public String getPhotos(final String username) {
        Document doc = new Document("owner", username);
        String liste = ImageUtil.parseImageList(imageDao.findAll(doc));
        System.out.println(liste);

        return ImageUtil.parseImageList(imageDao.findAll(doc));
    }

/*    public String getPhoto(String thumbnailPath) {

    }*/



    //deletes Image from Database and Server
    @Override
    public String delete(String name, String owner) {

        JSONObject deleted = new JSONObject();

        File file = new File("uploads/" + owner + "/" + name);

        //  file.delete();
        String filepath = "uploads/" + owner + "/" + name;
        Document doc = new Document("filepath", filepath);
        Image deletetone = imageDao.findOneAndDelete(doc);
        if (deletetone == null) {
            deleted.put("status", "failed")
                    .put("exception", "Image doesn't exist");
        } else {
            removeImageFile(filepath);
            deleted.put("status", "success")
                    .put("fileName", name);
        }

        return deleted.toString();
    }

    private void removeImageFile(String filepath){
        try {
            Process process = Runtime.getRuntime().exec(new String[] {"python", "createThumbnail.py", "delete", "./" + filepath, "lol"});
            process.waitFor();
            BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String recover(String filename, String owner) {
        File file = new File("trash/" + owner + "/" + filename);
        JSONObject recovered = new JSONObject();
        if (ImageUtil.recoverFileFromTrash(file)) {
            recovered.put("status", "success")
                    .put("fileName", filename);
        } else {
            recovered.put("status", "failed")
                    .put("exception", "Image doesn't exist");
        }
        return recovered.toString();
    }

    public String edit(String oldName, String newName, String owner) {

        JSONObject renamed = new JSONObject();


        File file = new File("uploads/" + owner + "/" + oldName);

        file.renameTo(new File("uploads/" + owner + "/" + newName));

        Document doc = new Document("filepath", "uploads/" + owner + "/" + oldName);
        Image newImage = imageDao.findOneAndDelete(doc);
        if (newImage == null) {

            renamed.put("status", "failed")
                    .put("exception", "Image doesn't exist");

        } else {

            newImage.setCustomName(newName);
            newImage.setFilepath("uploads/" + owner + "/" + newName);
            imageDao.insertOne(newImage);

            renamed.put("status", "success")
                    .put("fileName", newName);


        }


        return null;
    }

    @Override
    public String prepareDownload(String filepath, String type) {
        JSONObject download = new JSONObject();
        try {
            Process process = Runtime.getRuntime().exec(new String[] {"python", "createThumbnail.py", "prepareDownload", "./" + filepath, type});
            process.waitFor();
            BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        String pathwithoutfile = FilenameUtils.getPath(filepath);
        String filename = FilenameUtils.getName(filepath);
        String fileNameWithOutExt = FilenameUtils.removeExtension(filename);
        download.put("status", "success");
        download.put("path", pathwithoutfile + "forDownload/" + fileNameWithOutExt + "." + type);

        return download.toString();
    }

    @Override
    public String reset(String name, String owner){
        String filepath = "uploads/" + owner + "/" + name;
        try {
            Process process = Runtime.getRuntime().exec(new String[] {"python", "createThumbnail.py", "reset", "./" + filepath, "lol"});
            process.waitFor();
            BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Process process = Runtime.getRuntime().exec(new String[] {"python", "createThumbnail.py", "thumb", "./" + filepath, "lol"});
            process.waitFor();
            BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject reset = new JSONObject();
        reset.put("status", "success")
                .put("fileName", name);

        return reset.toString();
    }
}
