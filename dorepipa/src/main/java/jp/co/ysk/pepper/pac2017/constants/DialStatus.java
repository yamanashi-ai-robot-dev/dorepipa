package jp.co.ysk.pepper.pac2017.constants;

import lombok.Getter;

/**
 * ダイヤルステータス.
 *
 * @author smiyata
 */
public enum DialStatus {

    // DTO作成時初期化用
    /** 初期値（待機） */
    INIT(0),

    // 以下、アダプタ連携ステータス区分値
    /** 受話（受話器を取る） */
    PICK_UP(1),

    /** 終話（受話器を置く） */
    HUNG_UP(2),

    /** 呼出中止 */
    CANCEL(3),

    /** ビジー */
    BUSY(4),

    // 以下、サーバステータス管理用区分値
    /** サーバ側タイムアウト */
    SERVER_TIMED_OUT(10),

    /** Pepepr側タイムアウト */
    PEPPER_TIMED_OUT(11),

    /** エラー */
    ERROR(99);

    /** ステータス値 */
    @Getter
    private final int value;

    /**
     * コンストラクタ
     *
     * @param value
     */
    private DialStatus(int value) {
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