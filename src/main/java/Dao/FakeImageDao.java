package Dao;

import com.mongodb.client.FindIterable;
import entity.Image;
import org.bson.Document;

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
    public FindIterable<Image> findAll(Document doc) {
        return null;
    }

    @Override
    public Image findOneAndDelete(Document doc) {
        return null;
    }

    @Override
    public void replaceOne(Document doc, Image image) {

    }

    @Override
    public long countDocuments(String filterfield, String filter) {
        return 0;
    }
}
