package space.kuikui.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.web.client.RestTemplate;
import space.kuikui.pojo.User;
import space.kuikui.service.ArticleLikeService;
import space.kuikui.utils.CompressimgUtil;
import space.kuikui.utils.ExportUtil;
import space.kuikui.utils.JwtLoginUtil;
import space.kuikui.pojo.Article;
import space.kuikui.service.impl.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.kuikui.utils.ResultUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    //注入service
    @Autowired
    private ArticleServiceImpl articleService;
    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private ExportUtil exportUtil;
    @Autowired
    private ArticleLikeService articleLikeService;
    @Autowired
    private JwtLoginUtil jwtLoginUtil;

    /**
     * 点赞文章
     *
     * @param token 用户token
     * @return 操作结果
     */
    @PostMapping("/likes")
    public ResponseEntity<Map<String,Object>> likeArticle(@RequestBody Map<String,Object> map, @RequestHeader("token") String token) {
        String userId = jwtLoginUtil.jwtPe(token).get("id")+"";
        boolean hasLiked = articleLikeService.hasUserLikedArticle(map.get("article_id").toString(),userId);
        if(hasLiked){
            articleLikeService.deleteLike(map.get("article_id").toString(), userId);
            articleService.updateLickCount2(Integer.parseInt(map.get("article_id").toString()), 1);
            return ResponseEntity.ok(resultUtil.getResult(200,"取消点赞成功",resultUtil.DATA_NOW));
        }else{
            articleLikeService.likeArticle(map.get("article_id").toString(), userId);
            return ResponseEntity.ok(resultUtil.getResult(200,"点赞成功",resultUtil.DATA_NOW));
        }

    }

    /**
     * 判断用户是否点赞了文章
     * @param articleId 文章ID
     * @param token 用户token
     * @return 是否点赞
     */
    @GetMapping("/{articleId}/like")
    public boolean hasUserLikedArticle(@PathVariable String articleId, @RequestHeader("token") String token) {
        String userId = jwtLoginUtil.jwtPe(token).get("id").toString();
        return articleLikeService.hasUserLikedArticle(articleId, userId);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Article> articles = articleService.findAll();
        exportUtil.export(response,"xxx.xls",articles,Article.class);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(
            @RequestBody Map<String,Object> map,
            @RequestHeader(value = "token") String token) throws IOException {

        Article article = new Article();
        JwtLoginUtil jwtLoginUtil = new JwtLoginUtil();
        //获取发布者的id
        int article_authorid = Integer.parseInt(jwtLoginUtil.jwtPe(token).get("id").toString());
        //获取发布者的username
        String article_author= jwtLoginUtil.jwtPe(token).get("nickname").toString();
        //这里以时间戳作为文件名称
        long date = new Date().getTime();
        //文章封面存放的路径
        //String path = "/opt/project/articlecover/"+ date+".jpg";
        String tag = map.get("article_tag")+"";
        String trimmed = tag.substring(1, tag.length() - 1);
        // 按逗号和可能存在的空格分割字符串
        String[] parts = trimmed.split("\\s*,\\s*");
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < parts.length; i++) {
            // 给每个元素添加单引号
            sb.append("\"").append(parts[i]).append("\"");
            if (i < parts.length - 1) {
                // 添加逗号分隔元素
                sb.append(", ");
            }
        }
        sb.append("]");


        //设置参数
        article.setArticle_authorid(article_authorid);
        article.setArticle_author(article_author);
        article.setArticle_title((String) map.get("article_title"));
        article.setArticle_tag(sb.toString());
        article.setArticle_content((String) map.get("article_content"));
        article.setArticle_cover((String) map.get("article_cover"));
        article.setArticle_status(1);
        article.setArticle_type(Integer.parseInt(map.get("article_type")+""));
        Timestamp timestamp = new Timestamp(new Date().getTime());
        article.setLast_time(timestamp);
        article.setPublish_time(timestamp);
        articleService.insert(article);

        return ResponseEntity.ok(resultUtil.getResult(200,"发布文章成功",resultUtil.DATA_NOW));
    }

    @GetMapping("/getAll")
    public ResponseEntity<Map<String, Object>> getAll(
        @RequestParam(value = "pagenow",defaultValue = "1") int pagnow,
        @RequestParam(value = "pageCount",defaultValue = "10") int pageCount,
        @RequestParam(value = "type",defaultValue = "0") int type,
        @RequestParam(value = "article_id",defaultValue = "0",required = false) int article_id,
        @RequestParam(value = "article_title",required = false) String article_title,
        @RequestParam(value = "article_tag",required = false) String article_tag,
        @RequestParam(value = "article_authorid",defaultValue = "0",required = false) int article_authorid,
        @RequestParam(value = "article_type",defaultValue = "0",required = false) int article_type,
        @RequestParam(value = "article_status",defaultValue = "10",required = false) int article_status
    ){

        PageInfo<Article> articlePageInfo = articleService.findAll(pagnow,pageCount,type,article_id,article_title,article_tag,article_authorid,article_type,article_status);
        return ResponseEntity.ok(resultUtil.getResult(200,"查询成功",articlePageInfo));
    }

    @GetMapping("/delOne")
    public ResponseEntity<Map<String, Object>> delOne(@RequestParam("id") int id,@RequestParam("username") String username,@RequestHeader String token){
        JwtLoginUtil jwtLoginUtil = new JwtLoginUtil();
        if(jwtLoginUtil.jwtPe(token).get("username").equals(username) || jwtLoginUtil.jwtPe(token).get("type").equals(0) ){
            int  data = articleService.delOne(id);
            return ResponseEntity.ok(resultUtil.getResult(200,"删除成功",data));
        }else{
            return ResponseEntity.ok(resultUtil.getResult(206,"您无权限访问",new Date().getTime()));
        }

    }
    @PostMapping("/delArr")
    public ResponseEntity<Map<String, Object>> delAll(
            @RequestParam(value = "idArr") String[] idArr)
    {

        Map<String, Object> response = new LinkedHashMap<>();
        int data =  articleService.delByIDArr(idArr);

        response.put("code", 200);
        response.put("msg","删除成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/updateAll")
    public ResponseEntity<Map<String, Object>> updateAll(
            @RequestBody Map<String,Object> map,
            @RequestHeader String token)
    {
        Map<String, Object> response = new LinkedHashMap<>();
        Article article = new Article();
        JwtLoginUtil jwtLoginUtil = new JwtLoginUtil();
        if(jwtLoginUtil.jwtPe(token).get("username").equals(map.get("authorid").toString()) || jwtLoginUtil.jwtPe(token).get("type").equals(0) ){
            System.out.println(map.get("id"));
            article.setArticle_id(Integer.parseInt(map.get("id").toString()));
            article.setArticle_title(map.get("article_title").toString());
            String tag = map.get("article_tag")+"";
            String trimmed = tag.substring(1, tag.length() - 1);
            // 按逗号和可能存在的空格分割字符串
            String[] parts = trimmed.split("\\s*,\\s*");
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < parts.length; i++) {
                // 给每个元素添加单引号
                sb.append("\"").append(parts[i]).append("\"");
                if (i < parts.length - 1) {
                    // 添加逗号分隔元素
                    sb.append(", ");
                }
            }
            sb.append("]");
            article.setArticle_tag(String.valueOf(sb));
            article.setArticle_content(map.get("article_content").toString());
            article.setArticle_type(Integer.parseInt(map.get("article_type").toString()));
            Timestamp timestamp = new Timestamp(new Date().getTime());
            article.setLast_time(timestamp);
            int data =  articleService.updateByCondition(article);

            response.put("code", 200);
            response.put("msg","更新成功");
            response.put("data",data);
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.ok(resultUtil.getResult(206,"您无权限访问",new Date().getTime()));
        }

    }
    @GetMapping("/findOne")
    public ResponseEntity<Map<String, Object>> findOne(@RequestParam("id") int id){
        Article article = articleService.findByID(id).get(0);
        return ResponseEntity.ok(resultUtil.getResult(200,"获取信息成功",article));
    }
    @GetMapping("/updateStatus")
    public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam("id") int id,@RequestParam("status") int status,@RequestParam("username") String username,@RequestHeader String token){
        JwtLoginUtil jwtLoginUtil = new JwtLoginUtil();
        if(jwtLoginUtil.jwtPe(token).get("username").equals(username) || jwtLoginUtil.jwtPe(token).get("type").equals(0) ){
            int data = articleService.updateStatus(status,id);
            return ResponseEntity.ok(resultUtil.getResult(200,"更新状态成功",data));
        }else{
            return ResponseEntity.ok(resultUtil.getResult(206,"您无权限访问",new Date().getTime()));
        }

    }
    @GetMapping("/updateTop")
    public ResponseEntity<Map<String, Object>> updateTop(@RequestParam("id") int id,@RequestParam("top") int top,@RequestParam("username") String username,@RequestHeader String token){
        JwtLoginUtil jwtLoginUtil = new JwtLoginUtil();
        if(jwtLoginUtil.jwtPe(token).get("username").equals(username) || jwtLoginUtil.jwtPe(token).get("type").equals(0) ){
            int data = articleService.updateTop(top,id);
            return ResponseEntity.ok(resultUtil.getResult(200,"更新状态成功",data));
        }else{
            return ResponseEntity.ok(resultUtil.getResult(206,"您无权限访问",new Date().getTime()));
        }

    }
    @GetMapping("/like")
    public ResponseEntity<Map<String, Object>> like(
            @RequestParam(value = "pagenow",defaultValue = "1") int pagnow,
            @RequestParam(value = "pageCount",defaultValue = "10") int pageCount,
            @RequestParam(value = "txt") String txt
    ){
        PageInfo<Article> articles = articleService.getArticlesByCondition(pagnow,pageCount,txt);
        return ResponseEntity.ok(resultUtil.getResult(200,"查询成功",articles));
    }
    @GetMapping("/getTop")
    public ResponseEntity<Map<String, Object>> getTop(
    ){
        List<Article> articles = articleService.selectTop();
        return ResponseEntity.ok(resultUtil.getResult(200,"查询成功",articles));
    }
    @GetMapping("/getEcharts")
    public ResponseEntity<Map<String, Object>> getEcharts(
    ){
        List<Integer> count = articleService.getEcharts();
        return ResponseEntity.ok(resultUtil.getResult(200,"查询成功",count));
    }
}
