package endpoint;

import entity.Image;
import entity.User;
import repository.Repository;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("image")
public class ImageUploadService {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("upload")
    public String register(Image image){

        return Repository.getInstance().upload(image);

    }

}
