package jp.co.ysk.pepper.pac2017.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.co.ysk.pepper.pac2017.entity.MAssessmentConfEntity;

/**
 * TUserDao
 */
@ConfigAutowireable
@Dao
public interface MAssessmentConfDao {

    @Select
    List<MAssessmentConfEntity> selectAll();
}
