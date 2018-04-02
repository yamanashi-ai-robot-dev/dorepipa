package jp.co.ysk.pepper.pac2017.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ysk.pepper.pac2017.dao.MAssessmentConfDao;
import jp.co.ysk.pepper.pac2017.dao.MMusicDao;
import jp.co.ysk.pepper.pac2017.dao.TAnalyzedNoteDao;
import jp.co.ysk.pepper.pac2017.dao.TDeviceUserLinkDao;
import jp.co.ysk.pepper.pac2017.dao.TKeyRecordDao;
import jp.co.ysk.pepper.pac2017.dao.TPlayManageDao;
import jp.co.ysk.pepper.pac2017.dao.TScoreDao;
import jp.co.ysk.pepper.pac2017.dto.AssessmentResponseDto;
import jp.co.ysk.pepper.pac2017.dto.AssessmentResultDto;
import jp.co.ysk.pepper.pac2017.dto.CoachingDto;
import jp.co.ysk.pepper.pac2017.entity.AcesResultEntity;
import jp.co.ysk.pepper.pac2017.entity.MAssessmentConfEntity;
import jp.co.ysk.pepper.pac2017.entity.MMusicEntity;
import jp.co.ysk.pepper.pac2017.entity.TAnalyzedNoteEntity;
import jp.co.ysk.pepper.pac2017.entity.TDeviceUserLinkEntity;
import jp.co.ysk.pepper.pac2017.entity.TKeyRecordEntity;
import jp.co.ysk.pepper.pac2017.entity.TPlayManageEntity;
import jp.co.ysk.pepper.pac2017.entity.TScoreEntity;

@Service
public class AssessmentService {

    private final String msg1 = "<!DOCTYPE html><html lang=\"ja\"><head><style>body{position: relative;text-align: center;margin: 0;padding :0;}#bg{position: relative;background: #eeeeee;text-align: center;margin: 0;padding :0;width: 100%;}#main{position: relative;display: inline-block;background: #323282;text-align: center;margin: 0;padding :0;width: 100%;max-width: 1000px;}#logo{position: relative;margin: 25px 0 0;width: 40%;max-width: 347px;}#ul1{display: inline-block;width: 90%;height: 20px;margin: 20px 0 0 0;border-top: 1px solid #ffffff;}#img_wrap{position: relative;width: 100%;text-align: right;}#pepper{position: absolute;bottom: 1%;left: 5%;width: 18%;max-width: 192px;}#fukidashi{position: relative;width: 70%;margin-right: 5%;}#img_wrap p{position: absolute;display: block;margin: 0;padding: 0;right: 8%;width: 59%;font-size: 14px;color: #323282;text-align: left;}#avg{bottom: 53%;}#comment{top: 53%;}#content{display: inline-block;width: 90%;margin: 10px 0 0 0;padding: 15px 0;background: #ffffff;border-radius: 10px;-webkit-border-radius: 10px;-moz-border-radius: 10px;box-shadow: 0px 3px 3px 0px rgba(0,0,0,0.9);-webkit-box-shadow: 0px 3px 3px 0px rgba(0,0,0,0.9);-moz-box-shadow: 0px 3px 3px 0px rgba(0,0,0,0.9);}table{display: inline-block;}td{text-align: right;font-size: 20px;color: #000000;padding: 5px 3px;}td.name{text-align: left!important;padding-right: 20px!important;}#footer{display: block;width: 100%;margin: 15px 0 0;padding: 15px 0;border-top: 2px solid #666666;background: #cccccc;text-align: left;}#footer p{margin: 0;padding: 1px 0 1px 15px;font-size: 11px;color: #000000;}.br_before{margin-top: 12px!important;}</style></head><body><div id=\"bg\"><div id=\"main\"><img id=\"logo\" src=\"http://ysk-web-service.com/pac2017/img/mail/logo.png\"><div id=\"ul1\"></div><div id=\"img_wrap\"><img id=\"pepper\" src=\"http://ysk-web-service.com/pac2017/img/mail/pepper.png\"><img id=\"fukidashi\" src=\"http://ysk-web-service.com/pac2017/img/mail/fukidashi.png\"><p id=\"avg\">今回の平均点は ";

    private final String msg2 = " 点でした。</p><p id=\"comment\">";

    private final String msg3 = "</p></div><div id=\"content\"><table><tbody>";

    private final String msg4 = "</tbody></table></div><div id=\"footer\"><p>※このメールは画像つきメール(HTML形式)でお届けして</p><p>　おりますので、オンラインでご覧ください。</p><p class=\"br_before\">どれピパ小学校</p><p>pepper@dorepipa.ac.jp</p></div></div></div></body></html>";

    private int keyOpeDelayMillis = 0;

    @Autowired
    private TPlayManageDao tPlayManageDao;

