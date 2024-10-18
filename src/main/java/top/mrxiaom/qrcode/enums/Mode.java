package top.mrxiaom.qrcode.enums;

/**
 * 模式
 *
 * @author Kazuhiko Arase
 */
public enum Mode {

    /**
     * 数字模式
     */
    MODE_NUMBER(1, 0),

    /**
     * 英文加数字模式
     */
    MODE_ALPHA_NUM(1 << 1, 1),

    /**
     * 8位字节模式
     */
    MODE_8BIT_BYTE(1 << 2, 2),

    /**
     * 日文汉字模式 (SJIS编码)
     */
    MODE_KANJI(1 << 3, 3);

    public final int value;
    public final int m;
    Mode(int value, int m) {
        this.value = value;
        this.m = m;
    }

    public int getLengthInBits(int type) {
        if (1 <= type && type < 10) {
            // 1 - 9
            switch (this) {
                case MODE_NUMBER:
                    return 10;
                case MODE_ALPHA_NUM:
                    return 9;
                case MODE_8BIT_BYTE:
                case MODE_KANJI:
                    return 8;
                default:
                    throw new IllegalArgumentException("mode:" + this);
            }
        } else if (type < 27) {
            // 10 - 26
            switch (this) {
                case MODE_NUMBER:
                    return 12;
                case MODE_ALPHA_NUM:
                    return 11;
                case MODE_8BIT_BYTE:
                    return 16;
                case MODE_KANJI:
                    return 10;
                default:
                    throw new IllegalArgumentException("mode:" + this);
            }
        } else if (type < 41) {
            // 27 - 40
            switch (this) {
                case MODE_NUMBER:
                    return 14;
                case MODE_ALPHA_NUM:
                    return 13;
                case MODE_8BIT_BYTE:
                    return 16;
                case MODE_KANJI:
                    return 12;
                default:
                    throw new IllegalArgumentException("mode:" + this);
            }
        } else {
            throw new IllegalArgumentException("type:" + type);
        }
    }
}
