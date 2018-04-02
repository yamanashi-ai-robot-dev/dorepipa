package jp.co.ysk.pepper.pac2017.dao;

import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.co.ysk.pepper.pac2017.entity.MistakeDetailEntity;
import jp.co.ysk.pepper.pac2017.entity.TMistakePointsEntity;

/**
 * TUserDao
 */
@ConfigAutowireable
@Dao
public interface TMistakePointsDao {

    @Select
    MistakeDetailEntity selectWorstMissPoint(int playId, int userId);

    @Select
    MistakeDetailEntity selectFirstBetterPoint(int currPlayId, int lastPlayId, int userId);

    @Select
    MistakeDetailEntity selectFirstWorsePoint(int currPlayId, int lastPlayId, int userId);

    @Select
    MistakeDetailEntity selectNochangeWorstPoint(int currPlayId, int lastPlayId, int userId);

    @Insert
    int insert(TMistakePointsEntity entity);

    @BatchInsert
    int[] batchInsert(List<TMistakePointsEntity> entityList);
}
