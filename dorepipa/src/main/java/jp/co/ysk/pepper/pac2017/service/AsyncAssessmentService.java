package jp.co.ysk.pepper.pac2017.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ysk.pepper.pac2017.constants.MistakeCd;
import jp.co.ysk.pepper.pac2017.dao.MAssessmentConfDao;
import jp.co.ysk.pepper.pac2017.dao.TAnalyzedNoteDao;
import jp.co.ysk.pepper.pac2017.dao.TMistakePointsDao;
import jp.co.ysk.pepper.pac2017.dao.TScoreDao;
import jp.co.ysk.pepper.pac2017.dto.AsyncAssessmentServiceDto;
import jp.co.ysk.pepper.pac2017.entity.MAssessmentConfEntity;
import jp.co.ysk.pepper.pac2017.entity.MMusicEntity;
import jp.co.ysk.pepper.pac2017.entity.TAnalyzedNoteSelectEntity;
import jp.co.ysk.pepper.pac2017.entity.TDeviceUserLinkEntity;
import jp.co.ysk.pepper.pac2017.entity.TMistakePointsEntity;
import jp.co.ysk.pepper.pac2017.entity.TScoreEntity;

@Service
@Transactional
public class AsyncAssessmentService {

    private int maxDeductionPointByFaster = 15;

    private int maxDeductionPointBySlower = 15;

    private int maxDeductionPointByShorter = 30;

    private int uncheckTempoMissMillis = 250;

    private int uncheckLengthMissMillis = 250;

    @Autowired
    private TAnalyzedNoteDao tAnalyzedNoteDao;

    @Autowired
    private TMistakePointsDao tMistakePointsDao;

    @Autowired
    private TScoreDao tScoreDao;

    @Autowired
    private MAssessmentConfDao mAssessmentConfDao;

    @Async
    public void setParams() {
        List<MAssessmentConfEntity> list = mAssessmentConfDao.selectAll();
        if (list.isEmpty()) {
            return;
        }

        for (MAssessmentConfEntity entity : list) {
            switch (entity.getKeyword()) {
            case "maxDeductionPointByFaster":
                this.maxDeductionPointByFaster = Integer.parseInt(entity.getValue());
                break;
            case "maxDeductionPointBySlower":
                this.maxDeductionPointBySlower = Integer.parseInt(entity.getValue());
                break;
            case "maxDeductionPointByShorter":
                this.maxDeductionPointByShorter = Integer.parseInt(entity.getValue());
                break;
            case "uncheckTempoMissMillis":
                this.uncheckTempoMissMillis = Integer.parseInt(entity.getValue());
                break;
            case "uncheckLengthMissMillis":
                this.uncheckLengthMissMillis = Integer.parseInt(entity.getValue());
                break;
            default:
                break;
            }
        }
    }

    // キー操作データから、ON・OFFをセットにして１操作データとしてDB再登録
    @Async
    public void excuteAsyncAssessment(TDeviceUserLinkEntity tDeviceUserLinkEntity, List<MMusicEntity> noteList,
            CountDownLatch cdl) {
        AsyncAssessmentServiceDto dto = new AsyncAssessmentServiceDto(noteList.size());

        for (int i = 0; i < noteList.size(); i++) {
            MMusicEntity mMusicEntity = noteList.get(i);
            dto.getPtArr()[i] = 100;
            mistakenNoteAssessment(tDeviceUserLinkEntity, mMusicEntity, dto, i);
            exactNoteAssessment(tDeviceUserLinkEntity, mMusicEntity, dto, i);
        }

        // モタりの関係で後から修正が入ることがあるので、全部の特典集計が完了してから統計
        StringBuilder scoreDetail = new StringBuilder();
        int totalpoint = 0;
        for (int i = 0; i < noteList.size(); i++) {
            int point = 0;
            if (dto.getPtArr()[i] > 100) {
                point = 100;
            } else if (dto.getPtArr()[i] > 0) {
                point = dto.getPtArr()[i];
            }
            totalpoint += point;

            if (i != 0) {
                scoreDetail.append(",");
            }
            scoreDetail.append(point);
        }

        TScoreEntity tScoreEntity = new TScoreEntity();
        tScoreEntity.setPlayId(tDeviceUserLinkEntity.getPlayId());
        tScoreEntity.setMusicId(noteList.get(0).getMusicId());
        tScoreEntity.setUserId(tDeviceUserLinkEntity.getUserId());
        tScoreEntity.setScoreOfPercent(totalpoint / noteList.size());
        tScoreEntity.setScore(totalpoint);
        tScoreEntity.setDetail(scoreDetail.toString());
        tScoreEntity.setScaleMissPt(dto.getScaleMiss());
        tScoreEntity.setTempoMissPt(dto.getTempoMiss());
        tScoreEntity.setShorterMissPt(dto.getShorterMiss());
        tScoreEntity.setLittleMissPt(dto.getLittleMiss());

        if (tScoreDao.insert(tScoreEntity) == 1) {
            cdl.countDown();
        } else {
            System.out.println("Score insert error! userId:" + tDeviceUserLinkEntity.getUserId());
        }
    }

