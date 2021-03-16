package com.example

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*

class Customer(val id: Int, val name: String, val email: String)

fun Application.json() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }

//        json()

//        jackson {
//            enable(SerializationFeature.INDENT_OUTPUT)
//        }

    }
    routing {
        get("/customer") {
            val model = Customer(id = 1, name = "Anton Arhipov", email = "anton@arhipov.xyz")
            call.respond(model)
        }
    }

//    routing {
//        get("/json/kotlinx-serialization") {
//            call.respond(mapOf("hello" to "world"))
//        }
//    }
//    routing {
//        get("/json/gson") {
//            call.respond(mapOf("hello" to "world"))
//        }
//    }
//    routing {
//        get("/json/jackson") {
//            call.respond(mapOf("hello" to "world"))
//        }
//    }
}