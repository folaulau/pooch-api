package com.pooch.api.entity.pooch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PoochValidatorServiceImp implements PoochValidatorService {

    @Autowired
    private PoochDAO poochDAO;

    @Override
    public void validateCreateUpdatePooch(PoochCreateUpdateDTO poochCreateUpdateDTO) {

        String uuid = poochCreateUpdateDTO.getUuid();
        
        Pooch pooch = null;
        
        if (uuid != null && !uuid.trim().isEmpty()) {
            pooch = poochDAO.getByUuid(uuid).orElseThrow(() -> new ApiException("Pooch not found", "pooch not found for uuid="+uuid));
        }
        
        

    }
}
