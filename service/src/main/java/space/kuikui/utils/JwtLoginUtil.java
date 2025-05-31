package space.kuikui.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import space.kuikui.pojo.User;
import io.jsonwebtoken.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
@Component
public class JwtLoginUtil {

    private static long time = 1000 * 60 * 60 * 24 * 7 ;
    private static String sign = "512420";
    //加密
    public  String jwtBd(User u) {
        JwtBuilder jwtBuilder = Jwts.builder();
        String token = jwtBuilder
                .setHeaderParam("alg","HS256")
                .setHeaderParam("type","JWT")
                .claim("username",u.getUsername())
                .claim("nickname",u.getNickname())
                .claim("id",u.getId())
                .claim("type",u.getType())
                .claim("headsrc",u.getHeadsrc())
                .setExpiration(new Date(System.currentTimeMillis()+time))
                .signWith(SignatureAlgorithm.HS256,sign)
                .compact();
        return token;
    }
    //解密
    public Map<Object,Object> jwtPe(String token) {
        JwtParser jwtParser = Jwts.parser();
        Jws<Claims> claimsJwts = jwtParser.setSigningKey(sign).parseClaimsJws(token);
        Claims claims = claimsJwts.getBody();
        Map<Object,Object> map = new LinkedHashMap();
        map.put("id", claims.get("id"));
        map.put("username", claims.get("username"));
        map.put("nickname", claims.get("nickname"));
        map.put("type", claims.get("type"));
        map.put("headsrc", claims.get("headsrc"));
        return map;
    }

}
