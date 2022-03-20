package com.hrblizz.fileapi.dto

import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

data class UploadedFileAttribute(
    val name: String,
    val contentType: String,
    val meta: FileMetaDTO,
    val source: String,
    val expireTime: LocalDateTime?,
    val file: MultipartFile
)
