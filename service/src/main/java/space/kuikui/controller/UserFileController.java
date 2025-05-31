package space.kuikui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.web.bind.annotation.*;
import space.kuikui.pojo.UserFile;
import space.kuikui.pojo.dto.UserFileRequest;
import space.kuikui.service.UserFileService;
import space.kuikui.service.UserService;
import space.kuikui.utils.CompressimgUtil;
import space.kuikui.utils.JwtLoginUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuikui
 * @date 2025/5/14 18:54
 * @description
 */
@RestController
@RequestMapping("/api/userFile")
public class UserFileController {

    @Autowired
    private UserFileService userFileService;
    @Autowired
    private JwtLoginUtil jwtLoginUtil;

    @PostMapping("/addFavoriteImage")
    public boolean addUserFile(@RequestHeader(value = "token") String token,@RequestBody UserFileRequest imageUrl) {
        boolean result = false;
        try {
            Integer id = (Integer) jwtLoginUtil.jwtPe(token).get("id");
            // 是否是在线的图片
            UserFile userFile = new UserFile();
            userFile.setUserId(id);
            userFile.setFileUrl(imageUrl.getUrl());
            result = userFileService.saveUserFile(userFile);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @GetMapping("/getFavoriteImages")
    public List<UserFile> getFavoriteImages(@RequestHeader(value = "token") String token) {
        List<UserFile> files = new ArrayList<>();
        try {
            Integer id = (Integer) jwtLoginUtil.jwtPe(token).get("id");
            // 是否是在线的图片

            files = userFileService.readUserFiles(id);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
    @GetMapping("/del")
    public boolean del(@RequestHeader(value = "token") String token,@RequestParam Integer id) {
        boolean result = false;
        try {
            Integer userId = (Integer) jwtLoginUtil.jwtPe(token).get("id");
            result = userFileService.del(userId,id);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
