package jp.co.ysk.pepper.pac2017.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * アプリケーションセットアップ設定DTO
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@Table(name = "t_key_record")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TKeyRecordEntity {

    @Id
    private int keyRecordId;

    private int playId;

    private int deviceId;

    private int keyId;

    private int status;

    private String datetime;

    public TKeyRecordEntity(int playId, int deviceId, int keyId, int status, LocalDateTime datetime) {
        this.playId = playId;
        this.deviceId = deviceId;
        this.keyId = keyId;
        this.status = status;
        this.datetime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(datetime);
    }

    // public TKeyRecordEntity(int playId, KeyDataDto dto) {
    // this.playId = playId;
    // this.deviceId = dto.getDeviceId();
    // this.keyId = dto.getKeyId();
    // this.status = dto.getStatus();
    // this.datetime = dto.getDatetime();
    // }

    public TKeyRecordEntity() {
    }
}
