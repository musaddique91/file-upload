package com.hrblizz.fileapi.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.hrblizz.fileapi.dto.FileMetaDTO
import com.hrblizz.fileapi.dto.UploadedFileAttribute
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Component
class FileMapper(private val objectMapper: ObjectMapper) {
    fun mapBean(
        file: MultipartFile,
        name: String,
        contentType: String,
        metaJson: String,
        source: String,
        expireTime: LocalDateTime?
    ): UploadedFileAttribute {
        return UploadedFileAttribute(
            name,
            contentType,
            objectMapper.readValue(metaJson, FileMetaDTO::class.java),
            source,
            expireTime,
            file
        )
    }
}
