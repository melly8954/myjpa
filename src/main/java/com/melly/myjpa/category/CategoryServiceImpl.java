package com.melly.myjpa.category;

import com.melly.myjpa.*;
import com.melly.myjpa.phonebook.IPhoneBook;
import com.melly.myjpa.phonebook.PhoneBookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements ICategoryService<ICategory>{
    @Autowired
    private CategoryJpaRepository categoryJpaRepository;  // 선언 및 스프링부트 service , autowired 사용

    private boolean isValidInsert(ICategory dto){
        if(dto == null){
            return false;
        }
        else return dto.getName() != null && !dto.getName().isEmpty();
    }

    @Override
    public ICategory findById(Long id) {
        Optional<CategoryEntity> find = this.categoryJpaRepository.findById(id);
        return find.orElse(null);
    }

    @Override
    public ICategory findByName(String name) {
        Optional<CategoryEntity> entity = this.categoryJpaRepository.findByName(name);
        return entity.orElse(null);
    }


    @Override
    public List<ICategory> getAllList() {
        List<ICategory> list = new ArrayList<>();
        for ( CategoryEntity entity : this.categoryJpaRepository.findAll() ){
            list.add( (ICategory)entity );
        }

        return list;
    }

    @Override
    public ICategory insert(ICategory category) throws Exception {
        if ( !this.isValidInsert(category)){
            return null;
        }
        CategoryEntity entity = new CategoryEntity();
        entity.copyFields(category);
        ICategory result = this.categoryJpaRepository.saveAndFlush(entity);
        return result;
    }

    @Override
    public boolean remove(Long id) {
        ICategory find = this.findById(id);
        if ( find != null ) {
            this.categoryJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private boolean setICategoryIsNotNull(ICategory to, ICategory from) {
        if ( to == null || from == null ) {
            return false;
        }
        if ( from.getName() != null && !from.getName().isEmpty() ) {
            to.setName(from.getName());
        }
        return true;
    }

    @Override
    public ICategory update(Long id, ICategory category) {
        ICategory find = this.findById(id);
        if ( find == null ) {
            return null;
        }
        CategoryEntity entity = CategoryEntity.builder()
                .id(id).name(find.getName())
                .build();
        entity.copyFields(category);
        CategoryEntity result = this.categoryJpaRepository.saveAndFlush(entity);
        return result;
    }

    @Override
    public List<ICategory> findAllByNameContains(String name) {
        if (name == null || name.isEmpty()) {
            return new ArrayList<>();       // 빈 객체
        }
        List<CategoryEntity> list = this.categoryJpaRepository.findAllByNameContains(name);
        List<ICategory> result = new ArrayList<>();
        for( CategoryEntity item : list ){
            result.add((ICategory)item);
        }
        return result;
    }


}
