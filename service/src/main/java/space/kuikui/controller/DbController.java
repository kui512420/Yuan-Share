package space.kuikui.controller;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.var;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/ai")
public class DbController {

    @PostMapping("/db")
    public ResponseEntity<String> send(@RequestParam String txt){
        String apiKey = "e9ca187a-39e5-4eca-a84c-2d586ff67112";
        ArkService service = ArkService.builder().apiKey(apiKey).build();


        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(txt).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("ep-20240821142538-6ndvb")
                .messages(messages)
                .build();

        StringBuilder contentCollector = new StringBuilder();
        service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> contentCollector.append(choice.getMessage().getContent()));
        String collectedContent = contentCollector.toString();
        // shutdown service
        System.out.println(collectedContent);
        service.shutdownExecutor();
        return ResponseEntity.ok(collectedContent);

    }

}
