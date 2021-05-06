package Dao;

import com.mongodb.client.FindIterable;
import entity.User;
import org.bson.Document;

public class FakeUserDao implements Dao<User>{

    public User fakeUser = null;

    @Override
    public void init() {

    }

    @Override
    public User findOne(Document doc) {
        return fakeUser;
    }

    @Override
    public void insertOne(User user) {
        fakeUser = user;
    }

    @Override
    public FindIterable<User> findAll(Document doc) {
        return null;
    }

    @Override
    public User findOneAndDelete(Document doc) {
        return null;
    }

    @Override
    public void replaceOne(Document doc, User user) {

    }

    @Override
    public long countDocuments(String filterfield, String filter) {
        return 0;
    }
}
