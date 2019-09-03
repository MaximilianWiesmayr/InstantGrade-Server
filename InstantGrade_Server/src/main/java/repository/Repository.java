package repository;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entity.Image;
import entity.User;
import enums.AccountType;
import org.apache.commons.codec.binary.Base64;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONObject;
import util.ImageUtil;
import util.UserUtil;
import util.jwt.JWTHelper;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * @author M. Wiesmayr
 */
public final class Repository {

    private List<User> users = new LinkedList<User>();


    private CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders((PojoCodecProvider.builder().automatic(true).build())));
    private static MongoClient client;
    private MongoDatabase igDB;
    private MongoCollection<User> userCollection;
    private MongoCollection<Image> imageCollection;

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
        client = MongoClients.create("mongodb://" + properties.getProperty("mongo.username") + ":" + properties.getProperty("mongo.password") + "@instantgrade.bastiarts.com/?authSource=IG");
        igDB = client.getDatabase("IG").withCodecRegistry(pojoCodecRegistry);
        userCollection = igDB.getCollection("userCollection", User.class);
        imageCollection = igDB.getCollection("imageCollection", Image.class);
    }

    // decryption for Token from Email authentication
    private static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            return null;
        }
    }

    // encryption for Token from Email authentication
    private static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Email authentication for registration
    private void emailauth(User user) throws Exception {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "webmail.bastiarts.com");
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(props, auth);
        // uncomment for debugging infos to stdout
        // mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();

        String verificationCode = encrypt(user.getUsername());
        String encodingOptions = "text/html; charset=UTF-8";
        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject("Verify your Account", "UTF-8");
        message.setHeader("Content-Type", encodingOptions);
        message.setContent(this.buildHTML(verificationCode), "text/html");
        message.setFrom(new InternetAddress("instantgrade@bastiarts.com"));
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(user.getEmail()));

        transport.connect();
        transport.sendMessage(message,
                message.getRecipients(Message.RecipientType.TO));
        transport.close();

    }

    /**
     * @author Maximilian Wiesmayr
     */
    // uploads image to Server and Database
    public String upload(InputStream imageStream, FormDataContentDisposition fileMetaData, String owner) {
        JSONObject jsonImage = new JSONObject();

        Document doc = new Document("factoryName", fileMetaData.getName());
        doc.put("owner", owner);
        Image tempI = imageCollection.find(doc).first();
        String filepath = createFilepath(fileMetaData, owner);
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
            }

            Image newImage = new Image();
            newImage.setFactoryName(fileMetaData.getFileName());
            newImage.setOwner(owner);
            newImage.setMetadata(getMetadata(tempFile));
            newImage.setFilepath(filepath);
            this.imageCollection.insertOne(newImage);

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
        JSONObject jsonUser = new JSONObject();

        Document doc = new Document("username", user.getUsername());
        doc.put("email", user.getEmail());
        User tmpU = userCollection.find(doc).first();
        if (tmpU == null) {

            try {

                emailauth(user);

            } catch (Exception e) {

                e.printStackTrace();

            }
            User newUser = new User();
            newUser.setUsername(user.getUsername());
            newUser.setFirstname(user.getFirstname());
            newUser.setLastname(user.getLastname());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(user.getPassword());
            this.userCollection.insertOne(newUser);
            jsonUser.put("status", "success");
            jsonUser.put("username", user.getUsername());

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
        User tmpU = userCollection.find(doc).first();
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
                jsonUser.put("user", buildUserJSON(tmpU));
                jsonUser.put("token", tmpU.getAuthToken());
            } else {
                jsonUser.put("status", "failed");
                jsonUser.put("exception", "email not verified");
            }

        }

        return jsonUser.toString();
    }

    public String test() {
        String originalString = "password";
        System.out.println("Original String to encrypt - " + originalString);
        String encryptedString = encrypt(originalString);
        System.out.println("Encrypted String - " + encryptedString);
        String decryptedString = decrypt(encryptedString);
        System.out.println("After decryption - " + decryptedString);

        return "successful test";
    }

    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    // verify user with verification Code
    public String verify(String verifivationCode) {

        JSONObject jsonstatus = new JSONObject();

        String username = decrypt(verifivationCode);
        if (username == null) {
            jsonstatus.put("status", "fail");
            jsonstatus.put("exception", "Verification Code invalid");
            return jsonstatus.toString();
        }
        Document doc = new Document("username", username);
        User user = userCollection.find(doc).first();
        if (userCollection.find(doc).first() == null) {
            jsonstatus.put("status", "fail");
            jsonstatus.put("exception", "User not found");
        } else {
            user.setAccountType(AccountType.VERIFIED);
            this.userCollection.replaceOne(doc, user);
            jsonstatus.put("status", "success");
        }

        return jsonstatus.toString();
    }

    //login for email sender for Email authentication
    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = "instantgrade@bastiarts.com";
            String password = "6Tmv0?h4";
            return new PasswordAuthentication(username, password);
        }
    }

    /**
     * @author Sebastian Schiefermayr
     * This Method builds the HTML format for the Mail
     */
    private String buildHTML(String verifyCode) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("<div style = 'text-align: center; margin: 0 auto; width: 50%;'>")
                .append("<h1 style = 'background-image: linear-gradient(90deg, #f5b042, #FF0000);\n" +
                        "  -webkit-background-clip: text;\n" +
                        "  -webkit-text-fill-color: transparent;'>")
                .append("InstantGrade")
                .append("</h1><br>")
                .append("<h3>Activate your Account now!</h3>")
                .append("<a href='http://localhost:4200/verify?id=" + verifyCode + "'>Activate now</a>")
                .append("<div style = 'position: absolute; bottom: 0; width: 100%; height: 50px;'>&copy; by Sebastian Schiefermayr</div>")
                .append("</div>");

        return sb.toString();
    }

    /**
     * @author Sebastian Schiefermayr
     * This Method builds the JSON Object which will be sent to our frontend
     */
    private JSONObject buildUserJSON(User user) {
        JSONObject jso = new JSONObject();

        jso
                .put("username", user.getUsername())
                .put("firstname", user.getFirstname())
                .put("lastname", user.getLastname())
                .put("email", user.getEmail())
                .put("settings", new JSONObject(user.getSettings()).toString())
                .put("credits", user.getCredits())
                .put("accountType", user.getAccountType().toString())
                .put("subscriptionStatus", user.getSubscriptionStatus().toString());
        return jso;
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
        User temp = userCollection.find(new Document().append("username", username)).first();
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

        return ImageUtil.parseImageList(imageCollection.find(doc).into(new ArrayList<>()));
    }

    // creates Filepath for Imageupload
    private String createFilepath(FormDataContentDisposition fileMetaData, String owner) {
        File upload_dir = new File("uploads/" + owner);
        if (!upload_dir.exists()) {
            if (upload_dir.mkdirs()) {
                System.out.println("Directory " + upload_dir.getPath() + " successfully created!");
            } else {
                System.out.println("Directory could not been created.");
            }
        }
        return upload_dir.getPath() + "/" + fileMetaData.getFileName();

    }

    //deletes Image from Database and Server
    public String delete(String name, String owner) {

        JSONObject deleted = new JSONObject();

        File file = new File("uploads/" + owner + "/" + name);

        //  file.delete();

        Document doc = new Document("filepath", "uploads/" + owner + "/" + name);
        Image deletetone = imageCollection.findOneAndDelete(doc);
        // TODO change DB - IMAGE Path to trash/{User}/...
        if (deletetone == null && !moveFileToTrash(file, owner)) {

            deleted.put("status", "failed")
                    .put("exception", "Image doesn't exist");

        } else {

            deleted.put("status", "success")
                    .put("fileName", name);

        }

        return deleted.toString();
    }

    private boolean moveFileToTrash(File fileToTrash, String owner) {
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

    /**
     * @param file - the current File
     * @author Sebastian Schiefermayr
     */
    private String getMetadata(File file) {
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


    public String recover(String filename, String owner) {
        File file = new File("trash/" + owner + "/" + filename);
        JSONObject recovered = new JSONObject();
        if (recoverFileFromTrash(file)) {
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
        Image newImage = imageCollection.findOneAndDelete(doc);
        if (newImage == null) {

            renamed.put("status", "failed")
                    .put("exception", "Image doesn't exist");

        } else {

            newImage.setCustomName(newName);
            newImage.setFilepath("uploads/" + owner + "/" + newName);
            this.imageCollection.insertOne(newImage);

            renamed.put("status", "success")
                    .put("fileName", newName);

        }


        return null;
    }

    private boolean recoverFileFromTrash(File fileFromTrash) {
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
