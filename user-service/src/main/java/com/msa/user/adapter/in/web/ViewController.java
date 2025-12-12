package com.msa.user.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// 🚨 @Controller 사용 (HTML View 반환)
@Controller
public class ViewController {

    @GetMapping("/view/map")
    public String showMapPage(Model model) {
        // [향후 로직 추가] 사용자 정보나 기본 위치 등을 모델에 담아 전달할 수 있습니다.

        // Thymeleaf 템플릿 'map'을 찾아 렌더링합니다.
        return "map";
    }
}