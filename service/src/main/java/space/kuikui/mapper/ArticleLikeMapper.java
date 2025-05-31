package space.kuikui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import space.kuikui.pojo.Article;
import space.kuikui.pojo.ArticleLike;

import java.util.List;

/**
 * @author kuikui
 * @date 2025/3/13 1:22
 */
@Mapper
public interface ArticleLikeMapper  extends BaseMapper<ArticleLike> {
    /**
     * 批量插入文章点赞记录
     * @param articleLikes 文章点赞记录列表
     * @return 插入成功的记录数
     */
    @Insert("<script>" +
            "INSERT INTO articlelike (articleId, userId, likeTime) VALUES " +
            "<foreach collection=\"articleLikes\" item=\"item\" separator=\",\">" +
            "(#{item.articleId}, #{item.userId}, #{item.likeTime})" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("articleLikes") List<ArticleLike> articleLikes);

    @Select("select id from articlelike where articleId=#{articleId} and userId=#{userId}")
    List<ArticleLike> hasLiked(@Param("articleId") int articleId, @Param("userId") int userId);

    @Delete("DELETE FROM articlelike WHERE articleId = #{articleId} AND userId = #{userId}")
    int deleteLike(@Param("articleId") int articleId, @Param("userId") int userId);
}
