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

    @Override
    public boolean deleteByIds(Set<Long> ids) {
        try {
            careServiceRepository.deleteAllByIdInBatch(ids);
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    @Override
    public Optional<CareService> findByUuid(String uuid) {
        // TODO Auto-generated method stub
      return careServiceRepository.findByUuid(uuid);
    }

    @Override
    public Optional<CareService> getByUuidAndGroomer(String uuid, long groomerId) {
      // TODO Auto-generated method stub
      return careServiceRepository.findByUuidAndGroomerId(uuid, groomerId);
    }
}
