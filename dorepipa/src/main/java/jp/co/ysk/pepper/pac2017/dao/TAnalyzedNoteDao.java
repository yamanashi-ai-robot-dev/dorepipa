package jp.co.ysk.pepper.pac2017.dao;

import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.co.ysk.pepper.pac2017.entity.TAnalyzedNoteEntity;
import jp.co.ysk.pepper.pac2017.entity.TAnalyzedNoteSelectEntity;

/**
 * TUserDao
 */
@ConfigAutowireable
@Dao
public interface TAnalyzedNoteDao {

    @Select
    int selectCountById(int playId);

    @Select
    List<TAnalyzedNoteSelectEntity> selectExactNote(int playId, int deviceId, int keyId, int baseStartMillis, int baseEndMillis);

    @Select
    List<TAnalyzedNoteSelectEntity> selectMistakenNote(int playId, int deviceId, int keyId, int baseStartMillis, int baseEndMillis);

    @BatchInsert
    int[] batchInsert(List<TAnalyzedNoteEntity> entityList);
}
