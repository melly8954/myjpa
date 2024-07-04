package com.melly.myjpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="phonebook_tbl")
public class PhoneBookEntity implements IPhoneBook{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50,unique = true)
    private String name;

    @NotNull
    @Column(length = 10)
    private String category;

    @NotNull
    @Column(length = 20)
    private String phoneNumber;

    @NotNull
    @Column(length = 200)
    private String email;

    @Override
    public String toString(){
        return String.format("ID:%6d, 이름:%s, 분류:%s, 번호:%s, 이메일:%s"
                ,this.id,this.name,this.category,this.phoneNumber,this.email);
    }
}
