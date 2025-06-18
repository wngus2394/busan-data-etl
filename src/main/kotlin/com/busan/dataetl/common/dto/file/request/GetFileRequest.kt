package com.busan.dataetl.common.dto.file.request

import com.busan.dataetl.common.openapi.schema.BucketSchema
import org.springdoc.core.annotations.ParameterObject

@ParameterObject
data class GetFileRequest(
    @field:BucketSchema
    val bucketName: String
)
