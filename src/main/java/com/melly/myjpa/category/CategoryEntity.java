package com.melly.myjpa.category;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="category_tbl")
public class CategoryEntity implements ICategory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Column(length = 20, unique = true)
    private String name;


    @Override
    public String toString(){
        return String.format("ID:%6d,분류:%s"
                ,this.id,this.name);
    }
}
