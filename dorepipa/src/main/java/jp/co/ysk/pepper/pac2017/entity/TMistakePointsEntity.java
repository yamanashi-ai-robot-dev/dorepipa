package jp.co.ysk.pepper.pac2017.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jp.co.ysk.pepper.pac2017.constants.MistakeCd;
import lombok.Data;

/**
 * アプリケーションセットアップ設定DTO
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
@Table(name = "t_mistake_points")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TMistakePointsEntity {

    @Id
    private int mistakePointId;

    private int playId;

    private int userId;

    private int musicId;

    private int noteId;

    private int mistakeCd;

    private int deductedPoint;

    public TMistakePointsEntity(int playId, int userId, int musicId, int noteId, int mistakeCd, int deductedPoint) {

        this.playId = playId;
        this.userId = userId;
        this.musicId = musicId;
        this.noteId = noteId;
        this.mistakeCd = mistakeCd;
        this.deductedPoint = deductedPoint;
    }

    public TMistakePointsEntity(TDeviceUserLinkEntity tDeviceUserLinkEntity, MMusicEntity mMusicEntity,
            MistakeCd mistakeCd, int deductedPoint) {

        this.playId = tDeviceUserLinkEntity.getPlayId();
        this.userId = tDeviceUserLinkEntity.getUserId();
        this.musicId = mMusicEntity.getMusicId();
        this.noteId = mMusicEntity.getNoteId();
        this.mistakeCd = mistakeCd.getValue();
        this.deductedPoint = deductedPoint;
    }

    public TMistakePointsEntity() {
    }
}
