package com.pooch.api.elastic.groomer;

import com.pooch.api.dto.CustomPage;
import org.springframework.scheduling.annotation.Async;

import com.pooch.api.elastic.repo.GroomerES;

public interface GroomerESDAO {

	@Async
	void save(GroomerES groomerES);

    CustomPage<GroomerES> search(Long pageNumber, Long pageSize, Long lat, Long lon, String searchPhrase);
}
