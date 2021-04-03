package Dao;

import Interfaces.MongoInterface;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entity.Image;
import entity.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ImageDao implements MongoInterface<Image> {

    private MongoCollection<Image> imageCollection;
    private List<Image> images = new ArrayList<>();

    public ImageDao(){
    }

    public void init(MongoDatabase igDB) {
        imageCollection = igDB.getCollection("imageCollection", Image.class);
    }

    @Override
    public Image findOne(Document doc) {
        return imageCollection.find(doc).first();
    }

    @Override
    public void insertOne(Image image) {
        imageCollection.insertOne(image);
    }

    @Override
    public FindIterable<Image> findAll(Document doc) {
        return imageCollection.find(doc);
    }

    @Override
    public Image findOneAndDelete(Document doc) {
        return imageCollection.findOneAndDelete(doc);
    }

    @Override
    public void replaceOne(Document doc, Image image) {
        imageCollection.replaceOne(doc, image);
    }
}
