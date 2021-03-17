package com.example

import io.ktor.application.*
import io.ktor.config.*

operator fun ApplicationConfig.get(key: String) =
    this.propertyOrNull(key)?.getString()

//See https://ktor.io/docs/configurations.html#read-configuration-in-code
fun Application.hocon() {
    val custom = environment.config.config("ktor.custom")
    val key1 = custom["key1"]
    val key2 = custom["key2"]

    log.info(key1)
    log.info(key2)
}