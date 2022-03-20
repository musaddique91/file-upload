package com.hrblizz.fileapi.data.repository

import com.hrblizz.fileapi.data.entities.FileUploadEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface FileUploadEntityRepository : MongoRepository<FileUploadEntity, String>
