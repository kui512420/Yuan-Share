package space.kuikui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.kuikui.mapper.UserFileMapper;
import space.kuikui.pojo.UserFile;
import space.kuikui.service.UserFileService;

import java.util.Collections;
import java.util.List;

/**
 * @author kuikui
 * @date 2025/5/14 18:53
 * @description
 */
@Service
public class UserFileServiceImpl implements UserFileService {

    @Autowired
    private UserFileMapper userFileMapper;

    @Override
    public boolean saveUserFile(UserFile userFile) {
        return userFileMapper.insert(userFile) > 0;
    }

    @Override
    public List<UserFile> readUserFiles(int userId) {
        QueryWrapper<UserFile> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        return userFileMapper.selectList(qw);
    }

    @Override
    public boolean del(int userId, int fileId) {
        QueryWrapper<UserFile> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.eq("id", fileId);
        return userFileMapper.delete(qw) > 0;
    }
}
