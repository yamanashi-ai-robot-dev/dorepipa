package jp.co.ysk.pepper.pac2017.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ysk.pepper.pac2017.dao.TDeviceUserLinkDao;
import jp.co.ysk.pepper.pac2017.dao.TKeyRecordDao;
import jp.co.ysk.pepper.pac2017.dao.TPlayManageDao;
import jp.co.ysk.pepper.pac2017.dao.TUserDao;
import jp.co.ysk.pepper.pac2017.dto.KeyDataDto;
import jp.co.ysk.pepper.pac2017.dto.TypeSelectResponseDto;
import jp.co.ysk.pepper.pac2017.entity.TDeviceUserLinkEntity;
import jp.co.ysk.pepper.pac2017.entity.TKeyRecordEntity;
import jp.co.ysk.pepper.pac2017.entity.TPlayManageEntity;
import jp.co.ysk.pepper.pac2017.entity.TUserEntity;
import lombok.Getter;

@Service
@Transactional
public class DataOperationService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /** 現在の演奏ID **/
    @Getter
    private int playId = 0;

    /** 現在の演奏形式コード **/
    @Getter
    private int playTypeCd = 0;

    /** 奏者判定中の対象ユーザID **/
    private Integer identifyingUserId = null;

    // /** 時刻同期用のサーバ時間（開始） **/
    // private LocalDateTime syncStartDt = null;
    //
    // /** 時刻同期用のサーバ時間（終了） **/
    // private LocalDateTime syncEndDt = null;
    //
    // /** サーバとPepper時間の誤差（サーバ-Pepper） **/
    // private long serverDtGap = 0;

    static public LocalDateTime startDt;

    @Autowired
    private TKeyRecordDao tKeyRecordDao;

    @Autowired
    private TPlayManageDao tPlayManageDao;

    @Autowired
    private TUserDao tUserDao;

    @Autowired
    private TDeviceUserLinkDao tDeviceUserLinkDao;

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(DataOperationService.class);

    /** ロガー */
    private static final Logger keyLogger = LoggerFactory.getLogger("Key");

    // 時間同期
    // public void syncServerTimes(String requestDt, String responseDt) {
    // if (StringUtils.isEmpty(requestDt)) {
    // logger.error("Server times synchronization error. RequestDt is empty.");
    // return;
    // }
    //
    // if (StringUtils.isEmpty(requestDt)) {
    // logger.error("Server times synchronization error. ResponseDt is empty.");
    // return;
    // }
    //
    // if (syncStartDt == null) {
    // logger.error("Server times synchronization error. SyncStartDt is null.");
    // return;
    // }
    //
    // if (syncEndDt == null) {
    // logger.error("Server times synchronization error. SyncEndDt is null.");
    // return;
    // }
    //
    // serverDtGap = 0L;
    // serverDtGap += Duration
    // .between(LocalDateTime.parse(requestDt,
    // DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss.SSSSSS")),
    // syncStartDt)
    // .toNanos();
    //
    // serverDtGap += Duration
    // .between(LocalDateTime.parse(responseDt,
    // DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss.SSSSSS")),
    // syncEndDt)
    // .toNanos();
    //
    // serverDtGap /= 2L;
    // syncStartDt = null;
    // syncEndDt = null;
    // }

    // 演奏IDの発行・登録
    public void insertNewPlayData(int playTypeCd, TypeSelectResponseDto dto) {
        String playType = null;

        if (playTypeCd == 0) {
            playType = "みんなで";
            dto.setLastBestPlayer(tPlayManageDao.selectLastBestUser());
        } else {
            playType = "ひとりで";
        }
        this.playTypeCd = playTypeCd == 0 ? 0 : 1;

        int lastPlayId = tPlayManageDao.selectLatestPlayId();
        this.playId = lastPlayId + 1;
        tPlayManageDao.insert(new TPlayManageEntity(playId, playType));
    }

    // FaceIdから個人を特定し、名前を返却（IDはメモリ上にキープ）
    public String identifyByFaceId(String faceId) {

        TUserEntity entity = tUserDao.selectByFaceId(faceId);
        if (entity == null) {
            return "nodata";
        }
        if (tDeviceUserLinkDao.countByPlayIdAndUserId(playId, entity.getUserId()) > 0) {
            return "duplicated";
        }
        identifyingUserId = entity.getUserId();
        return entity.getName();
    }

    // VoiceIdから個人を特定し、名前を返却（IDはメモリ上にキープ）
    public String identifyByVoiceId(String voiceId) {

        TUserEntity entity = tUserDao.selectByVoiceId(voiceId);
        if (entity == null) {
            return "nodata";
        }
        if (tDeviceUserLinkDao.countByPlayIdAndUserId(playId, entity.getUserId()) > 0) {
            return "duplicated";
        }
        identifyingUserId = entity.getUserId();
        return entity.getName();
    }

    // DeviceIdとUserIdを紐づけて登録
    public void linkDeviceToUser(int deviceId) {
        tDeviceUserLinkDao.insert(new TDeviceUserLinkEntity(playId, deviceId, identifyingUserId));
        identifyingUserId = null;
    }

    // DeviceIdとUserIdを紐づけて登録
    public String linkOneTimeUser(int deviceId) {
        List<TUserEntity> oneTimeUserList = tUserDao.selectOneTimeUser();
        Collections.shuffle(oneTimeUserList);

        for (TUserEntity entity : oneTimeUserList) {
            if (tDeviceUserLinkDao.countByPlayIdAndUserId(playId, entity.getUserId()) > 0) {
                continue;
            }
            tDeviceUserLinkDao.insert(new TDeviceUserLinkEntity(playId, deviceId, entity.getUserId()));
            identifyingUserId = null;
            return entity.getName();
        }
        return "nodata";
    }

    // 再演奏用にデータを複製・準備
    public void readyForPlayAgain() {
        TPlayManageEntity entity = tPlayManageDao.selectById(playId);

        int newPlayId = entity.getPlayId() + 1;

        tPlayManageDao.insert(new TPlayManageEntity(newPlayId, entity.getType()));
        tDeviceUserLinkDao.selectInsert(newPlayId, entity.getPlayId());
        this.playId = newPlayId;
    }

    // // 演奏開始タイムスタンプの記録＆キー操作情報の受入ON
    // public void updatePlayStartDt(LocalDateTime startDt) {
    // identifyingUserId = null;
    // TPlayManageEntity entity = tPlayManageDao.selectById(playId);
    // entity.setStartDatetime(DateTimeFormatter.ofPattern("yyyy/MM/dd
    // HH:mm:ss.SSS").format(startDt));
    // tPlayManageDao.update(entity);
    // }

    // // 演奏終了タイムスタンプの記録＆キー操作情報の受入OFF
    // public int updatePlayEndDt(LocalDateTime endDt, boolean cancel) {
    // TPlayManageEntity entity = tPlayManageDao.selectById(playId);
    // entity.setEndDatetime(DateTimeFormatter.ofPattern("yyyy/MM/dd
    // HH:mm:ss.SSS").format(endDt));
    // entity.setCancel(cancel);
    // tPlayManageDao.update(entity);
    // logger.info("updatePlayEndDt is finished!");
    // return playId;
    // }

    // 演奏開始タイムスタンプの記録＆キー操作情報の受入ON
    public void updatePlayStartDt(LocalDateTime dt) {
        identifyingUserId = null;
        startDt = dt;
    }

    // 演奏終了タイムスタンプの記録＆キー操作情報の受入OFF
    public int updatePlayEndDt(LocalDateTime endDt, boolean cancel) {
        TPlayManageEntity entity = tPlayManageDao.selectById(playId);
        entity.setStartDatetime(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(startDt));
        entity.setEndDatetime(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(endDt));
        entity.setCancel(cancel);
        tPlayManageDao.update(entity);
        startDt = null;
        logger.info("updatePlayEndDt is finished!");
        return playId;
    }

    // キー操作情報をDB登録
    @Async
    public void insertKeyData(Integer deviceId, Integer keyId, Integer status, LocalDateTime datetime) {
        String logMsg = String.format("タイムスタンプ：%s　ID：%d　デバイスID：%d　ステータス：%d",
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(datetime), deviceId, keyId, status);
        keyLogger.info(logMsg);
        System.out.println(logMsg);
        simpMessagingTemplate.convertAndSend("/topic/test", new KeyDataDto(deviceId, keyId, status, datetime));
        tKeyRecordDao.insertKeyData(new TKeyRecordEntity(playId, deviceId, keyId, status, datetime));
    }

    // // 時刻同期用のサーバ時間（開始）設定用セッター
    // public void setSyncStartTime(LocalDateTime now) {
    // this.syncStartDt = now;
    // }
    //
    // // 時刻同期用のサーバ時間（終了）設定用セッター
    // public void setSyncEndTime(LocalDateTime now) {
    // this.syncEndDt = now;
    // }
}