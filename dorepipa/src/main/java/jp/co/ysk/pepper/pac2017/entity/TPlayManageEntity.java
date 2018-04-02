package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * TPlayManageEntity
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@Table(name = "t_play_manage")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TPlayManageEntity {

    @Id
    private Integer playId;

    private String type;

    private String startDatetime;

    private String endDatetime;

    private Boolean cancel;

    private Integer bestUser;

    public TPlayManageEntity(Integer playId, String type) {
        this.playId = playId;
        this.type = type;
    }

    public TPlayManageEntity() {
    }
}
