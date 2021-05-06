package Dao;

import Interfaces.MongoInterface;

public interface Dao<T> extends MongoInterface<T> {
    void init();

    long countDocuments(String filterfield, String filter);
}
