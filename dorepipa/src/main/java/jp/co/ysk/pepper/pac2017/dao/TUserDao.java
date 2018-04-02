package jp.co.ysk.pepper.pac2017.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.co.ysk.pepper.pac2017.entity.TUserEntity;

/**
 * TUserDao
 */
@ConfigAutowireable
@Dao
public interface TUserDao {

    @Select
    TUserEntity selectByFaceId(String faceId);

    @Select
    TUserEntity selectByVoiceId(String voiceId);

    @Select
    List<TUserEntity> selectOneTimeUser();
}
