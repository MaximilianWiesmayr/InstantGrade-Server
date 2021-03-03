package endpoint;

import repository.Repository;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Sebastian Schiefermayr
 * Requests according to the Client-Area are handled here
 */
@Path("clientarea")
public class ClientAreaService {

    /** @return Returns information for the dashboard so it can be displayed */
    // Load Information to Display it on our Dashboard
    @GET
    @Produces(MediaType.APPLICATION_JSON) // Temporally
    @Path("overview/{username}")
    public String getOverview(@NotNull @PathParam("username") String username) {
        return Repository.getInstance().getOverview(username);
    }

    /** @return Returns the information of all the images from a user */
    // Loads the photos
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("photos/{username}")
    public String getPhotos(@NotNull @PathParam("username") String username) {
        return Repository.getInstance().getPhotos(username);
    }

    /*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("photos/{username}/{imagename}")
    public String downloadImage(@NotNull @PathParam("imagename") String imagename, @NotNull @PathParam("username") String username) {
        return Repository.getInstance().downloadImage(imagename, username);
    }
     */
}
