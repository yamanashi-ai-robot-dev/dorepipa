package jp.co.ysk.pepper.pac2017.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * アプリケーションセットアップ設定DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentResultDto {

    private int id;

    private String name;

    private int score;

    private int comparison;

    private String coaching;
}
