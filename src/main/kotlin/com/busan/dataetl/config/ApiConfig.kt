package com.busan.dataetl.config

import com.busan.dataetl.common.interceptor.LoggingInterceptor
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.util.TimeValue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit


/**
 * RestTemplate 설정
 */
@Configuration
class ApiConfig {

    /**
     * RestTemplate 클라이언트 설정을 진행하는 Bean 객체
     */
    @Bean
    fun getRestTemplate(): RestTemplate {
        // 커넥션 풀 설정
        val poolingManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setDefaultConnectionConfig(
                ConnectionConfig.custom()
                    .setSocketTimeout(10, TimeUnit.SECONDS) // 읽기시간초과 타임아웃
                    .setConnectTimeout(10, TimeUnit.SECONDS) // 연결시간초과 타임아웃
                    .build()
            )
            .setMaxConnTotal(300) // 커넥션풀적용(최대 오픈되는 커넥션 수)
            .setMaxConnPerRoute(100) // 커넥션풀적용(IP:포트 1쌍에 대해 수행 할 연결 수제한)
            .build()

        // HttpClient 생성
        val httpClient = HttpClientBuilder.create()
            .setConnectionManager(poolingManager)
            .evictIdleConnections(TimeValue.of(30, TimeUnit.SECONDS))
            .build()

        val factory = HttpComponentsClientHttpRequestFactory(httpClient)

        return RestTemplate(BufferingClientHttpRequestFactory(factory)).apply {
            messageConverters.addAll(
                listOf(
                    MappingJackson2HttpMessageConverter(),
                    FormHttpMessageConverter()
                )
            )
            interceptors.add(LoggingInterceptor())
        }
    }
}

