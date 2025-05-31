package space.kuikui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import space.kuikui.pojo.LoginLog;

import java.sql.Timestamp;
import java.util.List;
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
    //添加登录日志
    @Insert("insert into login_log (username, login_time, login_ip, login_agent) values (#{username}, #{login_time}, #{login_ip}, #{login_agent})")
    int addLoginLog(String username, Timestamp login_time, String login_ip, String login_agent);

    @Select("select * from login_log order by login_time desc")
    List<LoginLog> selectAll();
    //通过ip查询登录日志
    @Select("select * from login_log where login_ip=#{login_ip} order by login_time desc")
    List<LoginLog> selectAllByIp(String login_ip);
    //通过ip查询登录日志
    @Select("select * from login_log where username=#{username} order by login_time desc")
    List<LoginLog> selectAllByUsername(String username);
}
