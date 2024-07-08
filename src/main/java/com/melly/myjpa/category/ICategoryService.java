package com.melly.myjpa.category;

import java.util.List;

public interface ICategoryService<T> {
    T findById(Long id);
    T findByName(String name);
    List<T> getAllList();
    T insert(T category) throws Exception;
    boolean remove(Long id) throws Exception;
    T update(Long id, T category) throws Exception;
    List<T> findAllByNameContains(String name);
}
