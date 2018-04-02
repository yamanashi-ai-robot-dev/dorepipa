package jp.co.ysk.pepper.pac2017.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * CoachingDto
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingDto {

    private String code;

    private int changeValue;

    private int lastValue;

    private int currentValue;

    public CoachingDto(String code, int lastValue, int currentValue) {
        this.code = code;
        this.changeValue = lastValue - currentValue;
        this.lastValue = lastValue;
        this.currentValue = currentValue;
    }

    public CoachingDto() {

    }
}
