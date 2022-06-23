package com.pooch.api.entity.groomer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.address.AddressDAO;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceDAO;
import com.pooch.api.entity.s3file.S3File;
import com.pooch.api.entity.s3file.S3FileDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerAuditServiceImp implements GroomerAuditService {

  @Autowired
  private CareServiceDAO careServiceDAO;

  @Autowired
  private AddressDAO addressDAO;

  @Autowired
  private GroomerDAO groomerDAO;

  @Autowired
  private S3FileDAO s3FileDAO;

  @Async
  @Override
  public void auditAsync(Groomer groomer) {

    audit(groomer);
  }

  @Override
  public Groomer audit(Groomer groomer) {

    // check for sign up flow
    boolean isReadyToActivate = checkForInfo(groomer) && checkForServices(groomer);// &&
                                                                                   // checkForDocuments(groomer);

    // check status
    if (isReadyToActivate) {

      groomer.setListing(true);

      if (groomer.isStripeReady()) {
        groomer.setStatus(GroomerStatus.ACTIVE);
      } else {
        groomer.setStatus(GroomerStatus.PENDING_STRIPE);
      }
    } else {
      groomer.setStatus(GroomerStatus.SIGNING_UP);
    }

    return groomerDAO.save(groomer);
  }

  private boolean checkForDocuments(Groomer groomer) {
    List<S3File> files = s3FileDAO.getByGroomerId(groomer.getId());
    return files != null && files.size() > 1;
  }

  private boolean checkForServices(Groomer groomer) {
    Set<CareService> careServices =
        careServiceDAO.findByGroomerId(groomer.getId()).orElse(new HashSet<>());

    if (careServices.size() == 0) {
      return false;
    }

    boolean anyNonPopulatedPrice = careServices.stream().filter(cs -> {
      return !isCarePricePopulated(cs);
    }).findFirst().isPresent();


    if (anyNonPopulatedPrice) {
      return false;
    }

    return true;
  }

  private boolean isCarePricePopulated(CareService cs) {
    if (cs.isServiceSmall() == false || cs.getSmallPrice() == null || cs.getSmallPrice() == 0) {
      return false;
    } else if (cs.isServiceMedium() == false || cs.getMediumPrice() == null
        || cs.getMediumPrice() == 0) {
      return false;
    } else if (cs.isServiceLarge() == false || cs.getLargePrice() == null
        || cs.getLargePrice() == 0) {
      return false;
    }
    return true;
  }

  private boolean checkForInfo(Groomer groomer) {

    if (groomer.getFirstName() == null || groomer.getLastName() == null) {
      return false;
    }

    Address address = groomer.getAddress();

    if (address == null || address.getLatitude() == null || address.getLongitude() == null) {
      return false;
    }

    return true;
  }

}
