package com.pooch.api.entity.s3file;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class S3FileDAOImp implements S3FileDAO {

    @Autowired
    private S3FileRepository s3FileRepository;

    @Override
    public S3File save(S3File s3File) {
        return s3FileRepository.saveAndFlush(s3File);
    }

    @Override
    public List<S3File> save(List<S3File> s3Files) {

        s3Files = s3FileRepository.saveAll(s3Files);
        s3FileRepository.flush();

        return s3Files;
    }
}
