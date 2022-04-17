package com.pooch.api.entity.groomer.careservice;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CareServiceDAOImp implements CareServiceDAO {

    @Autowired
    private CareServiceRepository careServiceRepository;

    @Override
    public CareService save(CareService careService) {
        return careServiceRepository.saveAndFlush(careService);
    }

    @Override
    public boolean existByUuidAndGroomer(String uuid, long groomerId) {
        Optional<CareService> optCareService = careServiceRepository.findByUuid(uuid);
        if (!optCareService.isPresent()) {
            return false;
        }
        log.info("cs={}", ObjectUtils.toJson(optCareService.get()));
        return optCareService.get().getGroomer().getId().equals(groomerId);
    }

    @Override
    public Optional<CareService> getByUuid(String uuid) {
        return careServiceRepository.findByUuid(uuid);
    }

    @Override
    public Optional<Set<CareService>> findByGroomerId(Long groomerId) {
        return careServiceRepository.findByGroomerId(groomerId);
    }

    @Override
    public List<CareService> saveAll(Set<CareService> careServices) {
        return careServiceRepository.saveAll(careServices);
    }
}
