package space.kuikui.service;

import space.kuikui.pojo.Article;
import space.kuikui.pojo.ArticleLike;

import java.util.List;
import java.util.Set;

/**
 * @author kuikui
 * @date 2025/3/13 0:15
 */
public interface ArticleLikeService {
    /**
     * 点赞文章
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void likeArticle(String articleId, String userId);

    /**
     * 取消点赞文章
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void deleteLike(String articleId, String userId);

    /**
     * 获取文章的点赞数
     * @param articleId 文章ID
     * @return 点赞数
     */
    long getArticleLikeCount(String articleId);

    /**
     * 获取文章的点赞用户列表
     * @param articleId 文章ID
     * @return 点赞用户列表
     */
    Set<String> getArticleLikeUsers(String articleId);

    /**
     * 判断用户是否点赞了文章
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    boolean hasUserLikedArticle(String articleId, String userId);

    void saveBatch(List<ArticleLike> articleLikes);
}
