package com.melly.myjpa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name="user_tbl")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String password;
    private String name;
    private String nickname;
    private String email;

    @ManyToOne
    @JoinColumn(name="role_id")
    private RoleEntity role;

    // OAuth를 위해 구성한 추가 필드 2개
    private String provider;
    @Column(name="provider_id")
    private String providerId;


    // createDate 필드 추가
    @Column(name="create_date")
    private Date createDate;
    // 엔티티가 영속화되기 전에 현재 시간을 자동으로 설정하는 메서드

    @Column(name = "delete_flag", nullable = false)
    private Boolean deleteFlag;
    
    // deleteFlag 설정
    public void softDelete() {
        this.deleteFlag = true;
        this.deleteDate = LocalDateTime.now();
    }

    // 사용자 계정 삭제 취소
    public void undoDeleteUser(){
        this.deleteFlag = false;
    }

    @Column(name="delete_date")
    private LocalDateTime deleteDate;

    @PrePersist
    public void prePersist() {
        if (this.createDate == null) {
            this.createDate = new Date();  // 현재 시간을 설정
        }
    }
}