    private void mistakenNoteAssessment(TDeviceUserLinkEntity tDeviceUserLinkEntity, MMusicEntity mMusicEntity,
            AsyncAssessmentServiceDto dto, int index) {

        dto.setNonTempoMiss(new ArrayList<TAnalyzedNoteSelectEntity>());

        List<TAnalyzedNoteSelectEntity> mistakenNoteList = tAnalyzedNoteDao.selectMistakenNote(
                tDeviceUserLinkEntity.getPlayId(), tDeviceUserLinkEntity.getDeviceId(), mMusicEntity.getKeyId(),
                mMusicEntity.getStartMillis(), mMusicEntity.getEndMillis());

        if (mistakenNoteList.size() == 0) {
            return;
        }

        int baseNoteLangth = mMusicEntity.getEndMillis() - mMusicEntity.getStartMillis();

        for (TAnalyzedNoteSelectEntity tAnalyzedNoteSelectEntity : mistakenNoteList) {

            // 開始終了時間を調整
            int noteLength = calcNoteLength(mMusicEntity, tAnalyzedNoteSelectEntity);

            int deductionPoint = 0;

            // テンポが速い（ハシっている）場合
            if (tAnalyzedNoteSelectEntity.getKeyId() == mMusicEntity.getCkFaster()
                    && tAnalyzedNoteSelectEntity.getStartMillis() >= mMusicEntity.getStartMillis()
                    && tAnalyzedNoteSelectEntity.getEndMillis() > mMusicEntity.getEndMillis()) {

                if (noteLength > uncheckTempoMissMillis) {
                    int point = Math.round(maxDeductionPointByFaster * (float) (noteLength - uncheckTempoMissMillis)
                            / (float) (baseNoteLangth - uncheckTempoMissMillis));
                    deductionPoint = point > maxDeductionPointByFaster ? maxDeductionPointByFaster : point;

                    if (deductionPoint >= 5) {
                        tMistakePointsDao.insert(new TMistakePointsEntity(tDeviceUserLinkEntity, mMusicEntity,
                                MistakeCd.FASTER, deductionPoint));
                        dto.setTempoMiss(dto.getTempoMiss() + deductionPoint);
                    } else if (deductionPoint > 0) {
                        dto.setLittleMiss(dto.getLittleMiss() + deductionPoint);
                    }
                    dto.getPtArr()[index + 1] -= deductionPoint;
                }
                continue;
            }

            // テンポが遅い（モタっている）場合
            if (tAnalyzedNoteSelectEntity.getKeyId() == mMusicEntity.getCkSlower()
                    && tAnalyzedNoteSelectEntity.getStartMillis() < mMusicEntity.getStartMillis()
                    && tAnalyzedNoteSelectEntity.getEndMillis() <= mMusicEntity.getEndMillis()) {

                if (noteLength > uncheckTempoMissMillis) {
                    int point = Math.round(maxDeductionPointBySlower * (float) (noteLength - uncheckTempoMissMillis)
                            / (float) (baseNoteLangth - uncheckTempoMissMillis));
                    deductionPoint = point > maxDeductionPointBySlower ? maxDeductionPointBySlower : point;

                    if (deductionPoint >= 5) {
                        tMistakePointsDao.insert(new TMistakePointsEntity(tDeviceUserLinkEntity, mMusicEntity,
                                MistakeCd.SLOWER, deductionPoint));
                        dto.setTempoMiss(dto.getTempoMiss() + deductionPoint);
                    } else if (deductionPoint > 0) {
                        dto.setLittleMiss(dto.getLittleMiss() + deductionPoint);
                    }
                    dto.getPtArr()[index + 1] -= deductionPoint;
                }
                continue;
            }

            dto.getNonTempoMiss().add(tAnalyzedNoteSelectEntity);
        }

    }

