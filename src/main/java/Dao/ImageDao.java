package Dao;

import Interfaces.MongoInterface;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import entity.Image;

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
    public Image findOne(Document doc, MongoCollection<Image> collection) {
        return collection.find(doc).first();
    }

    @Override
    public void insertOne(Image image, MongoCollection<Image> collection) {
        collection.insertOne(image);
    }

    @Override
    public FindIterable<Image> findAll(Document doc, MongoCollection<Image> collection) {
        return collection.find(doc);
    }

    @Override
    public Image findOneAndDelete(Document doc, MongoCollection<Image> collection) {
        return collection.findOneAndDelete(doc);
    }

    @Override
    public void replaceOne(Document doc, Image image, MongoCollection<Image> collection) {
        collection.replaceOne(doc, image);
    }

    public long countDocuments(String filterfield, String filter, MongoCollection<Image> collection){
        long count = collection.countDocuments(new Document(filterfield, filter));
        return count;
    }
}
