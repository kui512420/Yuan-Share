package space.kuikui.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import space.kuikui.pojo.User;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select count(*) from user where username=#{username}")
    public int isExist(String username);
    @Select("select * from user ")
    public List<User> all();
    @Select("select count(*) from user")
    public int count();
    @Select("select status from user where username=#{username}")
    public int getStatus(String username);
    @Select("SELECT id,username,email,nickname,headsrc,status,type,register_time,last_login,bean FROM user WHERE id = #{id}")
    User queryById(@Param("id") Integer id);
    @Select("SELECT id,username,email,nickname,headsrc,status,type,register_time,last_login,bean FROM user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);
    @Select("SELECT * FROM user WHERE username = #{username} and password=#{password}")
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    @Update("UPDATE user SET last_login = #{timestamp} where username = #{username}")
    boolean updateLoginTime(@Param("username") String username, @Param("timestamp")Timestamp timestamp);

    @Update("UPDATE user SET headsrc = #{headsrc}  where id =#{id}")
    int updateHeadsrc(@Param("id") int id, @Param("headsrc")String headsrc);
    @Select("SELECT bean FROM user WHERE id = #{id}")
    Integer queryBean(@Param("id") int id);
    //查询最新注册的单个用户
    @Select("SELECT id,username,register_time FROM user order by register_time desc limit 1")
    User queryRes();
    //更新币数
    @Update("UPDATE user SET bean = #{bean} where id = #{id}")
    boolean updateBean(@Param("id") int id, @Param("bean")Integer bean);
    //查询条件用户
    @Select("select id,username,email,nickname,headsrc,status,type,register_time,last_login,bean from user order by #{orderBy} desc")
    List<User> findAll(@Param("orderBy") String orderBy);

    //查询y一个用户
    @Select("select * from user where id = #{id}")
    User findOne(@Param("id") Integer id);
    //删除指定用户
    @Delete("delete from user where id = #{id}")
    int delByID(@Param("id") int id);
    //删除多个指定用户
    @Delete("delete from user where id in (#{idArr})")
    int delByIDArr(@Param("idArr") String idArr);
    //更新用户状态
    @Update("UPDATE user SET status = #{status} where id = #{id}")
    boolean updateStatus(@Param("id") int id, @Param("status")Integer status);
    //更新用户角色
    @Update("UPDATE user SET type = #{type} where id = #{id}")
    boolean updateType(@Param("id") int id, @Param("type")Integer type);
    //查询类型用户
    @Select("select id,username,email,nickname,headsrc,status,type,register_time,last_login,bean from user where type=#{type} order by register_time desc")
    List<User> findType(@Param("type") Integer type);
    //更新用户信息
    @Update("UPDATE user SET type = #{type} , status = #{status} ,password=#{password},nickname=#{nickname}  where id = #{id}")
    boolean updateInfo(@Param("id") int id, @Param("type")Integer type,@Param("status")Integer status,@Param("password")String password,@Param("nickname")String nickname);
}