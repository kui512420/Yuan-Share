package space.kuikui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.kuikui.mapper.CommentMapper;
import space.kuikui.pojo.Comment;
import space.kuikui.service.CommentService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Override
    public int addComment(Comment comment) {
        return commentMapper.insert(comment);
    }

    @Override
    public List<Map<String, Object>> getComments(Integer article_id) {
        return commentMapper.getCommentsWithUserInfo(article_id);
    }
}
