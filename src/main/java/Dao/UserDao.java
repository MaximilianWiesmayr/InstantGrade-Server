package Dao;

import Interfaces.MongoInterface;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import entity.Image;
import entity.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UserDao implements MongoInterface<User> {

    private MongoCollection<User> userCollection;
    private List<User> users = new ArrayList<>();

    public UserDao(){

    }

    @Override
    public User findOne(Document doc) {
        return userCollection.find(doc).first();
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
