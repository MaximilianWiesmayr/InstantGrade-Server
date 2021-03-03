package util;

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
}
