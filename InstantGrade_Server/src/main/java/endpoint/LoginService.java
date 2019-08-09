package endpoint;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import entity.User;
import repository.Repository;

@Path("login")
public class LoginService {


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("register")
    public String register(User user){

        return Repository.getInstance().register(user);

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("login")
    public String login(User user){

        return Repository.getInstance().login(user);

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("verificate")
    public String verificate(User user){

        return Repository.getInstance().verificate(user.getUsername());

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
