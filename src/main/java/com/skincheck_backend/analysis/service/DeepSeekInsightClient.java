package com.skincheck_backend.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DeepSeekInsightClient {

    private final ObjectMapper objectMapper;

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.model}")
    private String model;

    private static final MediaType JSON =
            MediaType.parse("application/json");

    public DeepSeekInsightClient() {
        this.objectMapper = new ObjectMapper();
    }

    public String generateInsight(String prompt) {
        try {
            OkHttpClient client = new OkHttpClient();

            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "너는 피부 관리 앱의 AI 인사이트 생성기야."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "temperature", 0.6
            );

            Request request = new Request.Builder()
                    .url("https://api.deepseek.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(
                            objectMapper.writeValueAsString(body),
                            JSON
                    ))
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }

            Map<?, ?> json = objectMapper.readValue(
                    response.body().string(),
                    Map.class
            );

            Map<?, ?> choice =
                    (Map<?, ?>) ((List<?>) json.get("choices")).get(0);

            Map<?, ?> message =
                    (Map<?, ?>) choice.get("message");

            return (String) message.get("content");

        } catch (Exception e) {
            return null;
        }
    }
}
