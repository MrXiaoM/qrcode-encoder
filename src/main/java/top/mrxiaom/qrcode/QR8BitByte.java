package top.mrxiaom.qrcode;

import java.io.UnsupportedEncodingException;

/**
 * QR8BitByte
 *
 * @author Kazuhiko Arase
 */
class QR8BitByte extends QRData {

    public QR8BitByte(String data) {
        super(Mode.MODE_8BIT_BYTE, data);
    }

    public void write(BitBuffer buffer) {

        try {

            byte[] data = getData().getBytes(QRCode.get8BitByteEncoding());

            for (byte datum : data) {
                buffer.put(datum, 8);
            }

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public int getLength() {
        try {
            return getData().getBytes(QRCode.get8BitByteEncoding()).length;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}