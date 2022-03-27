package com.pooch.api.elastic.groomer;

import org.springframework.scheduling.annotation.Async;

import com.pooch.api.elastic.repo.GroomerES;

public interface GroomerESDAO {

	@Async
	void save(GroomerES groomerES);

}
