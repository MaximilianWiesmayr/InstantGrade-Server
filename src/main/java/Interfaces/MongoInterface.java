package Interfaces;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;

public interface MongoInterface<T> {
    T findOne(Document doc, MongoCollection<T> collection);

    void insertOne(T t, MongoCollection<T> collection);

    FindIterable<T> findAll(Document doc, MongoCollection<T> collection);

    T findOneAndDelete(Document doc, MongoCollection<T> collection);

    void replaceOne(Document doc, T t, MongoCollection<T> collection);
}

