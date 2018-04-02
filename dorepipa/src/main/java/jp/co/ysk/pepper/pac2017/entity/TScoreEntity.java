package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * TScoreEntity
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@Table(name = "t_score")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TScoreEntity {

    @Id
    private int playId;

    @Id
    private int userId;

    private int musicId;

    private int scoreOfPercent;

    private int score;

    private String detail;

    private int scaleMissPt;

    private int tempoMissPt;

    private int shorterMissPt;

    private int littleMissPt;

}
