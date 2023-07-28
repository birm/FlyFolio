package us.rbirm.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.micrometer.prometheus.*

fun Application.configureRouting() {
    routing {
        val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
        get("/metrics") {
            call.respondText {
                registry.scrape()
            }
        }
        get("/") {
            call.respondText("Use an endpoint!")
        }
        get("/health") {
            call.respondText("healthy")
        }
    }
}
