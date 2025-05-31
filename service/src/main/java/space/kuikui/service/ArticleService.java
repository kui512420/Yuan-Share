package space.kuikui.service;

import com.github.pagehelper.PageInfo;
import space.kuikui.pojo.Article;

import java.util.List;

public interface ArticleService {
    public int updateLickCount(int lick_count,int id);
    public int updateLickCount2(int lick_count,int id);
    public List<Article> findAll();
    public int count();
    public int insert(Article article);
    public PageInfo<Article> findAll(int pagenow, int pagesize, int type, int article_id, String article_title, String article_tag, int article_authorid, int article_type, int article_status);
    public int delOne(int article_id);
    int delByIDArr(String[] idArr);
    public List<Article> findByID(int id);
    public int updateStatus(int article_status, int article_id);
    public PageInfo<Article> getArticlesByCondition(int pagenow, int pagesize,String keyword);
    public int updateByCondition(Article article);

    int updateTop(int top, int id);
    List<Article> selectTop();

    List<Integer> getEcharts();
}