    @Autowired
    private TKeyRecordDao tKeyRecordDao;

    @Autowired
    private TDeviceUserLinkDao tDeviceUserLinkDao;

    @Autowired
    private TAnalyzedNoteDao tAnalyzedNoteDao;

    @Autowired
    private MMusicDao mMusicDao;

    @Autowired
    private TScoreDao tScoreDao;

    @Autowired
    private AsyncAssessmentService asyncAssessmentService;

    @Autowired
    private MAssessmentConfDao mAssessmentConfDao;

    /** 利用実績用ロガー */
    private static final Logger logger = LoggerFactory.getLogger(AssessmentService.class);

    @Transactional
    @Async
    public void setParam() {
        List<MAssessmentConfEntity> list = mAssessmentConfDao.selectAll();
        if (list.isEmpty()) {
            return;
        }

        for (MAssessmentConfEntity entity : list) {
            if (StringUtils.equals(entity.getKeyword(), "keyOpeDelayMillis")) {
                this.keyOpeDelayMillis = Integer.parseInt(entity.getValue());
            }
        }
    }

    // キー操作データから、ON・OFFをセットにして１操作データとしてDB再登録
    @Transactional
    public void readyForAssessment(int playId) {
        // List<TDeviceUserLinkEntity> playerList =
        // tDeviceUserLinkDao.selectByPlayId(playId);

        TPlayManageEntity tPlayManageEntity = tPlayManageDao.selectById(playId);
        List<TKeyRecordEntity> keyOpeList = tKeyRecordDao.selectByPlayIdInPlay(playId,
                tPlayManageEntity.getStartDatetime(), tPlayManageEntity.getEndDatetime());

        Map<String, TAnalyzedNoteEntity> tempMap = new HashMap<String, TAnalyzedNoteEntity>();
        List<TAnalyzedNoteEntity> insertList = new ArrayList<TAnalyzedNoteEntity>();

        for (TKeyRecordEntity entity : keyOpeList) {
            String mapKey = entity.getDeviceId() + "-" + entity.getKeyId();

            if (entity.getStatus() == 1) {
                TAnalyzedNoteEntity insertEntity = new TAnalyzedNoteEntity();
                insertEntity.setPlayId(entity.getPlayId());
                insertEntity.setDeviceId(entity.getDeviceId());
                insertEntity.setKeyId(entity.getKeyId());
                insertEntity
                        .setStartMillis(
                                (int) (Duration
                                        .between(
                                                LocalDateTime.parse(tPlayManageEntity.getStartDatetime(),
                                                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"))
                                                        .plusNanos(keyOpeDelayMillis * 1000000L),
                                                LocalDateTime.parse(entity.getDatetime(),
                                                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")))
                                        .toMillis()));

                tempMap.put(mapKey, insertEntity);

            } else if (entity.getStatus() == 0) {
                if (!tempMap.containsKey(mapKey)) {
                    continue;
                }

                TAnalyzedNoteEntity insertEntity = tempMap.get(mapKey);
                insertEntity
                        .setEndMillis(
                                (int) (Duration
                                        .between(
                                                LocalDateTime.parse(tPlayManageEntity.getStartDatetime(),
                                                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"))
                                                        .plusNanos(keyOpeDelayMillis * 1000000L),
                                                LocalDateTime.parse(entity.getDatetime(),
                                                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")))
                                        .toMillis()));
                insertList.add(insertEntity);
                tempMap.remove(mapKey);

            } else {
                continue;
            }
        }
        tAnalyzedNoteDao.batchInsert(insertList);
        logger.info("readyForAssessment is finished!");
    }

    public void handleAssessment(int playId) {
        List<TDeviceUserLinkEntity> userList = tDeviceUserLinkDao.selectByPlayId(playId);
        List<MMusicEntity> noteList = mMusicDao.selectByMusicId(1); /* かえるの歌固定 */

        CountDownLatch cdl = new CountDownLatch(userList.size());

        for (TDeviceUserLinkEntity entity : userList) {
            asyncAssessmentService.excuteAsyncAssessment(entity, noteList, cdl);
        }

        try {
            boolean cdlRes = cdl.await(60, TimeUnit.SECONDS);
            if (!cdlRes) {
                logger.error(
                        "AsyncAssessment is Timed-out! thread-progress:" + cdl.getCount() + "/" + userList.size());
            }
        } catch (InterruptedException e) {
            logger.error("AsyncAssessment is Failed!");
            e.printStackTrace();
        }

        Integer topUserId = tScoreDao.selectTopUserIdByPlayId(playId);
        if (topUserId != null) {
            TPlayManageEntity entity = tPlayManageDao.selectById(playId);
            entity.setBestUser(topUserId);
            tPlayManageDao.update(entity);
        }
    }

    @Transactional
    public String buildMessageBody(int playId) {
        List<AcesResultEntity> scoreList = tScoreDao.selectAllUserInfoByPlayId(playId);

        int totalScore = 0;

        StringBuilder messageBody = new StringBuilder();
        StringBuilder scorePart = new StringBuilder();

        // 平均点の算出
        for (AcesResultEntity entity : scoreList) {
            totalScore += entity.getScoreOfPercent();

            scorePart.append("<tr><td class=\"name\">");
            scorePart.append(entity.getName());
            scorePart.append("さん</td><td>");
            scorePart.append(entity.getScoreOfPercent());
            scorePart.append("</td><td>点</td></tr>");
        }

        // 一つ桁上がりした平均点
        int avg = Math.round((float) totalScore / (float) scoreList.size() * 10F);

        if (avg > 1000) {
            avg = 1000;
        }

        String advice = null;
        if (avg < 600) {
            advice = "もう少しがんばりましょう。";
        } else if (avg < 750) {
            advice = "細かいミスに注意しましょう。";
        } else if (avg < 900) {
            advice = "いい調子です。次に期待！";
        } else if (avg < 1000) {
            advice = "素晴らしい演奏でした！";
        } else if (avg == 1000) {
            advice = "素晴らしい！パーフェクト！";
        }

        messageBody.append(msg1);
        messageBody.append((int) avg / 10);
        messageBody.append(".");
        messageBody.append((int) avg % 10);
        messageBody.append(msg2);
        messageBody.append(advice);
        messageBody.append(msg3);
        messageBody.append(scorePart.toString());
        messageBody.append(msg4);

        return messageBody.toString();
    }

    @Transactional
    public void extractAssessmentResult(int playId, int playTypeCd, AssessmentResponseDto dto) {

        List<AcesResultEntity> entityList = null;
        if (playTypeCd == 0) {
            entityList = tScoreDao.selectTop3UserInfoByPlayId(playId);
        } else {
            entityList = tScoreDao.selectUserInfoByPlayId(playId);
        }

        if (entityList.isEmpty()) {
            return;
        }

        for (AcesResultEntity currEntity : entityList) {

            AssessmentResultDto resultDto = new AssessmentResultDto();
            dto.getResult().add(resultDto);
            resultDto.setId(currEntity.getDeviceId());
            resultDto.setName(currEntity.getName());
            resultDto.setScore(currEntity.getScoreOfPercent());

            if (currEntity.isOneTime()) {
                resultDto.setComparison(0);
                resultDto.setCoaching("00");
                continue;
            }

            TScoreEntity lastEntity = tScoreDao.selectLastScoreByIdAndMusicId(playId, currEntity.getUserId(),
                    currEntity.getMusicId());

            if (lastEntity == null) {
                resultDto.setComparison(0);
                resultDto.setCoaching("00");
                continue;
            }

            // スコアが変わらない場合
            if (currEntity.getScoreOfPercent() == lastEntity.getScoreOfPercent()) {
                resultDto.setComparison(3);
                resultDto.setCoaching("00");
                continue;
            }

            List<CoachingDto> compareList = new ArrayList<CoachingDto>();
            compareList.add(new CoachingDto("4", lastEntity.getScaleMissPt(), currEntity.getScaleMissPt()));
            compareList.add(new CoachingDto("3", lastEntity.getTempoMissPt(), currEntity.getTempoMissPt()));
            compareList.add(new CoachingDto("2", lastEntity.getShorterMissPt(), currEntity.getShorterMissPt()));
            compareList.add(new CoachingDto("1", lastEntity.getLittleMissPt(), currEntity.getLittleMissPt()));

            // スコアが改善した場合
            if (currEntity.getScoreOfPercent() > lastEntity.getScoreOfPercent()) {
                resultDto.setComparison(1);

                compareList.sort(Comparator.comparing(CoachingDto::getChangeValue).reversed()
                        .thenComparing(Comparator.comparing(CoachingDto::getLastValue).reversed()
                                .thenComparing(Comparator.comparing(CoachingDto::getCode).reversed())));

                if (compareList.get(0).getChangeValue() < 0) {
                    resultDto.setCoaching("00");
                } else {
                    resultDto.setCoaching(compareList.get(0).getCode() + "1");
                }
                continue;
            }

            // スコアが悪化した場合
            resultDto.setComparison(2);

            compareList.sort(Comparator.comparing(CoachingDto::getChangeValue)
                    .thenComparing(Comparator.comparing(CoachingDto::getLastValue)
                            .thenComparing(Comparator.comparing(CoachingDto::getCode).reversed())));

            if (compareList.get(0).getChangeValue() > 0) {
                resultDto.setCoaching("00");
            } else {
                resultDto.setCoaching(compareList.get(0).getCode() + "2");
            }
        }

    }

}