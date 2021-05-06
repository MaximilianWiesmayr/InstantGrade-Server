package util;

import entity.User;
import org.apache.commons.codec.binary.Base64;
import repository.Repository;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    public static void emailauth(User user) throws Exception {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "webmail.bastiarts.com");
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new EmailUtil.SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(props, auth);
        // uncomment for debugging infos to stdout
        // mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();

        String verificationCode = encrypt(user.getUsername());
        String encodingOptions = "text/html; charset=UTF-8";
        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject("Verify your Account", "UTF-8");
        message.setHeader("Content-Type", encodingOptions);
        message.setContent(SystemUtil.buildHTML(verificationCode), "text/html");
        message.setFrom(new InternetAddress("instantgrade@bastiarts.com"));
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(user.getEmail()));

        transport.connect();
        transport.sendMessage(message,
                message.getRecipients(Message.RecipientType.TO));
        transport.close();

    }

    private static class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = "instantgrade@bastiarts.com";
            String password = "6Tmv0?h4";
            return new PasswordAuthentication(username, password);
        }
    }

    public static String encrypt(String value) {
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

    public static String decrypt(String encrypted) {
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
}
