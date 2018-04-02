package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * TDeviceUserLinkEntity
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@Table(name = "t_device_user_link")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TDeviceUserLinkEntity {

    @Id
    private Integer playId;

    private Integer deviceId;

    private Integer userId;

    public TDeviceUserLinkEntity(Integer playId, Integer deviceId, Integer userId) {
        this.playId = playId;
        this.deviceId = deviceId;
        this.userId = userId;
    }

    public TDeviceUserLinkEntity() {
    }
}
