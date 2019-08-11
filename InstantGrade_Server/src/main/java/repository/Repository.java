package repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entity.User;
import enums.AccountType;
import org.apache.commons.codec.binary.Base64;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

    private MongoClient client = MongoClients.create("mongodb://localhost");
    private MongoDatabase igDB = client.getDatabase("IG").withCodecRegistry(pojoCodecRegistry);
    private MongoCollection<User> igCol = igDB.getCollection("userCollection", User.class);

    private static Repository instance = null;

    private Repository() {

    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }


    public String register(User user) {
        JSONObject jsonUser = new JSONObject();

        Document doc = new Document("username", user.getUsername());
        doc.put("email", user.getEmail());
        if (igCol.find(doc).first() == null) {

            try {

                emailauth(user);

            } catch (Exception e) {

                e.printStackTrace();

            }
            this.igCol.insertOne(user);
            jsonUser.put("status", "success");
            jsonUser.put("username", user.getUsername());

        } else {

            jsonUser.put("status", "failed");
            jsonUser.put("exception", "User exists already");

        }

        return jsonUser.toString();
    }

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

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = "instantgrade@bastiarts.com";
            String password = "6Tmv0?h4";
            return new PasswordAuthentication(username, password);
        }
    }

    public String login(User user) {


        JSONObject jsonUser = new JSONObject();

        Document doc = new Document("username", user.getUsername());
        doc.put("password", user.getPassword());
        if (igCol.find(doc).first() == null) {

            jsonUser.put("status", "failed");
            jsonUser.put("exception", "User doesn't exist");

        } else {
            if (igCol.find(doc).first().getAccountType() == AccountType.VERIFIED) {
                jsonUser.put("status", "success");
                jsonUser.put("username", user.getUsername());
            } else {
                jsonUser.put("status", "failed");
                jsonUser.put("exception", "email not verified");
            }

        }

        return jsonUser.toString();
    }

    public String verify(String verifivationCode) {

        JSONObject jsonstatus = new JSONObject();

        String username = decrypt(verifivationCode);

        Document doc = new Document("username", username);
        User user = igCol.find(doc).first();
        if (igCol.find(doc).first() == null) {
            jsonstatus.put("status", "fail");
        } else {
            user.setAccountType(AccountType.VERIFIED);
            this.igCol.replaceOne(doc, user);
            jsonstatus.put("status", "success");
        }

        return jsonstatus.toString();
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

    private static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * @author Sebastian Schiefermayr
     * This Method builds the HTML format for the Mail
     */
    private String buildHTML(String verifyCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style = 'text-align: center;'>");
        sb.append("<h1 style = 'background-image: linear-gradient(90deg, #f5b042, #FF0000);\n" +
                "  -webkit-background-clip: text;\n" +
                "  -webkit-text-fill-color: transparent;'>");
        sb.append("Activate your Account");
        sb.append("</h1><br>");
        sb.append("<a href='http://localhost:4200/verify?id=" + verifyCode + "'>Activate now</a>");
        sb.append("<div style = 'position: absolute; bottom: 0; width: 100%; height: 50px;'>&copy; by Sebastian Schiefermayr</div>");
        sb.append("</div>");

        return sb.toString();
    }
}
