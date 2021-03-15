package com.example

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)


//fun main2() {
//    embeddedServer(Netty, port = 8000) {
//        routing {
//            get ("/") {
//                call.respondText("Hello, world!")
//            }
//        }
//    }.start(wait = true)
//}
