# qrcode-encoder

适用于 Java/Kotlin 的二维码编码库，fork 自 [kazuhikoarase/qrcode-generator](https://github.com/kazuhikoarase/qrcode-generator)。

主要做了以下修改
+ 将类移到特定的包里，不要全部平摊到一个包里面
+ 将语言目标版本升级到 `8`
+ 编写中文 Javadoc
+ 发布到 Maven Central
+ 将 `java.awt` 包的引用移到一个特定的工具类 (`ImageUtil`)

## 用法

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("top.mrxiaom:qrcode-encoder:1.0.0")
}
```

```java
import top.mrxiaom.qrcode.QRCode;
import top.mrxiaom.qrcode.enums.ErrorCorrectionLevel;
import top.mrxiaom.qrcode.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Example {
    public static void main(String[] args) throws IOException {
        String data = "Ciallo～(∠・ω< )⌒★";
        QRCode code = QRCode.create(data, ErrorCorrectionLevel.H);
        // 打印到终端
        int length = code.getModuleCount();
        for (int row = 0; row < length; row++) {
            for (int col = 0; col < length; col++) {
                if (code.isDark(row, col)) {
                    System.out.print("\33[47m　 \33[0m");
                } else {
                    System.out.print("　 ");
                }
            }
            System.out.println();
        }
        // 输出图片文件 (调用 awt)
        BufferedImage image = ImageUtil.createImage(code, 2, 6);
        File file = new File("run/ciallo.png");
        createParentDir(file);
        ImageIO.write(image, "png", file);
    }

    @SuppressWarnings("UnusedReturnValue")
    private static boolean createParentDir(File file) {
        return file.getParentFile().mkdirs();
    }
}
```
