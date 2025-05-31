package space.kuikui.utils;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisListResult;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.task.AsyncTaskListParam;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.kuikui.mapper.SignMapper;
import space.kuikui.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kuikui
 * @date 2025/3/10 22:16
 */
@Component
public class AiUtil {

    @Value("${ai.key}")
    private static String key ;


    public   ImageSynthesisResult  basicCall(String prompt,Integer id,String size) throws ApiException, NoApiKeyException {
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(key)
                        .model("wanx2.1-t2i-turbo")
                        .prompt(prompt)
                        .n(1)
                        .size(size)
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    public static void listTask() throws ApiException, NoApiKeyException {
        ImageSynthesis is = new ImageSynthesis();
        AsyncTaskListParam param = AsyncTaskListParam.builder().build();
        ImageSynthesisListResult result = is.list(param);
        System.out.println(result);
    }

    public void fetchTask() throws ApiException, NoApiKeyException {
        String taskId = "your task id";
        ImageSynthesis is = new ImageSynthesis();
        // If set DASHSCOPE_API_KEY environment variable, apiKey can null.
        ImageSynthesisResult result = is.fetch(taskId, null);
        System.out.println(result.getOutput());
        System.out.println(result.getUsage());
    }


}
