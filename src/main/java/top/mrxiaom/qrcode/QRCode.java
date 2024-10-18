package top.mrxiaom.qrcode;

import top.mrxiaom.qrcode.enums.ErrorCorrectionLevel;
import top.mrxiaom.qrcode.enums.MaskPattern;
import top.mrxiaom.qrcode.enums.Mode;
import top.mrxiaom.qrcode.mode.*;
import top.mrxiaom.qrcode.utils.QRUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * QRコード.
 * <br/>■使い方
 * <br/>(1) 誤り訂正レベル、データ等、諸パラメータを設定します。
 * <br/>(2) make() を呼び出してQRコードを作成します。
 * <br/>(3) getModuleCount() と isDark() で、QRコードのデータを取得します。
 * <br/>
 *
 * @author Kazuhiko Arase
 */
public class QRCode {

    private int typeNumber;
    private Boolean[][] modules;
    private int moduleCount;
    private ErrorCorrectionLevel errorCorrectionLevel;
    private final List<AbstractQRData> qrDataList;

    /**
     * コンストラクタ
     * <br>型番1, 誤り訂正レベルH のQRコードのインスタンスを生成します。
     *
     * @see ErrorCorrectionLevel
     */
    public QRCode() {
        this.typeNumber = 1;
        this.errorCorrectionLevel = ErrorCorrectionLevel.H;
        this.qrDataList = new ArrayList<>();
    }

    /**
     * 型番を取得する。
     *
     * @return 型番
     */
    public int getTypeNumber() {
        return typeNumber;
    }

    /**
     * 型番を設定する。
     *
     * @param typeNumber 型番
     */
    public void setTypeNumber(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    /**
     * 获取二维码纠错等级
     *
     * @return 纠错等级
     * @see ErrorCorrectionLevel
     */
    public ErrorCorrectionLevel getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    /**
     * 设置二维码纠错等级
     *
     * @param errorCorrectionLevel 纠错等级
     * @see ErrorCorrectionLevel
     */
    public void setErrorCorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel) {
        this.errorCorrectionLevel = errorCorrectionLevel;
    }

    /**
     * 追加数据，自动判定模式
     *
     * @param data 数据
     */
    public void addData(String data) {
        addData(data, QRUtil.getMode(data));
    }

    /**
     * 指定模式追加数据
     *
     * @param data 数据
     * @param mode 模式
     * @see Mode
     */
    public void addData(String data, Mode mode) {
        switch (mode) {
            case MODE_NUMBER:
                addData(new QRNumber(data));
                break;
            case MODE_ALPHA_NUM:
                addData(new QRAlphaNum(data));
                break;
            case MODE_8BIT_BYTE:
                addData(new QR8BitByte(data));
                break;
            case MODE_KANJI:
                addData(new QRKanji(data));
                break;
            default:
                throw new IllegalArgumentException("mode:" + mode);
        }
    }

    /**
     * 清空使用 addData 添加的数据
     */
    public void clearData() {
        qrDataList.clear();
    }

    protected void addData(AbstractQRData qrData) {
        qrDataList.add(qrData);
    }

    protected int getDataCount() {
        return qrDataList.size();
    }

    protected AbstractQRData getData(int index) {
        return qrDataList.get(index);
    }

    /**
     * 获取指定坐标是否为暗块
     *
     * @param row 行索引 (0 到 行数-1)
     * @param col 列索引 (0 到 列数-1)
     */
    public boolean isDark(int row, int col) {
        if (modules[row][col] != null) {
            return modules[row][col];
        } else {
            return false;
        }
    }

    public int getModuleCount() {
        return moduleCount;
    }

    /**
     * 制作二维码
     */
    public void make() {
        make(false, getBestMaskPattern());
    }

    private MaskPattern getBestMaskPattern() {

        int minLostPoint = 0;
        MaskPattern pattern = MaskPattern.PATTERN000;

        for (MaskPattern maskPattern : MaskPattern.values()) {
            make(true, maskPattern);

            int lostPoint = QRUtil.getLostPoint(this);

            if (maskPattern == MaskPattern.PATTERN000 || minLostPoint > lostPoint) {
                minLostPoint = lostPoint;
                pattern = maskPattern;
            }
        }

        return pattern;
    }

    private void make(boolean test, MaskPattern maskPattern) {
        // モジュール初期化
        moduleCount = typeNumber * 4 + 17;
        modules = new Boolean[moduleCount][moduleCount];

        // 位置検出パターン及び分離パターンを設定
        setupPositionProbePattern(0, 0);
        setupPositionProbePattern(moduleCount - 7, 0);
        setupPositionProbePattern(0, moduleCount - 7);

        setupPositionAdjustPattern();
        setupTimingPattern();

        setupTypeInfo(test, maskPattern.value);

        if (typeNumber >= 7) {
            setupTypeNumber(test);
        }

        byte[] data = QRUtil.createData(typeNumber, errorCorrectionLevel, qrDataList);
        mapData(data, maskPattern);
    }

