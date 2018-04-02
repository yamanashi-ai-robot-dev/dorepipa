package jp.co.ysk.pepper.pac2017.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * アプリケーションセットアップ設定DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentResponseDto {

    private int playId;

    private List<AssessmentResultDto> result = new ArrayList<AssessmentResultDto>();
}
