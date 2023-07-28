package us.rbirm.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.micrometer.prometheus.*
import io.micrometer.core.instrument.binder.system.ProcessorMetrics

fun Application.configureMetrics() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
            registry = appMicrometerRegistry
            meterBinders = listOf(
                ProcessorMetrics(),
            )
        }
}


