package com.melly.myjpa.category;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity,Long> {
    Optional<CategoryEntity> findByName(String name);
    List<CategoryEntity> findAllByNameContains(String name);

}
