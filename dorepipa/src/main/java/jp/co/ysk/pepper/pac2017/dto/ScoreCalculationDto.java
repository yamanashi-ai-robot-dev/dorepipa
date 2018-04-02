package jp.co.ysk.pepper.pac2017.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * アプリケーションセットアップ設定DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreCalculationDto {

    private int basePoint;

    private int shorterDeductionPoint;

    private int fasterDeductionPoint;

    private int slowerDeductionPoint;

    private int mistakeDeductionPoint;

    public ScoreCalculationDto() {

    }

    public ScoreCalculationDto(int slowerDeductionPoint) {
        this.slowerDeductionPoint = slowerDeductionPoint;
    }

}
