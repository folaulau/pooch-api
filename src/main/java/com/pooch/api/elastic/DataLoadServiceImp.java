package com.pooch.api.elastic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.ApiDefaultResponseDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerRepository;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataLoadServiceImp implements DataLoadService {

    @Autowired
    private GroomerESRepository   groomerESRepository;

    @Autowired
    private GroomerRepository     groomerRepository;

    @Autowired
    private CareServiceRepository careServiceRepository;

    @Autowired
    private EntityDTOMapper       entityDTOMapper;

    @Override
    public ApiDefaultResponseDTO loadGroomers() {

        try {
            groomerESRepository.deleteAll();
        } catch (Exception e) {
        }

        int pageNumber = 0;
        int pageSize = 50;
        Pageable page = PageRequest.of(pageNumber, pageSize);

        Page<Groomer> result = groomerRepository.findAll(page);

        while (result.hasContent()) {
            List<Groomer> groomers = result.getContent();

            List<GroomerES> esGroomers = new ArrayList<>();

            for (Groomer groomer : groomers) {
                GroomerES groomerES = entityDTOMapper.mapGroomerEntityToGroomerES(groomer);
                groomerES.populateGeoPoints();
                try {
                    Set<CareService> careServices = careServiceRepository.findByGroomerId(groomerES.getId());
                    groomerES.setCareServices(entityDTOMapper.mapCareServicesToCareServiceESs(careServices));
                } catch (Exception e) {
                    log.warn("Exception, msg={}", e.getLocalizedMessage());
                }

                esGroomers.add(groomerES);
            }

            groomerESRepository.saveAll(esGroomers);

            if (result.isLast()) {
                break;
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }

            pageNumber++;
            page = PageRequest.of(pageNumber, pageSize);

            result = groomerRepository.findAll(page);
        }

        log.info("Groomers have been loaded into Elasticsearch!");

        return new ApiDefaultResponseDTO("Groomers have been loaded into Elasticsearch!");
    }

}
