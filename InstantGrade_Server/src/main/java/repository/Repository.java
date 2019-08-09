package repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entity.User;
import enums.AccountStatus;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 *
 * @author H. Lackinger
 */
public final class Repository {

    private List<User> users = new LinkedList<User>();

    private CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders((PojoCodecProvider.builder().automatic(true).build())));

    private MongoClient client = MongoClients.create("mongodb://localhost");
    private MongoDatabase igDB = client.getDatabase("IG").withCodecRegistry(pojoCodecRegistry);
    private MongoCollection<User> igCol = igDB.getCollection("userCollection", User.class);
    
    private static Repository instance = null;
    
    private Repository () {

    }
    
    public static Repository getInstance(){
        if(instance == null){
            instance = new Repository();
        }
        
        return instance;
    }
    

    public String register(User user){

        JSONObject jsonUser = new JSONObject();

        Document doc = new Document("username", user.getUsername());
        doc.put("email", user.getEmail());
        if(igCol.find(doc).first() == null){

            try {

                emailauth(user);

            } catch (Exception e){

                e.printStackTrace();

            }

            this.igCol.insertOne(user);
            jsonUser.put("username", user.getUsername());

        } else {

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

        MimeMessage message = new MimeMessage(mailSession);
        message.setContent("Click on this link, to verificate your email: https://ie.schorn.io/ " + verificationCode, "text/plain");
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
        if(igCol.find(doc).first() == null){

            jsonUser.put("exception", "User doesn't exist");

        } else {
            if(igCol.find(doc).first().getAccountStatus() == AccountStatus.VERIFICATED) {
                jsonUser.put("username", user.getUsername());
            } else {
                jsonUser.put("exception", "email not verificated");
            }

        }

        return jsonUser.toString();
    }

    public String verificate(String verifivationCode) {

        JSONObject jsonstatus = new JSONObject();

        String username = decrypt(verifivationCode);

        Document doc = new Document("username", username);
        User user = igCol.find(doc).first();
        if(igCol.find(doc).first() == null){
            jsonstatus.put("status", "fail");
        } else {
            user.setAccountStatus(AccountStatus.VERIFICATED);
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

















   /*public List<Person> persons = new LinkedList<>();
    List<String> personstring = new LinkedList<>();
    
    public void loadFromFile() throws IOException{
        String [] tempperson;
        personstring = Files.readAllLines(new File("rest.csv").toPath()        );
        for(String s : personstring){
            Person p = new Person();
            tempperson = s.split(";");
            p.setId(  Integer.parseInt(tempperson[0]));
            p.setFirstname(tempperson[1]);
            p.setLastname(tempperson[2]);
            p.setGender(tempperson[3]);
            p.setEmail(tempperson[4]);
            p.setCountry(tempperson[5]);
            p.setAge(Integer.parseInt(tempperson[6]));
            p.setRegistered(Boolean.getBoolean(tempperson[7]));
            this.persons.add(p);
        }        
        
    }

    public List<Person> findAll() {
        return this.persons;
    }

    public void insert(Person person) {
        persons.add(person);
    }
    
    public void delete(int personId){
        this.persons.removeIf((person) -> person.getId() == personId);
    }
    
    public Person update(int personId, Person person) {
            this.delete(personId);
            this.persons.add(person);
        return person;
    }
    
    
    
    
    
    
    
    
    *//*@Path("{id}")
      @GET
      public String getPathParam(@PathParam("id") long id){
        return "get rereceived with PathParam: " + id;
      }
    
    
    
    
    Repository.get().delete(id);*//*
*/
    
    
}
