package space.kuikui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import space.kuikui.pojo.Notice;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    @Select("select count(*) from notice")
    public int count();
}
