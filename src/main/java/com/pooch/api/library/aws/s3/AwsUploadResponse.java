package com.pooch.api.library.aws.s3;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonInclude(value = Include.NON_NULL)
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AwsUploadResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * s3 object key
     */
    private String            objectKey;

    /**
     * s3 object url
     */
    private String            objectUrl;
}
