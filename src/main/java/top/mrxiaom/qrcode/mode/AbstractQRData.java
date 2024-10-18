package top.mrxiaom.qrcode.mode;

import top.mrxiaom.qrcode.utils.BitBuffer;
import top.mrxiaom.qrcode.enums.Mode;

/**
 * QRData
 *
 * @author Kazuhiko Arase
 */
public abstract class AbstractQRData {

    private final Mode mode;
    private final String data;
    protected AbstractQRData(Mode mode, String data) {
        this.mode = mode;
        this.data = data;
    }

    public int getMode() {
        return mode.value;
    }

    public String getData() {
        return data;
    }

    public abstract int getLength();

    public abstract void write(BitBuffer buffer);

    public int getLengthInBits(int type) {
        return mode.getLengthInBits(type);
    }
}
