package jp.co.ysk.pepper.pac2017.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jp.co.ysk.pepper.pac2017.entity.TAnalyzedNoteSelectEntity;
import lombok.Data;

/**
 * アプリケーションセットアップ設定DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncAssessmentServiceDto {

    private int[] ptArr = null;

    private int scaleMiss;

    private int tempoMiss;

    private int shorterMiss;

    private int littleMiss;

    private List<TAnalyzedNoteSelectEntity> nonTempoMiss;

    public AsyncAssessmentServiceDto(int num) {
        ptArr = new int[num];
    }

    public AsyncAssessmentServiceDto() {

    }
}
