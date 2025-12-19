package com.msa.user.adapter.in.web;

import com.msa.user.application.port.out.LoadMapDataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user") // 🚨 Gateway에서 설정한 /user 경로와 매핑
@RequiredArgsConstructor
public class MapController {

    private final LoadMapDataPort loadMapDataPort;

    /**
     * JS에서 호출하는 경로: GET /user/dog-parks?location=...
     */
    @GetMapping("/dog-parks")
    public ResponseEntity<Map<String, Object>> getDogParks(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "keyword", required = false) String keyword) {

        // 포트(Port)를 통해 어댑터(Adapter) 호출
        Map<String, Object> result = loadMapDataPort.loadNearbyParks(location, keyword);

        return ResponseEntity.ok(result);
    }
}