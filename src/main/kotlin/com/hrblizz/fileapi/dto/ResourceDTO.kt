package com.hrblizz.fileapi.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.hrblizz.fileapi.constant.FileConstants
import org.springframework.core.io.Resource
import java.time.LocalDateTime

data class ResourceDTO(
    val resource: Resource,
    val filename: String,
    val fileSize: Long,
    val contentType: String,
    @JsonFormat(pattern = FileConstants.DATE_PATTERN)
    val createTime: LocalDateTime?,
)
