package space.kuikui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import space.kuikui.service.*;
import space.kuikui.service.impl.UserServiceImpl;
import space.kuikui.utils.CompressimgUtil;
import space.kuikui.utils.JwtLoginUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LodingController {



    private static final String ImgUserHeader = "/opt/project/ImgUserHeader/";
    private static final String ImgCover = "/opt/project/ImgCover/";
    private static final String ImgContent = "/opt/project/ImgContent/";
    private static final String ImgGoods = "/opt/project/ImgGoods/";
  private static final String ImgCom = "/opt/project/ImgCom/";
    // static final String ImgUserHeader = "C:\\Users\\30767\\Desktop\\vscode\\imges\\header\\";
   // private static final String ImgCover = "C:\\Users\\30767\\Desktop\\vscode\\imges\\cover\\";
   // private static final String ImgContent = "C:\\Users\\30767\\Desktop\\vscode\\imges\\content\\";
    // private static final String ImgCom = "C:\\Users\\30767\\Desktop\\vscode\\imges\\ImgGoods\\";
    @Autowired
    private UserService userService;
    @Autowired
    private InfoService infoService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private JwtLoginUtil jwtLoginUtil;


    @GetMapping("/getCount")
    public  ResponseEntity<Map<String,Object>>  getCount(
            @RequestHeader(value = "token") String token
    ){
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> detail = new LinkedHashMap<>();

            response.put("code",200);
            response.put("msg","获取成功");
            detail.put("user",userService.count());
            detail.put("info",infoService.count());
            detail.put("article",articleService.count());
            detail.put("goods",goodsService.count());
            response.put("data",detail);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * @param file
     * @param id
     * @param type 0 是 头像， 1 是文章封面 ，2 是文章内容图片
     * @return
     * @throws IOException
     */
    @PostMapping("/upload-url")
    public ResponseEntity<Map<String,Object>> putimg(
            @RequestParam(value = "fileInput") MultipartFile file,
            @RequestParam(value = "id",required = false) Integer id,
            @RequestParam(value = "article_id",required = false) Integer article_id,
            @RequestParam(value = "type") Integer type
    ) throws IOException {

        JwtLoginUtil jwtLoginUtil = new JwtLoginUtil();
        //获取id
        //int id = Integer.parseInt(jwtLoginUtil.jwtPe(token).get("id").toString());
        String path = "";

        String lowerCaseFileName = file.getName().toLowerCase();
        System.out.println(lowerCaseFileName);
        //这里以用户id作为文件名称
        String pathT = "";

        if(type==0){
            pathT = "api/userheader/"+ id;
            path = ImgUserHeader+ id;
        }else if(type==1){
            String datatime = new Date().getTime()+"";
            pathT = "api/articlecover/"+datatime;
            path = ImgCover+ datatime;
        }else if(type==2){
            Date data = new Date();
            String datatime = data.getTime()+"";
            pathT = "api/articlecontent/"+datatime;
            path = ImgContent+ datatime;
        }else if(type==3){
            Date data = new Date();
            String datatime = data.getTime()+"";
            pathT = "api/goodsImg/"+datatime;
            path = ImgGoods+ datatime;
        }else if(type==4){
            Date data = new Date();
            String datatime = data.getTime()+"";
            pathT = "api/ComImg/"+datatime;
            path = ImgCom+ datatime;
        }


        if(file==null){
            path = "";
        }else{
            //服务器存放地址
            //path = "/opt/project/userheader/"+ id+".jpg";

            if(lowerCaseFileName.endsWith(".jpg")){
                pathT+=".jpg";
                path+=".jpg";
            }else if(lowerCaseFileName.endsWith(".png")){
                pathT+=".png";
                path+=".png";
            }else if(lowerCaseFileName.endsWith(".jpeg")){
                pathT+=".jpeg";
                path+=".jpeg";
            }
            CompressimgUtil compressimgUtil = new CompressimgUtil();
            compressimgUtil.compressImage(file,300,300,0.8f,path);
            if(type==0){
                //修改头像路径
                userService.updateHeadsrc(id,pathT);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code",200);
        response.put("msg",pathT);
        response.put("data",new Date().getTime());
        return ResponseEntity.ok(response);
    }



}
