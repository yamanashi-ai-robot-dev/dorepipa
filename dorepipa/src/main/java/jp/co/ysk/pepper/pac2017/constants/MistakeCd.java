package jp.co.ysk.pepper.pac2017.constants;

import lombok.Getter;

/**
 * ダイヤルステータス.
 *
 * @author smiyata
 */
public enum MistakeCd {

    /** 音が出ていない */
    NO_SPEAK(0),

    /** 音（音階の間違い） */
    SCALE_MISS(1),

    /** テンポが速い（ハシっている） */
    FASTER(2),

    /** テンポが遅い（モタっている） */
    SLOWER(3),

    /** 音の長さが足りていない */
    SHORTER(4);

    /** ステータス値 */
    @Getter
    private final int value;

    /**
     * コンストラクタ
     *
     * @param value
     */
    private MistakeCd(int value) {
        this.value = value;
    }

    /**
     * 引数がvalueと一致するか判定
     *
     * @param value
     * @return
     */
    public boolean is(int arg) {
        return this.value == arg;
    }
}