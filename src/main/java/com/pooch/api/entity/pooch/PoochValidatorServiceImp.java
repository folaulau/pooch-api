package com.pooch.api.entity.pooch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PoochValidatorServiceImp implements PoochValidatorService {

  @Autowired
  private PoochDAO poochDAO;

  @Override
  public void validateCreateUpdatePooch(Parent parent, PoochCreateUpdateDTO poochCreateUpdateDTO) {

    String uuid = poochCreateUpdateDTO.getUuid();

    Pooch pooch = null;

    if (uuid != null && !uuid.trim().isEmpty()) {
      pooch = poochDAO.getByUuid(uuid).orElseThrow(
          () -> new ApiException("Pooch not found", "pooch not found for uuid=" + uuid));

      if (!parent.getId().equals(pooch.getParent().getId())) {
        throw new ApiException("Pooch does not belong to parent",
            "parent id=" + parent.getId() + ", pooch parent id=" + pooch.getParent().getId());
      }
    }



  }
}
