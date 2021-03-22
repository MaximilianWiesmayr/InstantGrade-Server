package Interfaces;

import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.List;

public interface MongoInterface<T> {
    T findOne(Document doc);

    void insertOne(T t);

    FindIterable<T> findAll(Document doc);

    T findOneAndDelete(Document doc);

    void replaceOne(Document doc, T t);
}

