package repository;


import Dao.ImageDao;
import Dao.UserDao;
import Interfaces.RepositoryInterface;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entity.Image;
import entity.User;
import enums.AccountType;
import org.apache.commons.io.FilenameUtils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONObject;
import util.EmailUtil;
import util.ImageUtil;
import util.SystemUtil;
import util.UserUtil;
import util.jwt.JWTHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * @author M. Wiesmayr
 */
public final class Repository implements RepositoryInterface {

    private List<User> users = new LinkedList<User>();


    private CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders((PojoCodecProvider.builder().automatic(true).build())));
    private static MongoClient client;
    private MongoDatabase igDB;
    private MongoCollection<User> userCollection;
    private MongoCollection<Image> imageCollection;

    private static ImageDao imageDao;
    private static UserDao userDao;
    private static Repository instance = null;

    private JWTHelper jwth = new JWTHelper();

    private Repository() {
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public void connectToDB() {
        // Set params for DB
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        client = MongoClients.create("mongodb://" + properties.getProperty("mongo.username") + ":"
                + properties.getProperty("mongo.password") + "@instantgrade.bastiarts.com:27017/?authSource=IG");

        igDB = client.getDatabase("IG").withCodecRegistry(pojoCodecRegistry);
        userCollection = igDB.getCollection("userCollection", User.class);
        imageCollection = igDB.getCollection("imageCollection", Image.class);
    }

    /**
     * @author Maximilian Wiesmayr
     */
    // uploads image to Server and Database
    public String upload(InputStream imageStream, FormDataContentDisposition fileMetaData, String owner) {
        JSONObject jsonImage = new JSONObject();

        Document doc = new Document("factoryName", fileMetaData.getName());
        doc.put("owner", owner);
        Image tempI = imageDao.findOne(doc);
        String filepath = ImageUtil.createFilepath(fileMetaData, owner);
        File tempFile = new File(filepath);
        if (tempI == null && !tempFile.exists()) {
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

            Image newImage = new Image();
            newImage.setFactoryName(fileMetaData.getFileName());
            newImage.setOwner(owner);
            newImage.setMetadata(ImageUtil.getMetadata(tempFile));
            Document doc2 = new Document("username", owner);
            User newUser = userDao.findOne(doc2);
            newUser.getImageFactoryName().add(newImage.getFactoryName());
            System.out.println("Path control: " + filepath);
            try {
                //Process process = Runtime.getRuntime().exec("python3 -c \"import createThumbnail;createThumbnail.generateThumbnail(\\\"" + "./" + filepath + "\\\")\"");
                Process process = Runtime.getRuntime().exec(new String[] {"python", "createThumbnail.py", "thumb", "./" + filepath});
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
            System.out.println("createThumbnail");
            String pathwithoutfile = FilenameUtils.getPath(filepath);
            System.out.println(pathwithoutfile);
            String filename = FilenameUtils.getName(filepath);
            String fileextension = FilenameUtils.getExtension(filepath);
            System.out.println(filename);
            filename = filename.split("\\.", 2)[0];
            System.out.println(filename);
            String[] splitpathwithoutfile = pathwithoutfile.split("/");
            newImage.setThumbnailPath(splitpathwithoutfile[0] + "/" + splitpathwithoutfile[1] + "/thumbnail/" + filename + "_thumb.jpg");
            newImage.setFilepath(splitpathwithoutfile[0] + "/" + splitpathwithoutfile[1] + "/" + filename + "." + fileextension);
            System.out.println(newImage.getFilepath());
            System.out.println(newImage.getThumbnailPath());
            imageDao.insertOne(newImage);

            jsonImage.put("status", "success");
            jsonImage.put("fileName", newImage.getFactoryName());

        } else {

            jsonImage.put("status", "failed");
            jsonImage.put("exception", "Image already exists in this directory");

        }
        return jsonImage.toString();

    }

    // create new User with email authentication
    public String register(User user) {
        System.out.println(user.getUsername() + " " + user.getEmail());
        JSONObject jsonUser = new JSONObject();
        Document doc = new Document("username", user.getUsername());
        doc.put("email", user.getEmail());
        User tmpU = userDao.findOne(doc);
        System.out.println("hi2");
        if (tmpU == null) {

            try {

                EmailUtil.emailauth(user);

            } catch (Exception e) {

                e.printStackTrace();

            }
            User newUser = new User();
            newUser.setUsername(user.getUsername());
            newUser.setFirstname(user.getFirstname());
            newUser.setLastname(user.getLastname());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(user.getPassword());
            userDao.insertOne(newUser);
            jsonUser.put("status", "success");
            jsonUser.put("username", user.getUsername());
            System.out.println("hi3");

        } else {

            jsonUser.put("status", "failed");
            jsonUser.put("exception", "User already exists");

        }
        return jsonUser.toString();
    }

    // login with jwt Token
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
        if (userDao.findOne(doc) == null) {
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
    public String getOverview(final String username) {
        User temp = userDao.findOne(new Document().append("username", username));
        JSONObject jso = new JSONObject();
        jso
                .put("photos", UserUtil.countAllImagesFromUser(username, imageCollection))
                .put("disc_space", UserUtil.calculateDiscSpace(username, imageCollection) + " / " + UserUtil.getMaxDiscSpaceForUserGB(temp) + " GB")
                .put("subscription", temp.getSubscriptionStatus().toString())
                .put("notifications", 0);
        return jso.toString();
    }

    // Gets all the Photos from a user in JSON
    public String getPhotos(final String username) {
        Document doc = new Document("owner", username);
        String liste = ImageUtil.parseImageList(imageDao.findAll(doc).into(new ArrayList<>()));
        System.out.println(liste);

        return ImageUtil.parseImageList(imageDao.findAll(doc).into(new ArrayList<>()));
    }

/*    public String getPhoto(String thumbnailPath) {

    }*/



    //deletes Image from Database and Server
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
            try {
                Process process = Runtime.getRuntime().exec(new String[] {"python", "createThumbnail.py", "delete", "./" + filepath});
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
            deleted.put("status", "success")
                    .put("fileName", name);
        }

        return deleted.toString();
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
}