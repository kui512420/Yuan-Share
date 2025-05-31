package space.kuikui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import space.kuikui.pojo.Info;
import space.kuikui.pojo.Sign;

import java.util.List;

@Mapper
public interface  SignMapper  extends BaseMapper<Sign> {
    @Select("select * from sign where DATE(time) = CURDATE() and userid = #{userid}")
    Sign cheak(@Param("userid") Integer userid);

    @Select("select * from sign")
    List<Sign> all();

    @Select("select * from sign where userid = #{userid} and DATE(time) = CURDATE()")
    List<Sign> findByUserid(@Param("userid") String userid);
}
