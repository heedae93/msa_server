package com.msa.user.adapter.out.external;

// ... (기존 import 생략) ...

import com.msa.user.application.port.out.LoadMapDataPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class MapExternalAdapter implements LoadMapDataPort {

    @Value("${external.map.kakao-rest-api-key}")
    private String kakaoRestApiKey;

    @Value("${external.map.kakao-search-url}")
    private String kakaoSearchUrl;

    private final WebClient webClient = WebClient.create();
    private static final String DEFAULT_KEYWORD = "강아지 운동장";

    @Override
    public Map<String, Object>  loadNearbyParks(String location, String keyword) {
        // 카카오는 위경도 파라미터로 x(경도), y(위도)를 받습니다. 필요시 추가하세요.
        URI uri = UriComponentsBuilder
                .fromHttpUrl(kakaoSearchUrl)
                .queryParam("query", DEFAULT_KEYWORD)
                .queryParam("size", 10)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        log.info("Calling Kakao Local API: {}", uri);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = webClient.get()
                    .uri(uri)
                    // 🚨 카카오 필수 헤더 설정: KakaoAK 한 칸 띄우고 키 입력
                    .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("Kakao API error: " + body))
                    )
                    .bodyToMono(Map.class)
                    .block();

            return result;
        } catch (Exception e) {
            log.error("Failed to load map data from Kakao API.", e);
            throw new RuntimeException("카카오 지도 데이터를 불러오는 데 실패했습니다.", e);
        }
    }
}