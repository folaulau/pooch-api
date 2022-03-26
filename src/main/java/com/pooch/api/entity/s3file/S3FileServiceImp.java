package com.pooch.api.entity.s3file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3FileServiceImp implements S3FileService {

    @Autowired
    private S3FileDAO s3FileDAO;

    
}
