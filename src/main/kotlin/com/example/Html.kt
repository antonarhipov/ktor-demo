package com.example

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.css.*
import kotlinx.html.*

//See https://ktor.io/docs/html-dsl.html
//See https://ktor.io/docs/css-dsl.html
fun Application.html() {

    routing {
        get("/html") {
            call.respond(
                HttpStatusCode.OK,
                Page().page1
            )
        }
    }


    routing {
        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }
    }

    routing {
        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.darkBlue
                    margin(0.px)
                }
                rule("h1.page-title") {
                    color = Color.white
                }
            }
        }
    }
    routing {
        get("/html-css-dsl") {
            call.respondHtml {
                head {
                    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
                }
                body {
                    h1(classes = "page-title") {
                        +"Hello from Ktor!"
                    }
                }
            }
        }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

class Page {
    val page1: HTML.() -> Unit = {
        common {
            title {
                +"Page 1"
            }
        }
    }

    val page2: HTML.() -> Unit = {
        common {
            title {
                +"Page 2"
            }
        }
    }

    private fun HTML.common(title: HEAD.() -> Unit) {
        head {
            script {
                // some script goes here
            }
            title() // injects title
        }
        body {
            div {
                p {
                    +"Common part"
                }
            }
        }
    }
}