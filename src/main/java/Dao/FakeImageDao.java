package Dao;

import com.mongodb.Block;
import com.mongodb.CursorType;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import entity.Image;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class FakeImageDao implements Dao<Image> {

    public Image fakeImage = null;

    @Override
    public void init() {

    }

    @Override
    public Image findOne(Document doc) {
        return fakeImage;
    }

    @Override
    public void insertOne(Image image) {
        fakeImage = image;
    }

    @Override
    public ArrayList<Image> findAll(Document doc) {
        ArrayList<Image> images = new ArrayList<>();
        if(fakeImage != null) {
            images.add(fakeImage);
        }
        return images;
    }

    @Override
    public Image findOneAndDelete(Document doc) {
        return fakeImage;
    }

    @Override
    public void replaceOne(Document doc, Image image) {

    }

    @Override
    public long countDocuments(String filterfield, String filter) {
        if(fakeImage == null){
            return 0;
        }
        return 1;
    }
}
