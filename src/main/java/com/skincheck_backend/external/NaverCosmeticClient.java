package com.skincheck_backend.external;

import com.skincheck_backend.external.dto.NaverSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NaverCosmeticClient {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    // ❗ 여기서는 절대 header 넣지 말 것
    private final WebClient webClient =
            WebClient.builder()
                    .baseUrl("https://openapi.naver.com")
                    .build();

    public NaverSearchResponse search(String keyword) {

        // 🔥 디버그 (처음엔 꼭 찍어봐)
        System.out.println("NAVER CLIENT ID = " + clientId);
        System.out.println("NAVER CLIENT SECRET = " + clientSecret);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/shop.json")
                        .queryParam("query", keyword)
                        .queryParam("display", 20)
                        .queryParam("sort", "sim")
                        .build()
                )
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .header("User-Agent", "Mozilla/5.0 (SkinCheck Server)")
                .retrieve()
                .bodyToMono(NaverSearchResponse.class)
                .block();

    }
}