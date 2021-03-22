package util;

import entity.User;
import org.json.JSONObject;

import java.io.*;
import java.nio.channels.Channels;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Sebastian Schiefermayr
 */
public class SystemUtil {
    public static void logToFile(String title, String message) {
        String logDirPath = "log";
        // Necessary Files
        File logDir = new File(logDirPath);
        File logFile = new File(logDirPath + "/" + title.replace(" ", "_") + ".txt");
        // For the Log timestamp
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        // Append it with the message to the final log String
        String logMessage = dtf.format(now) + " > " + message;
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Create a Writer and append the message in the log file
        try (Writer writer = Channels.newWriter(new FileOutputStream(
                logFile.getAbsoluteFile(), true).getChannel(), "UTF-8")) {
            writer.append(logMessage + "\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String buildHTML(String verifyCode) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("<div style = 'text-align: center; margin: 0 auto; width: 50%;'>")
                .append("<h1 style = 'background-image: linear-gradient(90deg, #f5b042, #FF0000);\n" +
                        "  -webkit-background-clip: text;\n" +
                        "  -webkit-text-fill-color: transparent;'>")
                .append("InstantGrade")
                .append("</h1><br>")
                .append("<h3>Activate your Account now!</h3>")
                .append("<a href='http://instantgrade.bastiarts.com:4200/verify?id=" + verifyCode + "'>Activate now</a>")
                .append("<div style = 'position: absolute; bottom: 0; width: 100%; height: 50px;'>&copy; by Sebastian Schiefermayr</div>")
                .append("</div>");

        return sb.toString();
    }

    public static JSONObject buildUserJSON(User user) {
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
}
