package jp.co.ysk.pepper.pac2017.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * アプリケーションセットアップ設定DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDto {

    private Map<Integer, String> map = new HashMap<Integer, String>();
}
