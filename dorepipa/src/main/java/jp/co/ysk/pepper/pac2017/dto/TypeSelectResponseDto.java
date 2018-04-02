package jp.co.ysk.pepper.pac2017.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * アプリケーションセットアップ設定DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeSelectResponseDto {

    private String result;

    private String lastBestPlayer;
}
