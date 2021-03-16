package com.example

import io.ktor.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module(){
    simple()
    html()
    json()
    webapp()
    hocon()
}