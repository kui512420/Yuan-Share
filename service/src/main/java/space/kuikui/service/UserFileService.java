package space.kuikui.service;

import space.kuikui.pojo.UserFile;

import java.util.List;

/**
 * @author kuikui
 * @date 2025/5/14 18:53
 * @description
 */
public interface UserFileService {
    public boolean saveUserFile(UserFile userFile);
    public List<UserFile> readUserFiles(int userId);
    public boolean del(int userId,int fileId);
}