    private void mapData(byte[] data, MaskPattern maskPattern) {
        int inc = -1;
        int row = moduleCount - 1;
        int bitIndex = 7;
        int byteIndex = 0;

        for (int col = moduleCount - 1; col > 0; col -= 2) {
            if (col == 6) col--;
            while (true) {
                for (int c = 0; c < 2; c++) {
                    if (modules[row][col - c] == null) {
                        boolean dark = false;
                        if (byteIndex < data.length) {
                            dark = (((data[byteIndex] >>> bitIndex) & 1) == 1);
                        }
                        boolean mask = maskPattern.invoke(row, col - c);
                        if (mask) {
                            dark = !dark;
                        }
                        modules[row][col - c] = dark;
                        bitIndex--;
                        if (bitIndex == -1) {
                            byteIndex++;
                            bitIndex = 7;
                        }
                    }
                }
                row += inc;
                if (row < 0 || moduleCount <= row) {
                    row -= inc;
                    inc = -inc;
                    break;
                }
            }
        }
    }

    /**
     * 设置位置对其样式
     */
    private void setupPositionAdjustPattern() {
        int[] pos = QRUtil.getPatternPosition(typeNumber);
        for (int row : pos) {
            for (int col : pos) {
                if (modules[row][col] != null) {
                    continue;
                }
                for (int r = -2; r <= 2; r++) {
                    for (int c = -2; c <= 2; c++) {
                        modules[row + r][col + c] =
                                r == -2 || r == 2
                                || c == -2 || c == 2
                                || (r == 0 && c == 0);
                    }
                }
            }
        }
    }

    /**
     * 设置位置检测样式
     */
    private void setupPositionProbePattern(int row, int col) {

        for (int r = -1; r <= 7; r++) {

            for (int c = -1; c <= 7; c++) {

                if (row + r <= -1 || moduleCount <= row + r
                        || col + c <= -1 || moduleCount <= col + c) {
                    continue;
                }

                if ((0 <= r && r <= 6 && (c == 0 || c == 6))
                        || (0 <= c && c <= 6 && (r == 0 || r == 6))
                        || (2 <= r && r <= 4 && 2 <= c && c <= 4)) {
                    modules[row + r][col + c] = Boolean.TRUE;
                } else {
                    modules[row + r][col + c] = Boolean.FALSE;
                }
            }
        }
    }

    /**
     * 设定Timing样式
     */
    private void setupTimingPattern() {
        for (int r = 8; r < moduleCount - 8; r++) {
            if (modules[r][6] != null) {
                continue;
            }
            modules[r][6] = r % 2 == 0;
        }
        for (int c = 8; c < moduleCount - 8; c++) {
            if (modules[6][c] != null) {
                continue;
            }
            modules[6][c] = c % 2 == 0;
        }
    }

    /**
     * 设置类型数字
     */
    private void setupTypeNumber(boolean test) {
        int bits = QRUtil.getBCHTypeNumber(typeNumber);
        for (int i = 0; i < 18; i++) {
            boolean mod = !test && ((bits >> i) & 1) == 1;
            modules[i / 3][i % 3 + moduleCount - 8 - 3] = mod;
        }
        for (int i = 0; i < 18; i++) {
            boolean mod = !test && ((bits >> i) & 1) == 1;
            modules[i % 3 + moduleCount - 8 - 3][i / 3] = mod;
        }
    }

    /**
     * 设置类型信息
     */
    private void setupTypeInfo(boolean test, int maskPattern) {
        int data = (errorCorrectionLevel.value << 3) | maskPattern;
        int bits = QRUtil.getBCHTypeInfo(data);
        // 纵向
        for (int i = 0; i < 15; i++) {
            boolean mod = !test && ((bits >> i) & 1) == 1;
            if (i < 6) {
                modules[i][8] = mod;
            } else if (i < 8) {
                modules[i + 1][8] = mod;
            } else {
                modules[moduleCount - 15 + i][8] = mod;
            }
        }

        // 横向
        for (int i = 0; i < 15; i++) {
            boolean mod = !test && ((bits >> i) & 1) == 1;
            if (i < 8) {
                modules[8][moduleCount - i - 1] = mod;
            } else if (i < 9) {
                modules[8][15 - i - 1 + 1] = mod;
            } else {
                modules[8][15 - i - 1] = mod;
            }
        }

        // 固定
        modules[moduleCount - 8][8] = !test;
    }

    /**
     * 创建二维码
     *
     * @param data                 数据
     * @param errorCorrectionLevel 二维码纠错等级
     */
    public static QRCode create(String data, ErrorCorrectionLevel errorCorrectionLevel) {
        Mode mode = QRUtil.getMode(data);
        QRCode qr = new QRCode();
        qr.setErrorCorrectionLevel(errorCorrectionLevel);
        qr.addData(data, mode);

        int length = qr.getData(0).getLength();
        for (int typeNumber = 1; typeNumber <= 10; typeNumber++) {
            if (length <= QRUtil.getMaxLength(typeNumber, mode, errorCorrectionLevel)) {
                qr.setTypeNumber(typeNumber);
                break;
            }
        }
        qr.make();
        return qr;
    }

    private static String _8BitByteEncoding = QRUtil.getJISEncoding();

    public static void set8BitByteEncoding(final String _8BitByteEncoding) {
        QRCode._8BitByteEncoding = _8BitByteEncoding;
    }

    public static String get8BitByteEncoding() {
        return _8BitByteEncoding;
    }
}
