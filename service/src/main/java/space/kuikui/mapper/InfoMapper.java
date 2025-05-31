package space.kuikui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import space.kuikui.pojo.Info;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface InfoMapper extends BaseMapper<Info> {

    @Select("SELECT info.*, user.headsrc,user.nickname FROM info JOIN user ON info.userid = user.id ORDER BY info.time desc LIMIT 10")
    List<Map<String, Object>> selectInfo();
    @Select("SELECT info.*, user.headsrc,user.nickname FROM info JOIN user ON info.userid = user.id  ORDER BY info.time desc")
    List<Map<String, Object>> selectInfoAll();
    @Delete("DELETE FROM info ")
    boolean delInfo();
    @Select("select count(*) from info")
    public int count();
}
