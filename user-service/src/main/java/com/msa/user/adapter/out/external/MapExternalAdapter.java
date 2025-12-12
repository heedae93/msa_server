package com.msa.user.adapter.out.external;

// ... (기존 import 생략) ...

import com.msa.user.application.port.out.LoadMapDataPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
public class MapExternalAdapter implements LoadMapDataPort {

    // 🚨 [변경] Naver Client ID 및 Secret 주입
    @Value("${external.map.naver-client-id}")
    private String naverClientId;

    @Value("${external.map.naver-client-secret}")
    private String naverClientSecret;

    @Value("${external.map.naver-search-url}") // 🚨 지역 검색 API URL
    private String naverSearchUrl;

    private final WebClient webClient = WebClient.create();
    private static final String DEFAULT_KEYWORD = "강아지 운동장"; // 🚨 키워드를 한글로 변경

    @Override
    public Map<String, Object> loadNearbyParks(String location, String keyword) {
        // Naver API는 location 대신 keyword와 query를 사용합니다.
        // 여기서는 위도/경도(location)를 기준으로 검색할 수 없으므로,
        // 키워드와 함께 검색할 쿼리(display=100, start=1)를 구성합니다.
        // 💡 주의: Naver 지역 검색 API는 Geo-location 기반 검색(주변 검색)을 직접 지원하지 않으므로,
        //         '위치' 정보를 '검색 쿼리'로 변환하는 로직(예: location 기반 주소 변환 후 검색)이 필요하나,
        //         일단은 '강아지 운동장' 키워드 검색 결과를 반환합니다.

        String url = String.format("%s?query=%s&display=10", naverSearchUrl, DEFAULT_KEYWORD);

        log.info("Calling Naver Map API: {}", url);

        try {
            // 🚨 [변경] HTTP 헤더를 통해 Client ID/Secret 전달
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header("X-Naver-Client-Id", naverClientId)
                    .header("X-Naver-Client-Secret", naverClientSecret)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response;
        } catch (Exception e) {
            log.error("Failed to load map data from Naver API.", e);
            throw new RuntimeException("지도 데이터를 불러오는 데 실패했습니다.", e);
        }
    }
}