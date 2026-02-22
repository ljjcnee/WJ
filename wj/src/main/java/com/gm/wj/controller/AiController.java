package com.gm.wj.controller;

import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AiController {

    // 👑 把这里换成你在硅基流动官网申请的真实的 API Key！(保留 sk- 前缀)
    private static final String API_KEY = "sk-udrjjqyhymqgvkzvqhgayybcrswzdbopwptblyexqueknsja";

    // 硅基流动的官方聊天接口
    private static final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";

    @PostMapping("/api/ai/chat")
    public Result chatWithAi(@RequestBody Map<String, String> request) {
        // 获取前端学生发来的问题
        String userMessage = request.get("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ResultFactory.buildFailResult("问题不能为空哦~");
        }

        RestTemplate restTemplate = new RestTemplate();

        // 1. 设置请求头 (告诉 AI 我们是谁)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        // 2. 组装请求体 (选用免费、速度极快、智商极高的 Qwen2.5-7B 模型)
        Map<String, Object> body = new HashMap<>();
        body.put("model", "Qwen/Qwen2.5-7B-Instruct");

        // 3. 设定 AI 的“人设”和上下文
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一个部署在大学图书馆智慧学习系统中的 AI 伴读导师。你的任务是解答学生关于图书、专业知识和学习规划的问题。请保持语气友好、专业，回答尽量精简，使用 Markdown 排版。");
        messages.add(systemMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // 4. 发送请求并解析 AI 的回答
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);
            Map<String, Object> resBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) resBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            // 提取出 AI 回答的纯文本内容
            String aiReply = (String) message.get("content");
            return ResultFactory.buildSuccessResult(aiReply);

        } catch (Exception e) {
            System.err.println("AI 接口调用失败: " + e.getMessage());
            return ResultFactory.buildFailResult("AI 导师暂时被外星人抓走啦，请稍后再试！");
        }
    }
}