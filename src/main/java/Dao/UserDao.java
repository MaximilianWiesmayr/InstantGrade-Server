package Dao;

import Interfaces.MongoInterface;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import entity.Image;
import entity.User;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class UserDao implements Dao<User> {

    private CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders((PojoCodecProvider.builder().automatic(true).build())));
    private MongoCollection<User> collection;

    public UserDao(){

    }

    public void init() {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MongoClient client = MongoClients.create("mongodb://" + properties.getProperty("mongo.username") + ":"
                + properties.getProperty("mongo.password") + "@instantgrade.bastiarts.com:27017/?authSource=IG");

        MongoDatabase igDB = client.getDatabase("IG").withCodecRegistry(pojoCodecRegistry);
        collection = igDB.getCollection("userCollection", User.class);
    }

    @Override
    public long countDocuments(String filterfield, String filter) {
        return 0;
    }

    @Override
    public User findOne(Document doc) {
        return collection.find(doc).first();
    }

    @Override
    public void insertOne(User user) {
        collection.insertOne(user);
    }

    @Override
    public FindIterable<User> findAll(Document doc) {
        return collection.find(doc);
    }

    @Override
    public User findOneAndDelete(Document doc) {
        return collection.findOneAndDelete(doc);
    }

    @Override
    public void replaceOne(Document doc, User user) {
        collection.replaceOne(doc, user);
    }
}
