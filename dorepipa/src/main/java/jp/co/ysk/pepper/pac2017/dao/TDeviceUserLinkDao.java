package jp.co.ysk.pepper.pac2017.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.co.ysk.pepper.pac2017.entity.TDeviceUserLinkEntity;

/**
 * TUserDao
 */
@ConfigAutowireable
@Dao
public interface TDeviceUserLinkDao {

    @Select
    List<TDeviceUserLinkEntity> selectByPlayId(int playId);

    @Select
    int countByPlayIdAndUserId(int playId, int userId);

    @Insert
    int insert(TDeviceUserLinkEntity entity);

    @Insert(sqlFile = true)
    int selectInsert(int newPlayId, int oldPlayId);
}
