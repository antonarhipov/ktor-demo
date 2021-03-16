package com.example

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.a
import kotlinx.html.body

@KtorExperimentalLocationsAPI
@Location("/")
class index()

@KtorExperimentalLocationsAPI
@Location("/employee/{id}")
class employee(val id: String)

@KtorExperimentalLocationsAPI
fun Application.locations() {
    install(Locations)
    routing {
        get<index> {
            call.respondText("Routing Demo")
        }
        get<employee> { employee ->
//            call.respondText(employee.id)

            call.respondHtml {
                body {
                    a(locations.href(employee("Anton")))
                }
            }
        }
    }
}