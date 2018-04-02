package jp.co.ysk.pepper.pac2017.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.co.ysk.pepper.pac2017.entity.TKeyRecordEntity;

/**
 * Pepper会話マスタのDaoインターフェース.
 */
@ConfigAutowireable
@Dao
public interface TKeyRecordDao {

    @Select
    List<TKeyRecordEntity> selectByPlayId(int playId);

    @Select
    List<TKeyRecordEntity> selectByPlayIdInPlay(int playId, String playStartDt, String playEndDt);

    @Insert
    int insertKeyData(TKeyRecordEntity entity);

}
