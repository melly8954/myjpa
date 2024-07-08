package com.melly.myjpa.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity,Long> {
    List<CategoryEntity> findAllByName(String name);
    List<CategoryEntity> findAllByNameContains(String name);


}
