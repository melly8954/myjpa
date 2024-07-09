package com.melly.myjpa.phonebook;

import com.melly.myjpa.category.CategoryEntity;
import com.melly.myjpa.category.ICategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhoneBookServiceImpl implements IPhoneBookService<IPhoneBook> {
    @Autowired      
    private PhoneBookjpaRepository phoneBookjpaRepository;  // 선언 및 스프링부트 service , autowired 사용

    private boolean isValidInsert(IPhoneBook dto){
        if(dto == null){
            return false;
        }
        else return dto.getName() != null && !dto.getName().isEmpty();
    }

    @Override
    public IPhoneBook findById(Long id) {
        Optional<PhoneBookEntity> find = this.phoneBookjpaRepository.findById(id);
        return find.orElse(null);
    }



    @Override
    public List<IPhoneBook> getAllList() {
        List<IPhoneBook> list = new ArrayList<>();
        for ( PhoneBookEntity entity : this.phoneBookjpaRepository.findAll() ){
            list.add( (IPhoneBook)entity );
        }

        return list;
    }

    @Override
    public IPhoneBook insert(IPhoneBook phoneBook) throws Exception {
        if ( !this.isValidInsert(phoneBook)){
            return null;
        }
        PhoneBookEntity entity = new PhoneBookEntity();
        entity.copyFields(phoneBook);
        IPhoneBook result = this.phoneBookjpaRepository.saveAndFlush(entity);
        return result;
    }

    @Override
    public boolean remove(Long id) {
        IPhoneBook find = this.findById(id);
        if ( find != null ) {
            this.phoneBookjpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    
    @Override
    public IPhoneBook update(Long id, IPhoneBook phoneBook) {
        IPhoneBook find = this.findById(id);
        if ( find == null ) {
            return null;
        }
        PhoneBookEntity entity = PhoneBookEntity.builder()
                .id(id).name(find.getName()).category((CategoryEntity) find.getCategory()).phoneNumber(find.getPhoneNumber())
                .email(find.getEmail())
                .build();
        entity.copyFields(phoneBook);
        PhoneBookEntity result = this.phoneBookjpaRepository.saveAndFlush(entity);
        return result;
    }

    @Override
    public List<IPhoneBook> getListFromName(String findName) {
        if (findName == null || findName.isEmpty()) {
            return new ArrayList<>();
        }
        List<PhoneBookEntity> list = this.phoneBookjpaRepository.findAllByNameContains(findName);
        List<IPhoneBook> result = new ArrayList<>();
        for( PhoneBookEntity item : list ){
            result.add((IPhoneBook)item);
        }
        return result;
    }

    @Override
    public List<IPhoneBook> getListFromCategory(ICategory category) {
        if (category == null) {
            return new ArrayList<>();
        }
        List<PhoneBookEntity> list = this.phoneBookjpaRepository.findAllByCategory( (CategoryEntity)category );
        List<IPhoneBook> result = list.stream()
                .map(item -> (IPhoneBook)item)
                .toList();
        return result;
    }

    @Override
    public List<IPhoneBook> getListFromPhoneNumber(String findPhone) {
        if (findPhone == null || findPhone.isEmpty()) {
            return new ArrayList<>();
        }
        List<PhoneBookEntity> list = this.phoneBookjpaRepository.findAllByPhoneNumberContains(findPhone);
        List<IPhoneBook> result = list.stream()
                .map(item -> (IPhoneBook)item)
                .toList();
        return result;
    }

    @Override
    public List<IPhoneBook> getListFromEmail(String findEmail) {
        if (findEmail == null || findEmail.isEmpty()) {
            return new ArrayList<>();
        }
        List<PhoneBookEntity> list = this.phoneBookjpaRepository.findAllByEmailContains(findEmail);
        List<IPhoneBook> result = list.stream()
                .map(node -> (IPhoneBook)node)
                .collect(Collectors.toUnmodifiableList());
        return result;
    }
}
