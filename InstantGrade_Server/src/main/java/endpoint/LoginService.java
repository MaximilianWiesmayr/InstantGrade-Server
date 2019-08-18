package endpoint;

import entity.User;
import org.json.JSONObject;
import repository.Repository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("auth")
public class LoginService {


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("register")
    public String register(User user){
        return Repository.getInstance().register(user);

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON) // MediaType.APPLICATION_JSON
    @Produces(MediaType.APPLICATION_JSON)
    @Path("login")
    // Example Request {"username": "dummy", "password": "dummy"}
    public String login(String userJSON) {
        JSONObject jso = new JSONObject(userJSON);
        User tmp = new User();
        tmp.setUsername(jso.getString("username"));
        tmp.setPassword(jso.getString("password"));
        return Repository.getInstance().login(tmp);

    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("verify")
    public String verify(String token) {

        return Repository.getInstance().verify(token);

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("test")
    public String test(){

        return Repository.getInstance().test();

    }













   /* // Show message
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("message")
    public String message() {
        return " Hello REST Service powered by Java SE ";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("findAll")
    public List<Person> findAll() {
        return Repository.getInstance().findAll();
        //return Arrays.asList(new Person(0, "lol",null,null,null,null,0,false));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("insert")
    public void insert(Person person){
        Repository.getInstance().insert(person);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("delete/{id}")
    public void delete(@PathParam("id") int personID) {
        Repository.getInstance().delete(personID);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("update/{id}")
    public void update(@PathParam("id") int personId, Person person){
        Repository.getInstance().update(personId, person);
    }*/

}
