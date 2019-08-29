package endpoint;

import entity.Image;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import repository.Repository;

import javax.ws.rs.*;
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
        return Repository.getInstance().upload(fileInputStream, fileMetaData, owner);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("edit")
    public String edit(String jsonEditString){
        JSONObject jsonEdit = new JSONObject(jsonEditString);
        String oldName = jsonEdit.getString("oldName");
        String newName = jsonEdit.getString("newName");
        String owner = jsonEdit.getString("owner");

        return Repository.getInstance().edit(oldName, newName, owner);
    }
}
