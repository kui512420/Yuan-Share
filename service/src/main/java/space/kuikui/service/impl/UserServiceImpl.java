package space.kuikui.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.redis.core.RedisTemplate;
import space.kuikui.mapper.LoginLogMapper;
import space.kuikui.mapper.UserMapper;
import space.kuikui.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.kuikui.service.UserService;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginLogMapper loginLogMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public User queryByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    // 获取所有用户信息
    @Override
    public List<User> queryUser() {
        log.info("进入业务逻辑 queryUser() 方法");
        return userMapper.all();
    }
    //插入用户
    @Override
    public Boolean insertUser(User user) {
        log.info("进入业务逻辑 queryUser() 方法");
        Boolean result = false;
        if(userMapper.insert(user)>=1){
            result = true;
        }
        return result;
    }
    //通过id删除用户信息
    @Override
    public int deleteById(Serializable id){
        return userMapper.deleteById(id);
    }

    //验证账号是否匹配
    @Override
    public User verifyLogin(String username,String password){
        User u1 = userMapper.selectByUsernameAndPassword(username, password);

        return  u1;
    }

    //修改最后登录时间
    @Override
    public boolean updateLoginTime(String username, Timestamp timestamp){

        return userMapper.updateLoginTime(username,timestamp);
    }

    @Override
    public User queryById(Integer id) {
        return userMapper.queryById(id);
    }

    @Override
    public Map<String,String> queryRes() {
        User u = userMapper.queryRes();
        Map<String,String> map = new HashMap<>();
        map.put("id",u.getId()+"");
        map.put("username",u.getUsername());
        map.put("time", String.valueOf(u.getRegister_time()));
        return map;
    }

    @Override
    public Integer queryBean(Integer id) {
        return userMapper.queryBean(id);
    }

    @Override
    public boolean updateBean(Integer id, int bean) {
        return userMapper.updateBean(id,bean);
    }
    @Override
    public PageInfo<User> findAll(String orderBy, int pageNow, int pageSize){
        PageHelper.startPage(pageNow,pageSize);
        List<User> users = userMapper.findAll("register_time");
        PageInfo<User> ArticlePageInfo = new PageInfo<>(users);
        return ArticlePageInfo;
    }
    @Override
    public Integer delByID(Integer id){
        return userMapper.delByID(id);
    }
    @Override
    public User findOne(Integer id){
        return userMapper.findOne(id);
    }

    @Override
    public int delByIDArr(String[] idArr) {
        // (1,2,3)
        Collection<Integer> idCollection = Arrays.stream(idArr)
                .map(Integer::parseInt)
                .collect(java.util.stream.Collectors.toList());
        return userMapper.deleteBatchIds(idCollection);
    }

    //更新用户状态
    public boolean updateStatus(int id,String username, Integer status) {
        if(status==0){
            redisTemplate.delete(username+"-token");
        }
        return userMapper.updateStatus(id,status);
    }

    //更新用户角色
    public boolean updateType(int id, Integer type) {
        return userMapper.updateType(id,type);
    }
    //查询类型用户
    @Override
    public PageInfo<User> findType(Integer type, int pageNow, int pageSize){
        PageHelper.startPage(pageNow,pageSize);
        List<User> users = userMapper.findType(type);
        PageInfo<User> userPageInfo = new PageInfo<>(users);
        return userPageInfo;
    }
    //修改部分用户信息
    @Override
    public boolean updateInfo(int id,String username, Integer type, Integer status, String password,String nickname){
        if(status==0){
            redisTemplate.delete(username+"-token");
        }
        return userMapper.updateInfo(id,type,status,password,nickname);
    }
    @Override
    public int count() {

        return userMapper.count();
    }

    @Override
    public int getStatus(String username) {
        return userMapper.getStatus(username);
    }
    //修改头像路径
    @Override
    public int updateHeadsrc(int id, String headsrc){
        return userMapper.updateHeadsrc(id,headsrc);
    }

    @Override
    public boolean isExist(String username) {
        int count = userMapper.isExist(username);
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
}
