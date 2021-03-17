
package com.example

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*

//See https://ktor.io/docs/serving-static-content.html
fun Application.static() {
    routing {
        static("/static") {
            resources("static")
        }
    }
}

