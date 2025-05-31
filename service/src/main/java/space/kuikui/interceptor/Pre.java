package space.kuikui.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import space.kuikui.service.UserService;
import space.kuikui.utils.JwtLoginUtil;
import space.kuikui.utils.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Pre implements HandlerInterceptor {
    //开放
    private static final List<String> EXCLUDED_URLS = Arrays.asList(
            "/api/user/register", "/api/getCount", "/api/article/findOne",
            "/api/articlecover", "/api/article/getAll", "/api/articlecontent",
            "/api/users/getLoginLogs", "/api/user/login", "/api/code", "/api/userheader",
            "/api/article/like","/api/users/updateInfo2","/ai/db","/api/comment/getComment",
            "/api/info/get","/api/goodsImg","/api/goods/getGoodsByStatus","/api/article/getTop",
            "/api/ai","/api/email","/api/ComImg"
    );
    //权限
    private static final List<String> RESTRICTED_PATHS = Arrays.asList(
            "/api/users/updateInfo", "/api/users/updateStatus", "/api/users/delArr",
            "/api/users/del", "/api/users/findType", "/api/users/queryByUsername",
            "/api/users/queryById", "/api/users/findOne",
            "/api/article/getAll",
            "/api/article/delArr", "/api/article/updateStatus","/api/goods/updateStatus",
            "/api/goods/delByID","/api/users/export"
    );

    private final ResultUtil resultUtil = new ResultUtil();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getHeader("token");
        String requestURI = request.getRequestURI();

        // 放行排除路径
        if (EXCLUDED_URLS.stream().anyMatch(requestURI::startsWith)) {
            return true;
        }

        // 检查 token 是否为空
        if (token == null || token.isEmpty()) {
            sendErrorResponse(response, 205, "请登录后重新尝试");
            return false;
        }

        try {
            // 解析 token
            Map<Object, Object> claims = new JwtLoginUtil().jwtPe(token);
            String username = (String) claims.get("username");
            int type = (int) claims.get("type");

            // 权限校验
            if (type == 1 && RESTRICTED_PATHS.stream().anyMatch(requestURI::startsWith)) {
                sendErrorResponse(response, 206, "您无权限访问");
                return false;
            }

            // 检查 Redis 中的 token
            String redisToken = redisTemplate.opsForValue().get(username + "-token");
            if (redisToken == null) {
                int id = (int) claims.get("id");
                userService.updateStatus(id, username, 0);
                sendErrorResponse(response, 204, "账号被停用，请联系管理员");
                return false;
            }

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            sendErrorResponse(response, 207, "身份信息过期，请重新登录");
            return false;
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Token 解析异常，请稍后重试");
            return false;
        }

        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter pw = response.getWriter()) {
            String json = objectMapper.writeValueAsString(resultUtil.getResult(code, message, resultUtil.DATA_NOW));
            pw.write(json);
        }
    }
}