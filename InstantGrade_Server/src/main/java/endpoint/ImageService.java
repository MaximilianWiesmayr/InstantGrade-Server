package endpoint;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * @author Sebastian Schiefermayr
 */

@Path("image")
public class ImageService {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("upload")
    public String uploadImage(@FormDataParam("file") InputStream fileInputStream,
                              @FormDataParam("file") FormDataContentDisposition fileMetaData,
                              @FormDataParam("owner") String owner) {
        System.out.println(owner + " " + fileMetaData.getFileName());
        return "";
    }
}
