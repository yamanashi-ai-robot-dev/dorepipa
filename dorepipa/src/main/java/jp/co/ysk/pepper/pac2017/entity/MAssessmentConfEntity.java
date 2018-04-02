package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * MAssessmentConfEntity
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@Table(name = "m_assessment_conf")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MAssessmentConfEntity {

    @Id
    private int id;

    private String keyword;

    private String value;

    private String explanation;
}
