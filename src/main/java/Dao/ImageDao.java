package Dao;

import Interfaces.MongoInterface;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import entity.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class ImageDao implements Dao<Image> {

    private CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders((PojoCodecProvider.builder().automatic(true).build())));
    private MongoCollection<Image> collection;

    public ImageDao(){
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
        collection = igDB.getCollection("imageCollection", Image.class);
    }

    @Override
    public Image findOne(Document doc) {
        return collection.find(doc).first();
    }

    @Override
    public void insertOne(Image image) {
        collection.insertOne(image);
    }

    @Override
    public ArrayList<Image> findAll(Document doc) {
        return collection.find(doc).into(new ArrayList<>());
    }

    @Override
    public Image findOneAndDelete(Document doc) {
        return collection.findOneAndDelete(doc);
    }

    @Override
    public void replaceOne(Document doc, Image image) {
        collection.replaceOne(doc, image);
    }

    public long countDocuments(String filterfield, String filter){
        long count = collection.countDocuments(new Document(filterfield, filter));
        return count;
    }
}
