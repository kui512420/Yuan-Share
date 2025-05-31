package space.kuikui.utils;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
@Component
public class ResultUtil {

    public  final long DATA_NOW = new Date().getTime();

    public Map<String,Object> getResult(Integer code,String message,Object data){
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code",code);
        response.put("msg",message);
        response.put("data",data);
        return response;
    }
}
