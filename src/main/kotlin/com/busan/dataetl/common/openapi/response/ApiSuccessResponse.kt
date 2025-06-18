package com.busan.dataetl.common.openapi.response

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.core.annotation.AliasFor

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(description = "OK", content = [Content(mediaType = "application/json")])
annotation class ApiSuccessResponse(
    @get:AliasFor(annotation = ApiResponse::class, attribute = "responseCode")
    val responseCode: String = "200",

    @get:AliasFor(annotation = ApiResponse::class)
    val content: Array<Content> = []
)