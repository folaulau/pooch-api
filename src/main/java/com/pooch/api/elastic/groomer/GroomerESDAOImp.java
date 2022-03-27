package com.pooch.api.elastic.groomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GroomerESDAOImp implements GroomerESDAO {

	@Autowired
	private GroomerESRepository groomerESRepository;

	@Override
	public GroomerES save(GroomerES groomerES) {
		return groomerESRepository.save(groomerES);
	}

}
