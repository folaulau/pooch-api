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
    private AddressDAO     addressDAO;

    @Autowired
    private GroomerDAO     groomerDAO;

    @Autowired
    private S3FileDAO      s3FileDAO;

    @Async
    @Override
    public void auditAsync(Groomer groomer) {

        audit(groomer);
    }

    @Override
    public Groomer audit(Groomer groomer) {

        // check for sign up flow
        boolean isReadyToActivate = checkForAddress(groomer) && checkForServices(groomer) && checkForDocuments(groomer);

        // check status
        if (isReadyToActivate) {
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
        Set<CareService> careServices = careServiceDAO.findByGroomerId(groomer.getId()).orElse(new HashSet<>());
        return careServices.size() > 0;
    }

    private boolean checkForAddress(Groomer groomer) {
        Address address = groomer.getAddress();
        return (address != null);
    }

}
