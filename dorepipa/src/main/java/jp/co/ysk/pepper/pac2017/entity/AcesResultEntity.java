package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TScoreEntity
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcesResultEntity extends TScoreEntity {

    private int deviceId;

    private String name;

    private boolean oneTime = false;
}
