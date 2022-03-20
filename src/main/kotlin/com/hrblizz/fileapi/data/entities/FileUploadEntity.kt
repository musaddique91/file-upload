package com.hrblizz.fileapi.data.entities

import com.hrblizz.fileapi.dto.FileMetaDTO
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

@Document(value = "file_upload")
class FileUploadEntity(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val originalUploadedFileName: String?,
    val contentType: String,
    val meta: FileMetaDTO?,
    val source: String?,
    val expireTime: LocalDateTime?,
    val storagePath: String,
    val createdDate: LocalDateTime = LocalDateTime.now()
)
