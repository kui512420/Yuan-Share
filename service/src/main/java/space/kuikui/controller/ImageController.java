package space.kuikui.controller;


import org.springframework.http.ResponseEntity;


import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;

/*
* 使外部可以正常访问图片
* */
@RestController
@RequestMapping("/api")
public class ImageController {
    //服务器端
    private static final String UPLOAD_DIR = "/opt/project/ImgUserHeader/";
    private static final String UPLOAD_DIR2 = "/opt/project/ImgCover/";
    private static final String UPLOAD_DIR3 = "/opt/project/ImgContent/";
    private static final String UPLOAD_DIR4 = "/opt/project/ImgGoods/";
    private static final String UPLOAD_DIR5 = "/opt/project/ImgCom/";
    //private static final String UPLOAD_DIR = "C:\\Users\\30767\\Desktop\\vscode\\imges\\header\\";
   // private static final String UPLOAD_DIR2 = "C:\\Users\\30767\\Desktop\\vscode\\imges\\cover\\";
    // static final String UPLOAD_DIR3 = "C:\\Users\\30767\\Desktop\\vscode\\imges\\content\\";
    //   private static final String UPLOAD_DIR5 = "C:\\Users\\30767\\Desktop\\vscode\\imges\\ImgGoods\\";
    @GetMapping("/userheader/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
        // 读取文件内容
        byte[] imageBytes = Files.readAllBytes(filePath);
        // 设置Content-Type为image/jpeg
        MediaType mediaType = MediaType.IMAGE_JPEG;
        // 构建响应
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }
    @GetMapping("/articlecover/{filename:.+}")
    public ResponseEntity<byte[]> getImage2(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR2).resolve(filename).normalize();
        // 读取文件内容
        byte[] imageBytes = Files.readAllBytes(filePath);
        // 设置Content-Type为image/jpeg
        MediaType mediaType = MediaType.IMAGE_JPEG;
        // 构建响应
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }
    @GetMapping("/articlecontent/{filename:.+}")
    public ResponseEntity<byte[]> getImage3(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR3).resolve(filename).normalize();
        // 读取文件内容
        byte[] imageBytes = Files.readAllBytes(filePath);
        // 设置Content-Type为image/jpeg
        MediaType mediaType = MediaType.IMAGE_JPEG;
        // 构建响应
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }
    @GetMapping("/goodsImg/{filename:.+}")
    public ResponseEntity<byte[]> getImage4(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR4).resolve(filename).normalize();
        // 读取文件内容
        byte[] imageBytes = Files.readAllBytes(filePath);
        // 设置Content-Type为image/jpeg
        MediaType mediaType = MediaType.IMAGE_JPEG;
        // 构建响应
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }
    @GetMapping("/ComImg/{filename:.+}")
    public ResponseEntity<byte[]> getImage5(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR5).resolve(filename).normalize();
        // 读取文件内容
        byte[] imageBytes = Files.readAllBytes(filePath);
        // 设置Content-Type为image/jpeg
        MediaType mediaType = MediaType.IMAGE_JPEG;
        // 构建响应
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }
    private MediaType getMediaTypeForFileName(String filename) {
        String lowerCaseFileName = filename.toLowerCase();
        if (lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (lowerCaseFileName.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        // 可以根据需要添加更多的图片格式支持
        return MediaType.APPLICATION_OCTET_STREAM; // 默认返回二进制流类型
    }
}

