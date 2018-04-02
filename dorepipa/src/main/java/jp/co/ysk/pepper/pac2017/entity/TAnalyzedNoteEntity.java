package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * TUserEntity
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@Table(name = "t_analyzed_note")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TAnalyzedNoteEntity {

    @Id
    private int analyzedNoteId;

    private int playId;

    private int deviceId;

    private int keyId;

    private int startMillis;

    private int endMillis;

}
