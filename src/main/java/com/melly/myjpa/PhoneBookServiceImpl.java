package com.melly.myjpa;

import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PhoneBookServiceImpl implements IPhoneBookService<IPhoneBook> {
    @Autowired      
    private PhoneBookjpaRepository phoneBookjpaRepository;  // 선언 및 스프링부트 service , autowired 사용

    private boolean isValidInsert(IPhoneBook dto){
        if(dto == null){
            return false;
        }
        else if(dto.getName() == null || dto.getName().isEmpty() ){
            return false;
        }
        else if(dto.getCategory() == null || dto.getCategory().isEmpty() ){
            return false;
        }
        return true;
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
    public IPhoneBook insert(String name, String category, String phoneNumber, String email) throws Exception {
        PhoneBookDto phoneBook = PhoneBookDto.builder()
                .id(0L)
                .name(name).category(category)
                .phoneNumber(phoneNumber).email(email).build();
        return this.insert(phoneBook);
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

    private boolean setIphoneBookIsNotNull(IPhoneBook to, IPhoneBook from) {
        if ( to == null || from == null ) {
            return false;
        }
        if ( from.getName() != null && !from.getName().isEmpty() ) {
            to.setName(from.getName());
        }
        if ( from.getCategory() != null ) {
            to.setCategory(from.getCategory());
        }
        if ( from.getPhoneNumber() != null && !from.getPhoneNumber().isEmpty() ) {
            to.setPhoneNumber(from.getPhoneNumber());
        }
        if ( from.getEmail() != null && !from.getEmail().isEmpty() ) {
            to.setEmail(from.getEmail());
        }
        return true;
    }

    @Override
    public IPhoneBook update(Long id, IPhoneBook phoneBook) {
        IPhoneBook find = this.findById(id);
        return null;
    }

    @Override
    public List<IPhoneBook> getListFromName(String findName) {
        if (findName == null || findName.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    public List<IPhoneBook> getListFromGroup(ECategory category) {
        if (category == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    public List<IPhoneBook> getListFromPhoneNumber(String findPhone) {
        if (findPhone == null || findPhone.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    public List<IPhoneBook> getListFromEmail(String findEmail) {
        if (findEmail == null || findEmail.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }
}
