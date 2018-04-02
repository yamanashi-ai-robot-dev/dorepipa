package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * TUserEntity
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TAnalyzedNoteSelectEntity {

    @Id
    private int deviceId;

    private int playId;

    private int keyId;

    private int startMillis;

    private int endMillis;

}
