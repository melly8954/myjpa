package com.melly.myjpa.category;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto implements ICategory {
    private Long id;
    private String name;

    @Override
    public String toString(){
        return String.format("ID:%6d, 이름:%s "
                ,this.id,this.name);
    }

}
