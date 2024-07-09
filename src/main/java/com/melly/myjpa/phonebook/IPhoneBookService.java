package com.melly.myjpa.phonebook;

import com.melly.myjpa.category.ICategory;

import java.util.List;

public interface IPhoneBookService<T> {
    T findById(Long id);
    List<T> getAllList();
    T insert(T phoneBook) throws Exception;
    boolean remove(Long id) throws Exception;
    T update(Long id, T phoneBook) throws Exception;
    List<T> getListFromName(String findName);
    List<T> getListFromCategory(ICategory findCategory);
    List<T> getListFromPhoneNumber(String findPhone);
    List<T> getListFromEmail(String findEmail);

}
