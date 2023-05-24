package com.muhammadusman92.authenticationservice.payloads;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PageResponse<T> {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPage;
    boolean isLast;
    private List<T> content;
}
