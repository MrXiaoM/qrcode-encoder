package top.mrxiaom.qrcode.enums;

/**
 * 二维码纠错等级
 *
 * @author Kazuhiko Arase
 */
public enum ErrorCorrectionLevel {

    /**
     * 复原能力 7%.
     */
    L(1, 0),

    /**
     * 复原能力 15%.
     */
    M(0, 1),

    /**
     * 复原能力 25%.
     */
    Q(3, 2),

    /**
     * 复原能力 30%.
     */
    H(2, 3);

    public final int value;
    public final int e;
    ErrorCorrectionLevel(int value, int e) {
        this.value = value;
        this.e = e;
    }
}
