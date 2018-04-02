package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * MMusicEntity
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@Table(name = "m_music")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MMusicEntity {

    @Id
    private int musicId;

    private int noteId;

    private int measure;

    private int numInMeasure;

    private int keyId;

    private int startMillis;

    private int endMillis;

    private int ckLength;

    private int ckFaster;

    private int ckSlower;
}
