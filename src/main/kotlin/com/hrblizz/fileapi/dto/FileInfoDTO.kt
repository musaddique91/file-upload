package com.hrblizz.fileapi.dto

import org.springframework.hateoas.RepresentationModel
import java.time.LocalDateTime

data class FileInfoDTO(
    val token: String,
    val filename: String,
    val size: Long,
    val contentType: String,
    val createTime: LocalDateTime?,
    val meta: FileMetaDTO?
) : RepresentationModel<FileInfoDTO>()
