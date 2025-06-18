package com.busan.dataetl.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Swagger 설정
 */
@Configuration
class SwaggerConfig {

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI().info(
            Info().title("파일 수집·전처리 API")
                .description(
                    """
                    ## 📋 API 개요
                    Storage에 저장되어 있는 파일을 수집·전처리하는 REST API를 제공합니다.

                    ## 🚀 주요 기능
                    - 파일 리스트 조회
                    - 파일 업로드
                    - 파일 수동 업로드

                    ## ⚠️ 주의사항
                    - 파일 크기 제한: 600MB
                    - 지원 파일 형식: HWP, HWPX, PDF
                    - UTF-8 인코딩 권장
                """.trimIndent()
                )
                .version("v0.0.6-Beta")
        )
    }
}