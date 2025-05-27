package com.busan.dataetl.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.converter.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.charset.Charset

/**
 * MVC 설정
 */
@Configuration
@EnableWebMvc
class MvcConfig : WebMvcConfigurer {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.apply {
            add(ResourceHttpMessageConverter())
            add(StringHttpMessageConverter(Charset.forName("UTF-8")))
            add(ByteArrayHttpMessageConverter())
            add(MappingJackson2HttpMessageConverter())
            add(FormHttpMessageConverter())
        }
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*") // 허용할 도메인 설정
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메소드 설정
            .allowedHeaders("Origin", "Content-Type", "Accept", "X-Container-Port") // 허용할 헤더 설정
            .exposedHeaders(HttpHeaders.CONTENT_DISPOSITION)
            .allowCredentials(true) // 인증정보 허용 여부
            .maxAge(3600); // preflight 요청의 유효시간 설정
    }
}