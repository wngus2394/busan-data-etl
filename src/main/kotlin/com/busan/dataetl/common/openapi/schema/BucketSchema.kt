package com.busan.dataetl.common.openapi.schema

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import io.swagger.v3.oas.annotations.media.Schema

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Schema(description = "Storage 버킷명", example = "busan-ai")
@JacksonAnnotationsInside
annotation class BucketSchema()
