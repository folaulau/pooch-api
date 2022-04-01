package com.pooch.api.entity.groomer;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.elastic.groomer.GroomerESDAO;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class GroomerAspect {

	@Autowired
	private GroomerESDAO groomerESDAO;

	@Autowired
	private EntityDTOMapper entityDTOMapper;

	/**
	 * https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop-advice-after-returning
	 */
	@AfterReturning(pointcut = "execution(* com.pooch.api.entity.groomer.GroomerDAOImp.save(..))", returning = "groomer")
	public void pushGroomerToElasticsearch(Groomer groomer) {
		log.info("pushGroomerToElasticsearch={}", ObjectUtils.toJson(groomer));

		GroomerES groomerES = entityDTOMapper.mapGroomerEntityToGroomerES(groomer);

		log.info("groomerES={}", ObjectUtils.toJson(groomerES));

		groomerESDAO.save(groomerES);

	}
}
