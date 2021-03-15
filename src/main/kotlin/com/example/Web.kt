package com.example

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.webjars.*
import io.ktor.websocket.*
import java.time.Duration

fun Application.webapp(){

    //region Webjars
    install(Webjars) {
        path = "/webjars" //defaults to /webjars
    }

    routing {
        get("/webjars") {
            call.respondText("<script src='/webjars/jquery/jquery.js'></script>", ContentType.Text.Html)
        }
    }
    //endregion

    //region WebSockets
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/") { // websocketSession
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        outgoing.send(Frame.Text("YOU SAID: $text"))
                        if (text.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        }
                    }
                    is Frame.Binary -> TODO()
                    is Frame.Close -> TODO()
                    is Frame.Ping -> TODO()
                    is Frame.Pong -> TODO()
                }
            }
        }
    }
    //endregion

}