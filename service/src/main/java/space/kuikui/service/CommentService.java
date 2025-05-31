package space.kuikui.service;


import org.springframework.beans.factory.annotation.Autowired;
import space.kuikui.pojo.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {
   public int addComment(Comment comment);
   public List<Map<String, Object>> getComments(Integer article_id);
}
