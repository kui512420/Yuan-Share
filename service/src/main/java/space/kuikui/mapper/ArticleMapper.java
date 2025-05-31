package space.kuikui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.*;
import space.kuikui.pojo.Article;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    //统计数量
    @Select("select count(*) from article")
    public int count();

    //更新点赞数量
    @Select("UPDATE article SET like_count = like_count + #{likeCount} WHERE article_id = #{id}")
    public int updateLikeCount(@Param("likeCount") Integer likeCount,@Param("id") Integer id);

    //更新点赞数量-
    @Update("UPDATE article SET like_count = like_count - #{likeCount} WHERE article_id = #{articleId}")
    int updateLikeCount2(@Param("articleId") int articleId, @Param("likeCount") int likeCount);

    //查询所有文章
    @Select("select art.*,u.nickname from article art,user u where art.article_status=#{status} and art.article_authorid=u.id order by publish_time desc")
    public List<Article> selectAll(int status);

    @Select("select art.*,u.nickname from article art,user u where art.article_authorid=u.id order by publish_time desc")
    public List<Article> selectAll2();

    //查询类型文章
    @Select("select * from article where article_type=#{type} order by publish_time desc")
    public List<Article> selectAllByType(int type);

    //查询id文章
    @Select("select * from article where article_id=#{article_id} order by publish_time desc")
    public List<Article> selectAllByArticleId(int article_id);

    //查询作者文章
    @Select("select * from article where article_authorid=#{authId} order by publish_time desc")
    public List<Article> selectAllByAuthId(int authId);

    //查询标题文章
    @Select("select * from article where article_title=#{title} order by publish_time desc")
    public List<Article> selectAllByTitle(String title);

    //查询标签文章
    @Select("select * from article where article_tag LIKE #{tag} order by publish_time desc")
    public List<Article> selectAllByTag(String tag);

    //删除单个文章
    @Delete("delete from article where article_id=#{article_id}")
    public int delOne(int article_id);

    //改文章状态
    @Update("update article set article_status=#{article_status} where article_id=#{article_id}")
    int updateStatus(@Param("article_status") int article_status, @Param("article_id") int article_id);

    //模糊查询
    @Select("SELECT * FROM article WHERE article_title LIKE CONCAT('%', #{keyword}, '%') OR JSON_CONTAINS(article_tag, CONCAT('\"', #{keyword}, '\"'))")
    List<Article> getArticlesByCondition(String keyword);

    //更新文章
    @Update("UPDATE article " +
            "SET article_title = #{article_title}, " +
            "    article_tag = #{article_tag}, " +
            "    article_content = #{article_content}, " +
            "    last_time = #{last_time}, " +
            "    article_type = #{article_type} " +
            "WHERE article_id = #{article_id}")
    int updateArticleExceptCover(
            @Param("article_title") String article_title,
            @Param("article_tag") String article_tag,
            @Param("article_content") String article_content,
            @Param("last_time") java.sql.Timestamp last_time,
            @Param("article_type") Integer article_type,
            @Param("article_id") Integer article_id
    );
    //改文章置顶
    @Update("update article set top=#{top} where article_id=#{id}")
    int updateTop(int top, int id);
    //查置顶文章
    @Select("select * from article where top=1")
    List<Article> selectTop();
    //查置天
    @Select("select count(*) from article where DATE(publish_time)=CURDATE()-INTERVAL 6 DAY")
    int selectOne();
    //查置天
    @Select("select count(*) from article where DATE(publish_time)=CURDATE()-INTERVAL 5 DAY")
    int selectTwo();
    //查置天
    @Select("select count(*) from article where DATE(publish_time)=CURDATE()-INTERVAL 4 DAY")
    int selectThree();
    //查置天
    @Select("select count(*) from article where DATE(publish_time)=CURDATE()-INTERVAL 3 DAY")
    int selectFour();
    //查置天
    @Select("select count(*) from article where DATE(publish_time)=CURDATE()-INTERVAL 2 DAY")
    int selectFive();
    //查置天
    @Select("select count(*) from article where DATE(publish_time)=CURDATE()-INTERVAL 1 DAY")
    int selectSix();
    //查置天
    @Select("select count(*) from article where DATE(publish_time)=CURDATE()")
    int selectSeven();
}
