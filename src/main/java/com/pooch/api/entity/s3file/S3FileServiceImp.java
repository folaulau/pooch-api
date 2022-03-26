package com.pooch.api.entity.s3file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.S3FileDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3FileServiceImp implements S3FileService {

    @Autowired
    private S3FileDAO s3FileDAO;

    @Override
    public S3FileDTO refreshTTL(String uuid) {
        // TODO Auto-generated method stub
        return null;
    }

}
