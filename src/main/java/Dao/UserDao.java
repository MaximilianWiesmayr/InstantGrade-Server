package Dao;

import Interfaces.MongoInterface;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entity.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UserDao implements MongoInterface<User> {

    private MongoCollection<User> userCollection;
    private List<User> users = new ArrayList<>();

    public UserDao(){

    }

    public void init(MongoDatabase igDB) {
        userCollection = igDB.getCollection("userCollection", User.class);
    }

    @Override
    public User findOne(Document doc) {
        System.out.println("hellow");
        User testuser = userCollection.find(doc).first();
        System.out.println(testuser);
        return testuser;
    }

    @Override
    public void insertOne(User user) {
        userCollection.insertOne(user);
    }

    @Override
    public FindIterable<User> findAll(Document doc) {
        return userCollection.find(doc);
    }

    @Override
    public User findOneAndDelete(Document doc) {
        return userCollection.findOneAndDelete(doc);
    }

    @Override
    public void replaceOne(Document doc, User user) {
        userCollection.replaceOne(doc, user);
    }
}
