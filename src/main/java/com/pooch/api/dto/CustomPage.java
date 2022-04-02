package com.pooch.api.dto;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class CustomPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int               pageNumber;
    private int               pageSize;
    private int               totalPages;
    private long              totalElements;
    private boolean           last;
    private boolean           first;
    private List<T>           content;

    public CustomPage() {
    }

    public CustomPage(Page<T> jpaPage) {
        this(jpaPage.getNumber(), jpaPage.getTotalPages(), jpaPage.getTotalElements(), jpaPage.getSize(), jpaPage.isLast(), jpaPage.isFirst());
        this.setContent(jpaPage.getContent());
    }

    public CustomPage(int pageNumber, int totalPages, long totalElements, int pageSize, boolean last, boolean first) {
        super();
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
        this.last = last;
        this.first = first;
    }

}
