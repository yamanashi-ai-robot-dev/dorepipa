package jp.co.ysk.pepper.pac2017.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.co.ysk.pepper.pac2017.entity.TPlayManageEntity;

/**
 * Pepper莨夊ｩｱ繝槭せ繧ｿ縺ｮDao繧､繝ｳ繧ｿ繝ｼ繝輔ぉ繝ｼ繧ｹ.
 */
@ConfigAutowireable
@Dao
public interface TPlayManageDao {

    @Select
    int selectLatestPlayId();

    @Select
    TPlayManageEntity selectById(Integer playId);

    @Select
    String selectLastBestUser();

    @Insert
    int insert(TPlayManageEntity entity);

    @Update
    int update(TPlayManageEntity entity);
}
