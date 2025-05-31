package space.kuikui.service.impl;

import space.kuikui.mapper.ArticleMapper;
import space.kuikui.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.kuikui.service.ArticleService;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.*;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public int updateLickCount(int lick_count, int id) {
        return articleMapper.updateLikeCount(id,lick_count);
    }
    @Override
    public int updateLickCount2(int lick_count, int id) {
        return articleMapper.updateLikeCount2(id,lick_count);
    }
    @Override
    public List<Article> findAll() {
        return articleMapper.selectList(null);
    }

    //查询总文章数目
    @Override
    public int count() {
        return articleMapper.count();
    }
    @Override
    public int insert(Article article){
        return articleMapper.insert(article);
    }

    /**
     * @param pagenow
     * @param pagesize
     * @param type
     * @param article_id
     * @param article_title
     * @param article_tag
     * @param article_authorid
     * @param article_type
     * @return type : 0 全查
     * 1 文章id
     * 2 文章标题
     * 3 文章标签
     * 4 作者id
     * 5 文章类型 ： 1 前端 2 后端 3 测试 4 运维 5 算法 6 数据库 7 工具 8 其他
     */
    @Override
    public PageInfo<Article> findAll(int pagenow, int pagesize, int type, int article_id, String article_title, String article_tag, int article_authorid, int article_type, int article_status){
        PageHelper.startPage(pagenow,pagesize);
        List<Article> articles = null;
        if (type==0){
            if(article_status==10){
                articles = articleMapper.selectAll2();
            }else{
                articles = articleMapper.selectAll(1);
            }
        }else if (type==1){
            articles = articleMapper.selectAllByArticleId(article_id);
        }else if (type==2){
            articles = articleMapper.selectAllByTitle(article_title);
        }else if (type==3){
            article_tag ="%"+ article_tag+"%";
            articles = articleMapper.selectAllByTag(article_tag);
        }else if (type==4){
            articles = articleMapper.selectAllByAuthId(article_authorid);
        }else if (type==5){
            if (article_type==0){
                articles = articleMapper.selectAll2();
            }else{
                articles = articleMapper.selectAllByType(article_type);
            }

        }

        PageInfo<Article> pageInfo = new PageInfo<>(articles);
        return pageInfo;
    }

    @Override
    public int delOne(int article_id){
        return articleMapper.delOne(article_id);
    }
    @Override
    public int delByIDArr(String[] idArr){
        Collection<Integer> idCollection = Arrays.stream(idArr)
                .map(Integer::parseInt)
                .collect(java.util.stream.Collectors.toList());
        return articleMapper.deleteBatchIds(idCollection);
    }
    @Override
    public List<Article> findByID(int id) {
        return articleMapper.selectAllByArticleId(id);
    }

    @Override
    public int updateStatus(int article_status, int article_id) {
        return articleMapper.updateStatus(article_status, article_id);
    }

    @Override
    public PageInfo<Article> getArticlesByCondition(int pagenow, int pagesize,String keyword) {
        PageHelper.startPage(pagenow,pagesize);
        List<Article> articles = null;
        articles = articleMapper.getArticlesByCondition(keyword);
        PageInfo<Article> pageInfo = new PageInfo<>(articles);
        return pageInfo;
    }

    @Override
    public int updateByCondition(Article article) {
        return  articleMapper.updateArticleExceptCover(article.getArticle_title(),article.getArticle_tag(),article.getArticle_content(),article.getLast_time(),article.getArticle_type(),article.getArticle_id());
    }

    @Override
    public int updateTop(int top, int id) {
        return articleMapper.updateTop(top, id);
    }

    @Override
    public List<Article> selectTop() {
        return articleMapper.selectTop();
    }

    @Override
    public List<Integer> getEcharts() {
        List<Integer> list = new ArrayList<>();
        list.add(articleMapper.selectOne());
        list.add(articleMapper.selectTwo());
        list.add(articleMapper.selectThree());
        list.add(articleMapper.selectFour());
        list.add(articleMapper.selectFive());
        list.add(articleMapper.selectSix());
        list.add(articleMapper.selectSeven());
        return list;
    }
}
