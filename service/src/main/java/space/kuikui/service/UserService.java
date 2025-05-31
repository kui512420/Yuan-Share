package space.kuikui.service;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import space.kuikui.pojo.User;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface UserService {
    public User queryByUsername(String username);
    //查询所用用户信息
    public List<User> queryUser();
    //插入用户
    public Boolean insertUser(User user);
    //通过id来删除用户
    public int deleteById(Serializable id);
    //判断用户输入的账号和密码是否正确
    public User verifyLogin(String username,String password);
    public boolean updateLoginTime(String username, Timestamp timestamp);
    //通过id查询用户
    public User queryById(Integer id);
    //查询最新注册用户
    public Map<String,String> queryRes();
    //查询用户币的数量
    public Integer queryBean(Integer id);
    //更新币数
    public boolean updateBean(Integer id, int bean);
    //查询条件用户
    public PageInfo<User> findAll(String orderBy,int pageNow, int pageSize);
    //删除单个用户
    public Integer delByID(Integer id);
    //查单个用户
    public User findOne(Integer id);
    //删除多个用户
    public int delByIDArr(String[] idArr);
    //更新用户状态
    public boolean updateStatus(int id,String username ,Integer status);
    //更新用户角色
    public boolean updateType(int id, Integer type);
    //查询类型用户
    public PageInfo<User>  findType(Integer type,int pageNow, int pageSize);
    //修改部分用户信息
    boolean updateInfo(int id, String username,Integer type, Integer status, String password, String nickname);
    //统计数量
    public int count();
    //获取用户状态
    public int getStatus(String username);
    //修改头像路径
    public int updateHeadsrc(int id,String headsrc);
    //查询用户是否存在
    public boolean isExist(String username);
}
