package com.pooch.api.entity.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.DemoCreateDTO;
import com.pooch.api.dto.DemoDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.entity.notification.NotificationService;
import com.pooch.api.entity.notification.email.dynamicdata.DemoInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemoServiceImp implements DemoService {

    @Autowired
    private DemoDAO             demoDAO;

    @Autowired
    private EntityDTOMapper     entityDTOMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public DemoDTO schedule(DemoCreateDTO demoCreateDTO) {
        Demo demo = entityDTOMapper.mapDemoCreateDTOToDemo(demoCreateDTO);
        demo = demoDAO.save(demo);
        
        notificationService.sendDemoConfirmationToGroomer(demo);

        DemoInfo demoInfo = entityDTOMapper.mapDemoToDemoInfo(demo);

        String services = String.join(", ", demo.getServices());
        demoInfo.setServices(services);

        if (demo.isMarketingCommunicationConsent()) {
            demoInfo.setMarketingCommunicationConsent("Yes");
        } else {
            demoInfo.setMarketingCommunicationConsent("No");
        }

        notificationService.sendDemoRequestDataToAdmins(demoInfo);

        return entityDTOMapper.mapDemoToDemoDTO(demo);
    }
}
