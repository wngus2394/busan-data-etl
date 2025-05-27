package com.busan.dataetl.service.home

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 서버 확인 API
 */
@RestController
class HomeController {

    /**
     * Ping 확인
     * API Gateway에서 서버가 정상적으로 동작하는지 확인하기 위해 PING을 보내는 경로이이다.
     */
    @GetMapping("/health")
    fun ping(): String {
        return "success"
    }
}