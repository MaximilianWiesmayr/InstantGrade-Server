package endpoint;

import entity.User;
import org.json.JSONObject;
import repository.Repository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("auth")
public class LoginService {

    /** @return Returns the username to the Client if the registration was successful */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("register")
    public String register(User user){
        return Repository.getInstance().register(user);

    }

    /** @return Returns a success message and an jwt token for future interactions*/

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("login")
    public String login(String userJSON) {
        JSONObject jso = new JSONObject(userJSON);
        User tmp = new User();
        tmp.setUsername(jso.getString("username"));
        tmp.setPassword(jso.getString("password"));
        return Repository.getInstance().login(tmp);

    }

    /** When the Link of the verification email is clicked a verification Token gets sent
     * @return Returns an success message if the token is the same as the one from the registration*/
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("verify")
    public String verify(String token) {

        return Repository.getInstance().verify(token);

    }

}
