package com.melly.myjpa.crawling;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BakeryRepository extends JpaRepository<Bakery, Long> {
    // 이름과 주소가 모두 같은 레코드가 존재하는지 확인
    boolean existsByNameAndAddress(String name, String address);
    List<Bakery> findByRegion(String region); // 특정 지역 데이터 조회
}
