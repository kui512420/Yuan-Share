package space.kuikui.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class CompressimgUtil {

    public static File compressImage(MultipartFile file, int maxWidth, int maxHeight, float quality, String path) throws IOException {
        File compressedImage = new File(path);
        try {
            // 确保文件的父目录存在
            if (!compressedImage.getParentFile().exists()) {
                compressedImage.getParentFile().mkdirs();
            }

            // 将 MultipartFile 写入到指定文件
            file.transferTo(compressedImage);

            // 使用 Thumbnails 对象进行压缩
            Thumbnails.of(compressedImage)
                    .size(maxWidth, maxHeight)
                    .outputQuality(quality)
                    .toFile(compressedImage);
            // 手动释放资源（虽然 Thumbnails 通常会自动处理，但为了保险起见）
            System.gc(); // 触发垃圾回收，可能有助于释放资源
            Thread.sleep(100); // 等待一小段时间，确保资源释放
            return compressedImage;
        } catch (IOException e) {
            // 处理文件操作和压缩过程中可能出现的异常
            throw new IOException("Failed to compress image: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
