package space.kuikui.service.impl;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import space.kuikui.mapper.ArticleLikeMapper;
import space.kuikui.pojo.ArticleLike;
import space.kuikui.service.ArticleLikeService;

import java.util.List;
import java.util.Set;

/**
 * @author kuikui
 * @date 2025/3/13 0:16
 */
@Service
public class ArticleLikeServiceImpl implements ArticleLikeService {
    private static final String ARTICLE_LIKE_KEY_PREFIX = "article:like:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Override
    public void likeArticle(String articleId, String userId) {
        String key = getArticleLikeKey(articleId);
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key, userId, System.currentTimeMillis());
    }

    @Override
    public void deleteLike(String articleId, String userId) {
        String key = getArticleLikeKey(articleId);
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.remove(key, userId);
        articleLikeMapper.deleteLike(Integer.parseInt(articleId), Integer.parseInt(userId));
    }

    @Override
    public long getArticleLikeCount(String articleId) {
        String key = getArticleLikeKey(articleId);
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.zCard(key);
    }

    @Override
    public Set<String> getArticleLikeUsers(String articleId) {
        String key = getArticleLikeKey(articleId);
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.range(key, 0, -1);
    }

    @Override
    public boolean hasUserLikedArticle(String articleId, String userId) {
        //先去redis里查
        String key = getArticleLikeKey(articleId);
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        boolean hasLiked  = zSetOperations.score(key, userId) != null;
        if(hasLiked){
            return hasLiked;
        }else{
            //如果redis里面没有在去数据库中查询
            List<ArticleLike> articleLike = articleLikeMapper.hasLiked(Integer.parseInt(articleId),Integer.parseInt(userId));
            if(articleLike.size()>0){
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    public void saveBatch(List<ArticleLike> articleLikes) {
        articleLikeMapper.insertBatch(articleLikes);
    }

    private String getArticleLikeKey(String articleId) {
        return ARTICLE_LIKE_KEY_PREFIX + articleId;
    }
}
