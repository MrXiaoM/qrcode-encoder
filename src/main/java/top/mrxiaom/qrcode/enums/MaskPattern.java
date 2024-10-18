package top.mrxiaom.qrcode.enums;

import java.util.function.BiFunction;

/**
 * 掩码样式
 *
 * @author Kazuhiko Arase
 */
public enum MaskPattern {

    /**
     * 掩码样式 000
     */
    PATTERN000(0, (row, col) -> (row + col) % 2 == 0),

    /**
     * 掩码样式 001
     */
    PATTERN001(1, (row, col) -> row % 2 == 0),

    /**
     * 掩码样式 010
     */
    PATTERN010(2, (row, col) -> col % 3 == 0),

    /**
     * 掩码样式 011
     */
    PATTERN011(3, (row, col) -> (row + col) % 3 == 0),

    /**
     * 掩码样式 100
     */
    PATTERN100(4, (row, col) -> (row / 2 + col / 3) % 2 == 0),

    /**
     * 掩码样式 101
     */
    PATTERN101(5, (row, col) -> (row * col) % 2 + (row * col) % 3 == 0),

    /**
     * 掩码样式 110
     */
    PATTERN110(6, (row, col) -> ((row * col) % 2 + (row * col) % 3) % 2 == 0),

    /**
     * 掩码样式 111
     */
    PATTERN111(7, (row, col) -> ((row * col) % 3 + (row + col) % 2) % 2 == 0);

    public final int value;
    private final BiFunction<Integer, Integer, Boolean> func;
    MaskPattern(int value, BiFunction<Integer, Integer, Boolean> func) {
        this.value = value;
        this.func = func;
    }

    public boolean invoke(int row, int col) {
        return func.apply(row, col);
    }
}
