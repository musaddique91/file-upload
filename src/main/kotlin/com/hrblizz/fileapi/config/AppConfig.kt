package com.hrblizz.fileapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppConfig {
    @Value("\${fileapi.file.storage.basePath}")
    lateinit var fileBaseStoragePath: String
}
