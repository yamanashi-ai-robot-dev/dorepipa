package jp.co.ysk.pepper.pac2017.dao;

import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.co.ysk.pepper.pac2017.entity.AcesResultEntity;
import jp.co.ysk.pepper.pac2017.entity.TScoreEntity;

/**
 * TScoreDao
 */
@ConfigAutowireable
@Dao
public interface TScoreDao {

    @Select
    Integer selectTopUserIdByPlayId(int playId);

    @Select
    List<AcesResultEntity> selectUserInfoByPlayId(int playId);

    @Select
    List<AcesResultEntity> selectTop3UserInfoByPlayId(int playId);

    @Select
    List<AcesResultEntity> selectAllUserInfoByPlayId(int playId);

    @Select
    TScoreEntity selectLastScoreByIdAndMusicId(int playId, int userId, int musicId);

    @Insert
    int insert(TScoreEntity entity);

    @BatchInsert
    int[] batchInsert(List<TScoreEntity> entityList);
}
