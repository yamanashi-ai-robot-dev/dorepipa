package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * MistakeDetailEntity
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MistakeDetailEntity {

    private int measure;

    private int numInMeasure;

    private int keyId;

    private int mistakeCd;
}
