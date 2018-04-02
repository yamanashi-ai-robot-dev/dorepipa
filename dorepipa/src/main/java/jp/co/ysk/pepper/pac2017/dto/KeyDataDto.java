package jp.co.ysk.pepper.pac2017.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * アプリケーションセットアップ設定DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyDataDto {

    private Integer id;

    private Integer deviceId;

    private Integer status;

    private String datetime;

    public KeyDataDto(Integer deviceId, Integer keyId, Integer status, LocalDateTime dt) {
        this.id = deviceId;
        this.deviceId = keyId;
        this.status = status;
        this.datetime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(dt);
    }

    public KeyDataDto() {
    }
}
