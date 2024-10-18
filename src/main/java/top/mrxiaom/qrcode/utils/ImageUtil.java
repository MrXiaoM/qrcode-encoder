package top.mrxiaom.qrcode.utils;

import top.mrxiaom.qrcode.QRCode;

import java.awt.image.BufferedImage;

public class ImageUtil {

    /**
     * 创建二维码图像
     *
     * @param cellSize 单格大小(像素)
     * @param margin   边框留白(像素)
     */
    public static BufferedImage createImage(QRCode qrCode, int cellSize, int margin) {
        return createImage(qrCode, cellSize, margin, 0x000000, 0xFFFFFF);
    }

    /**
     * 创建二维码图像
     *
     * @param cellSize   单格大小(像素)
     * @param margin     边框留白(像素)
     * @param darkColor  二维码暗色前景(RGB)
     * @param lightColor 二维码亮色背景(RGB)
     */
    public static BufferedImage createImage(QRCode qrCode, int cellSize, int margin, int darkColor, int lightColor) {

        int imageSize = qrCode.getModuleCount() * cellSize + margin * 2;

        BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < imageSize; y++) {

            for (int x = 0; x < imageSize; x++) {

                if (margin <= x && x < imageSize - margin
                        && margin <= y && y < imageSize - margin) {

                    int col = (x - margin) / cellSize;
                    int row = (y - margin) / cellSize;

                    if (qrCode.isDark(row, col)) {
                        image.setRGB(x, y, darkColor);
                    } else {
                        image.setRGB(x, y, lightColor);
                    }

                } else {
                    image.setRGB(x, y, lightColor);
                }
            }
        }

        return image;
    }
}
