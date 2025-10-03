package org.ashwin.service.interfaces;

public interface BaseRepository <T>{

    boolean createTableIfNotExists();

    Object create(T entity);

    boolean update(T entity);

    boolean deleteById(Object id);

    T findById(Object id);


}
