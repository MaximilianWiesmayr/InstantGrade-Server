import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import repository.Repository;
import util.UserUtil;

import java.io.IOException;
import java.net.URI;


public class REST_JavaSE {
  
    // Basis URI 
    public static final String BASE_URI = "http://0.0.0.0:8080/api";

    public static org.glassfish.grizzly.http.server.HttpServer startServer() {
        // Im Package "endpoint" alle Klassen durchsuchen, um REST Services zu finden

        final ResourceConfig rc = new ResourceConfig().packages("endpoint", "filter").register(MultiPartFeature.class);
        // Server starten
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Server starten
        final org.glassfish.grizzly.http.server.HttpServer server = startServer();
        // Static Content - Im Projekt-Verzeichnis "public" liegen die html-Files : localhost:8080/index.html
        StaticHttpHandler staticHttpHandler = new StaticHttpHandler("uploads");
        staticHttpHandler.setFileCacheEnabled(false);
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/uploads/");
        
        System.out.println(String.format("Server startet at %s\nHit enter to stop ...", BASE_URI));
        UserUtil.initProperties();
        Thread.currentThread().join();
        server.shutdownNow();
    }
}
