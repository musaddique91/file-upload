package com.hrblizz.fileapi.dto

import org.springframework.hateoas.RepresentationModel

data class FileUploadDTO(
    val token: String
) : RepresentationModel<FileUploadDTO>()
