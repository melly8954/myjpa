package com.melly.myjpa.category;

import java.io.Serializable;

public interface ICategory extends Serializable {
    Long getId();
    void setId(Long id);

    String getName();
    void setName(String name);

    default void copyFields(ICategory from) {
        if ( from == null ) {
            return;
        }
        if ( from.getId() != null ) {
            this.setId(from.getId());
        }
        if ( from.getName() != null ) {
            this.setName(from.getName());
        }

    }
}
