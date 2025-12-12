package com.msa.user.application.port.out;

import java.util.Map;

// 지도 API와 통신하여 데이터를 로드하는 Port
public interface LoadMapDataPort {

    /**
     * Naver API를 호출하여 주변 강아지 운동장 데이터를 검색합니다.
     * @param location 현재 위치 문자열 (예: "37.5665,126.9780")
     * @param keyword 검색 키워드 (예: "강아지 운동장")
     * @return Naver API 응답 JSON 데이터를 파싱한 Map 객체
     */
    Map<String, Object> loadNearbyParks(String location, String keyword);
}