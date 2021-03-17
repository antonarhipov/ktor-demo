package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

class CustomHeader(configuration: Configuration) {
    // get an immutable snapshot of a configuration values
    val name = configuration.headerName
    val value = configuration.headerValue

    // Feature configuration class
    class Configuration {
        // mutable properties with default values so user can modify it
        var headerName = "Custom"
        var headerValue = "Value"
    }

    private fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        // Add custom header to the response
        context.call.response.header(name, value)
    }


    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, CustomHeader> {
        override val key = AttributeKey<CustomHeader>("CustomHeader")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CustomHeader {
            // Call user code to configure a feature
            val configuration = Configuration().apply(configure)

            // Create a feature instance
            val feature = CustomHeader(configuration)

            // Install an interceptor that will be run on each call and call feature instance
            pipeline.intercept(ApplicationCallPipeline.Call) {

                feature.intercept(this)
            }

            // Return a feature instance so that client code can use it
            return feature
        }
    }
}
fun Application.customFeature() {
    install(CustomHeader) {
        headerName = "Abc"
        headerValue = "Def"
    }
    routing {
        get("/customHeader") {
            call.respondText("Check out headers!")
        }
    }
}