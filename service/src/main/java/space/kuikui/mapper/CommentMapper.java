package space.kuikui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import space.kuikui.pojo.Comment;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT c.*, u.nickname, u.headsrc " +
            "FROM comment c " +
            "JOIN user u ON c.user_id = u.id " +
            "WHERE c.article_id = #{article_id} order by c.comment_date desc")
    List<Map<String, Object>> getCommentsWithUserInfo(Integer article_id);
}
