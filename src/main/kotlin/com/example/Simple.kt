
package com.example

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*


fun Application.simple() {
    routing {
        get("/") {
            call.respondText("Hello World!", ContentType.Text.Plain)
        }
    }
}