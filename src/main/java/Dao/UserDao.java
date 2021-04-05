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
    public User findOne(Document doc, MongoCollection<User> collection) {
        return collection.find(doc).first();
    }

    @Override
    public void insertOne(User user, MongoCollection<User> collection) {
        collection.insertOne(user);
    }

    @Override
    public FindIterable<User> findAll(Document doc, MongoCollection<User> collection) {
        return collection.find(doc);
    }

    @Override
    public User findOneAndDelete(Document doc, MongoCollection<User> collection) {
        return collection.findOneAndDelete(doc);
    }

    @Override
    public void replaceOne(Document doc, User user, MongoCollection<User> collection) {
        collection.replaceOne(doc, user);
    }
}
