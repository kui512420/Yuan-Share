package space.kuikui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.kuikui.pojo.Comment;
import space.kuikui.service.CommentService;
import space.kuikui.utils.ResultUtil;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private ResultUtil resultUtil;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addComment(@RequestBody Map<String, String> data) {
        Comment comment = new Comment();
        comment.setUser_id(Integer.parseInt(data.get("user_id")));
        comment.setComment_content(data.get("comment_content"));
        comment.setArticle_id(Integer.parseInt(data.get("article_id")));
        comment.setComment_date(new Timestamp(new Date().getTime()));
        int status = commentService.addComment(comment);
        Map<String, Object> map ;
        if (status == 1) {
            map = resultUtil.getResult(200,"评论成功",resultUtil.DATA_NOW);
        }else{
            map = resultUtil.getResult(500,"评论失败",resultUtil.DATA_NOW);
        }
        return ResponseEntity.ok(map);
    }

    @GetMapping("/getComment")
    public ResponseEntity<Map<String, Object>> getComment(@RequestParam("article_id") int article_id) {
       List<Map<String, Object>> commentList =  commentService.getComments(article_id);
        Map<String, Object> map ;
        map = resultUtil.getResult(200,"获取评论成功",commentList);
        return ResponseEntity.ok(map);
    }
}
