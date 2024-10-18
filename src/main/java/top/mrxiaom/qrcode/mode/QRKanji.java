package top.mrxiaom.qrcode.mode;

import top.mrxiaom.qrcode.utils.BitBuffer;
import top.mrxiaom.qrcode.enums.Mode;
import top.mrxiaom.qrcode.utils.QRUtil;

import java.io.UnsupportedEncodingException;

/**
 * QRKanji
 *
 * @author Kazuhiko Arase
 */
public class QRKanji extends AbstractQRData {

    public QRKanji(String data) {
        super(Mode.MODE_KANJI, data);
    }

    public void write(BitBuffer buffer) {
        try {
            byte[] data = getData().getBytes(QRUtil.JIS_ENCODING);
            int i = 0;
            while (i + 1 < data.length) {
                int c = ((0xff & data[i]) << 8) | (0xff & data[i + 1]);
                if (0x8140 <= c && c <= 0x9FFC) {
                    c -= 0x8140;
                } else if (0xE040 <= c && c <= 0xEBBF) {
                    c -= 0xC140;
                } else {
                    throw new IllegalArgumentException("illegal char at " + (i + 1) + "/" + Integer.toHexString(c));
                }
                c = ((c >>> 8) & 0xff) * 0xC0 + (c & 0xff);
                buffer.put(c, 13);
                i += 2;
            }
            if (i < data.length) {
                throw new IllegalArgumentException("illegal char at " + (i + 1));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public int getLength() {
        try {
            return getData().getBytes(QRUtil.JIS_ENCODING).length / 2;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
