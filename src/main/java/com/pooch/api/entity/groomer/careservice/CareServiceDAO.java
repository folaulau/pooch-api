package com.pooch.api.entity.groomer.careservice;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CareServiceDAO {

    CareService save(CareService petCareService);

    List<CareService> saveAll(Set<CareService> careServices);

    boolean existByUuidAndGroomer(String uuid, long groomerId);

    Optional<CareService> getByUuid(String uuid);

    Optional<Set<CareService>> findByGroomerId(Long groomerId);
    
    boolean deleteByIds(Set<Long> ids);
}
