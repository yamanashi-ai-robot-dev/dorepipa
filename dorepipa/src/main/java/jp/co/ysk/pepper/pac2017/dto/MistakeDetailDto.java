package jp.co.ysk.pepper.pac2017.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * MistakeDetailDto
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MistakeDetailDto {

    private int measure;

    private int numInMeasure;

    private int keyId;

    private int missType;
}
