package com.example

import io.ktor.application.*
import io.ktor.config.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

operator fun ApplicationConfig.get(key: String) =
    this.propertyOrNull(key)?.getString()

private val log: Logger = LoggerFactory.getLogger("com.example.hocon")

fun Application.hocon(){
    val custom = environment.config.config("ktor.custom")
    val key1 = custom["key1"]
    val key2 = custom["key2"]

    log.info(key1)
    log.info(key2)
}