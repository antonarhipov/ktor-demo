package com.example

import io.ktor.application.*
import io.ktor.locations.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
fun Application.module(testing: Boolean = false) {
    simple() // simple text
    html()  // some HTML and CSS
    freemarker() // Using Freemarker templates
    hocon() // working with custom configuration

    json()  // ContentNegotiation. Serializing response to JSON
    webapp() // Webjars, WebSockets
    features() // different features, mostly headers

    routingApp()
//    structure()

    authentication() // some authentication examples

    locations() // the experimental Locations API

    statusPages() // handling errors

    async() // asynchronous handling
}