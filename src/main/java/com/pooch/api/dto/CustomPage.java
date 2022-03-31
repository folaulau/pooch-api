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

	private int page;
	private int totalPages;
	private long totalElements;
	private int size;
	private boolean last;
	private boolean first;
	private List<T> content;

	@JsonIgnore
	private Page<T> actualPage;

	public CustomPage() {
	}

	public CustomPage(Page<T> jpaPage) {
		this(jpaPage.getNumber(), jpaPage.getTotalPages(), jpaPage.getTotalElements(), jpaPage.getSize(),
				jpaPage.isLast(), jpaPage.isFirst());
		this.setContent(jpaPage.getContent());
		this.actualPage = jpaPage;
	}

	public CustomPage(int page, int totalPages, long totalElements, int size, boolean last, boolean first) {
		super();
		this.page = page;
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.size = size;
		this.last = last;
		this.first = first;
	}

	public void printActualPage() {
		System.out.println(actualPage.toString());
	}

	
}
