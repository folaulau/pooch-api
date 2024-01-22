package com.pooch.api.entity.demo;

import com.pooch.api.dto.DemoCreateDTO;
import com.pooch.api.dto.DemoDTO;

public interface DemoService {

    DemoDTO schedule(DemoCreateDTO demoCreateDTO);

}