    private void exactNoteAssessment(TDeviceUserLinkEntity tDeviceUserLinkEntity, MMusicEntity mMusicEntity,
            AsyncAssessmentServiceDto dto, int index) {

        int deductionPoint = 0;

        List<TAnalyzedNoteSelectEntity> exactNoteList = tAnalyzedNoteDao.selectExactNote(
                tDeviceUserLinkEntity.getPlayId(), tDeviceUserLinkEntity.getDeviceId(), mMusicEntity.getKeyId(),
                mMusicEntity.getStartMillis(), mMusicEntity.getEndMillis());

        // 規定の音が出ている場合
        if (exactNoteList.size() > 0 || mMusicEntity.getCkLength() == 0) {

            int exactLengthByMillis = 0;

            for (int i = mMusicEntity.getStartMillis(); i < mMusicEntity.getEndMillis(); i++) {

                if (!dto.getNonTempoMiss().isEmpty()) {
                    boolean hasMiss = false;
                    // 該当のミリ秒で間違った音が出ていた場合、成功チェックを飛ばして次へ
                    for (TAnalyzedNoteSelectEntity missEntity : dto.getNonTempoMiss()) {
                        if (missEntity.getStartMillis() <= i && i < missEntity.getEndMillis()) {
                            hasMiss = true;
                            break;
                        }
                    }
                    if (hasMiss)
                        continue;
                }

                // 長さチェックなし（発音チェックなし）の場合、成功として加算 -> 次へ
                if (mMusicEntity.getCkLength() == 0) {
                    exactLengthByMillis++;
                    continue;
                }

                // 該当のミリ秒で音が出ていた場合、成功として加算
                for (TAnalyzedNoteSelectEntity exactEntity : exactNoteList) {
                    if (exactEntity.getStartMillis() <= i && i < exactEntity.getEndMillis()) {
                        exactLengthByMillis++;
                        break;
                    }
                }
            }

            if (exactLengthByMillis > 0) {
                // 全体に占める長さの割合を算出
                int noteLength = mMusicEntity.getEndMillis() - mMusicEntity.getStartMillis();
                int needNoteMillis = (noteLength * 0.5f) > ((float) uncheckLengthMissMillis)
                        ? Math.round(noteLength * 0.5f) : noteLength - uncheckLengthMissMillis;

                if (needNoteMillis > exactLengthByMillis) {
                    int point = Math.round(maxDeductionPointByShorter * (float) (needNoteMillis - exactLengthByMillis)
                            / (float) needNoteMillis);
                    deductionPoint = point > maxDeductionPointByShorter ? maxDeductionPointByShorter : point;
                }
            } else {
                // 一回も単独で規定の音が出ていない場合、減点100
                deductionPoint = 100;
            }
        } else {
            // 規定の音が出ていない場合、減点100
            deductionPoint = 100;
        }

        if (deductionPoint > 5) {
            MistakeCd missReason = null;

            if (!dto.getNonTempoMiss().isEmpty()) {
                missReason = MistakeCd.SCALE_MISS;
                dto.setScaleMiss(dto.getScaleMiss() + deductionPoint);
            } else if (exactNoteList.isEmpty()) {
                missReason = MistakeCd.NO_SPEAK;
                dto.setScaleMiss(dto.getScaleMiss() + deductionPoint);
            } else {
                missReason = MistakeCd.SHORTER;
                dto.setShorterMiss(dto.getShorterMiss() + deductionPoint);
            }
            tMistakePointsDao
                    .insert(new TMistakePointsEntity(tDeviceUserLinkEntity, mMusicEntity, missReason, deductionPoint));
        } else if (deductionPoint > 0) {
            dto.setLittleMiss(dto.getLittleMiss() + deductionPoint);
        }
        dto.getPtArr()[index] -= deductionPoint;
    }

    private int calcNoteLength(MMusicEntity mMusicEntity, TAnalyzedNoteSelectEntity tAnalyzedNoteSelectEntity) {
        // 開始終了時間 基準値
        int baseStartMillis = mMusicEntity.getStartMillis();
        int baseEndMillis = mMusicEntity.getEndMillis();

        // 開始終了時間 実測値
        int actualStartMillis = tAnalyzedNoteSelectEntity.getStartMillis();
        int actualEndMillis = tAnalyzedNoteSelectEntity.getEndMillis();

        // 判定使用値
        int startMillis = baseStartMillis > actualStartMillis ? baseStartMillis : actualStartMillis;
        int endMillis = baseEndMillis < actualEndMillis ? baseEndMillis : actualEndMillis;

        return endMillis - startMillis;
    }
}