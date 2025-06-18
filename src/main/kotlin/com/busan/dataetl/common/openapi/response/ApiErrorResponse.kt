package com.busan.dataetl.common.openapi.response

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import com.busan.dataetl.common.dto.Error

@ApiResponses(
    value = [ApiResponse(
        responseCode = "400", description = "BAD REQUEST", content = [Content(
            mediaType = "application/json", schema = Schema(implementation = Error::class)
        )]
    ), ApiResponse(
        responseCode = "500", description = "INTERNAL SERVER ERROR", content = [Content(
            mediaType = "application/json", schema = Schema(implementation = Error::class)
        )]
    )]
)
annotation class ApiErrorResponse
