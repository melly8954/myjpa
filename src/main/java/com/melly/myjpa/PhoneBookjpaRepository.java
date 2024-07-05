package com.melly.myjpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneBookjpaRepository extends JpaRepository<PhoneBookEntity,Long> {
    List<PhoneBookEntity> findAllByNameContains(String name);
    List<PhoneBookEntity> findAllByCategory(ECategory category);
    List<PhoneBookEntity> findAllByPhoneNumberContains(String phoneNumber);
    List<PhoneBookEntity> findAllByEmailContains(String email);

}
