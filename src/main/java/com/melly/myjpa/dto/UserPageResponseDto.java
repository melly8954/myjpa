package com.melly.myjpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public class UserPageResponseDto<T> {
    private List<T> users;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

}