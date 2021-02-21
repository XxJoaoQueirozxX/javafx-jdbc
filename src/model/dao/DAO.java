package model.dao;

import java.util.List;


public interface DAO<T> {
    void insert(T o);
    void update(T o);
    void deleteById(Integer id);
    T findById(Integer id);
    List<T> findAll();
}
