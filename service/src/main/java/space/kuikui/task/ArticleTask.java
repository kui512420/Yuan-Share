package space.kuikui.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.kuikui.pojo.ArticleLike;
import space.kuikui.service.ArticleLikeService;
import space.kuikui.service.ArticleService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author kuikui
 * @date 2025/3/13 1:15
 */
@Component
public class ArticleTask {
    private static final String ARTICLE_LIKE_KEY_PREFIX = "article:like:";
    private static final Logger logger = LoggerFactory.getLogger(ArticleTask.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ArticleLikeService articleLikeService;
    @Autowired
    private ArticleService articleService;

    @Scheduled(fixedRate = 15000)
    public void executeTask() {
        try {
            Set<String> keys = redisTemplate.keys(ARTICLE_LIKE_KEY_PREFIX + "*");
            if (keys != null) {
                for (String key : keys) {
                    String articleId = key.substring(ARTICLE_LIKE_KEY_PREFIX.length());
                    try {
                        // 检查 articleId 是否为有效的整数
                        Integer.parseInt(articleId);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid articleId: {}", articleId, e);
                        continue;
                    }
                    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
                    Set<ZSetOperations.TypedTuple<String>> tuples = zSetOperations.rangeWithScores(key, 0, -1);
                    if (tuples != null) {
                        List<ArticleLike> articleLikes = new ArrayList<>();
                        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                            String userId = tuple.getValue();
                            Long likeTime = tuple.getScore().longValue();
                            ArticleLike articleLike = new ArticleLike();
                            articleLike.setArticleId(articleId);
                            articleLike.setUserId(userId);
                            LocalDateTime likeDateTime = Instant.ofEpochMilli(likeTime)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();
                            articleLike.setLikeTime(likeDateTime);
                            articleLikes.add(articleLike);
                        }
                        // 批量插入数据
                        try {
                            articleLikeService.saveBatch(articleLikes);
                        } catch (Exception e) {
                            logger.error("Failed to save article likes for articleId: {}", articleId, e);
                            continue;
                        }
                        // 删除 Redis 中的数据
                        boolean isDeleted = redisTemplate.delete(key);
                        if (!isDeleted) {
                            logger.error("Failed to delete Redis key: {}", key);
                        }
                        // 更新文章的点赞数量
                        int likeCount = articleLikes.size();
                        try {
                            articleService.updateLickCount(Integer.parseInt(articleId), likeCount);
                        } catch (Exception e) {
                            logger.error("Failed to update like count for articleId: {}", articleId, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error executing article task", e);
        }
    }
}