
package com.example

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*


fun Application.static() {
    routing {
        static("/static") {
            resources("static")
        }
    }
}

