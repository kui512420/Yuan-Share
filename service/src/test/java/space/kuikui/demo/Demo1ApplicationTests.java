package space.kuikui.demo;


import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisListResult;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.task.AsyncTaskListParam;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

@SpringBootTest
class Demo1ApplicationTests {
    public static void basicCall() throws ApiException, NoApiKeyException {
        String prompt = "可爱的妹妹";
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey("sk-bc75bb1f077a4de5b401c8206b03ccb8")
                        .model("wanx2.1-t2i-turbo")
                        .prompt(prompt)
                        .n(1)
                        .size("1024*1024")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        System.out.println( result.getOutput().getResults().get(0).get("url"));
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

    public static void main(String[] args){
        try{
            basicCall();
            //listTask();
        }catch(ApiException|NoApiKeyException e){
            System.out.println(e.getMessage());
        }
    }

}
