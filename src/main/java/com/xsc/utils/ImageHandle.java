package com.xsc.utils;

import com.xsc.exception.SystemException;
import com.xsc.protocol.Code;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageHandle {
    private ImageHandle() {
    }

    public static byte[] imageCompression(byte[] imageByteArray) {
        InputStream inFile = new ByteArrayInputStream(imageByteArray);
        // 计算大小，小于 2M 则返回
        if ((imageByteArray.length * 1.0) / 1024 / 1024 <= 2) {
            return imageByteArray;
        }
        try {
            // 临时存储
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(inFile).scale(0.7).toOutputStream(outputStream);
            return imageCompression(outputStream.toByteArray());
        } catch (Exception e) {
            throw new SystemException(Code.SYSTEM_ERR, "图片上传时数据丢失，请到售卖管理中重新上传", e);
        }
    }

    /**
     * JAVA给图片加水印
     *
     * @param imageByteArray 要添加水印的图片字节数组
     */
    public static byte[] addSoldOutWatermark(byte[] imageByteArray) {
        try { // 处理图片，转输入流
            InputStream inFile = new ByteArrayInputStream(imageByteArray);
            //加载水印文件
            File watFile = new File(ProjectPath.projectPath() + "\\src\\main\\resources\\static\\image\\watermark.png");
            //加载到BufferedImage中
            BufferedImage originalImage = ImageIO.read(inFile);
            BufferedImage waterImage = ImageIO.read(watFile);
            //调用Thumbnails库可参考（https://github.com/coobird/thumbnailator/wiki/Examples）
            BufferedImage thumbnail = Thumbnails.of(originalImage)
                    //必须设置大小，否则有size not set的ERROR
                    .size(600, 400)
                    //var3表示透明度
                    .watermark(Positions.CENTER, waterImage, 1.0f)
                    //缓存输出
                    .asBufferedImage();
            //将BufferedImage转换为byte[]返回
            return bufferedImageToBytes(thumbnail);
        } catch (IOException e) {
            throw new SystemException(Code.SYSTEM_ERR, "图片处理错误", e);
        }
    }

    /**
     * JAVA给图片加水印：将BufferedImage转换为byte[]
     *
     */
    private static byte[] bufferedImageToBytes(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(os.toByteArray());
            byte[] bytes = Convert.inputStreamConvertByteArray(inputStream);
            //清理缓存
            IOUtils.closeQuietly(inputStream);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
