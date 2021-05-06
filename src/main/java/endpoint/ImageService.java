package endpoint;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import repository.Repository;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * @author Sebastian Schiefermayr
 */

@Path("image")
public class ImageService {

    /** Uploads an image to the users images and creates a thumbnail to display the image on the dashboard
     *
     * @return Returns important information of the image so it can be displayed */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("upload")
    public String uploadImage(@FormDataParam("file") InputStream fileInputStream,
                              @FormDataParam("file") FormDataContentDisposition fileMetaData,
                              @FormDataParam("owner") String owner) {
        return Repository.getInstance().upload(fileInputStream, fileMetaData, owner);
    }

    /*@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("edit")
    public String edit(String jsonEditString) {
        JSONObject jsonEdit = new JSONObject(jsonEditString);
        String oldName = jsonEdit.getString("oldName");
        String newName = jsonEdit.getString("newName");
        String owner = jsonEdit.getString("owner");

        return Repository.getInstance().edit(oldName, newName, owner);
    }*/

    /** Deletes an image from the users uploaded images
     *
     * @return Returns a success message and the filename of the deleted image*/
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("delete")
    public String delete(String jsonEditString){
        JSONObject jsonEdit = new JSONObject(jsonEditString);
        String name = jsonEdit.getString("imageName");
        String owner = jsonEdit.getString("owner");

        return Repository.getInstance().delete(name, owner);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("preparedownloadImage")
    public String preparedownloadImage(String jsonEditString){
        JSONObject jsonEdit = new JSONObject(jsonEditString);
        String filepath = jsonEdit.getString("filepath");
        String type = jsonEdit.getString("type");

        return Repository.getInstance().prepareDownload(filepath, type);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("reset")
    public String reset(String jsonEditString){
        JSONObject jsonEdit = new JSONObject(jsonEditString);
        String name = jsonEdit.getString("imageName");
        String owner = jsonEdit.getString("owner");

        return Repository.getInstance().reset(name, owner);
    }

    /** Recovers the last image that got deleted
     *
     * @return Returns a success message and the filename of the image*/
    /*@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("recover")
    public String recover(String jsonEditString) {
        JSONObject jsonEdit = new JSONObject(jsonEditString);
        String name = jsonEdit.getString("imageName");
        String owner = jsonEdit.getString("owner");

        return Repository.getInstance().recover(name, owner);
    }*/
}
