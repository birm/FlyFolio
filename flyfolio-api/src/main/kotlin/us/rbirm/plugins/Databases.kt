package us.rbirm.plugins

import org.jetbrains.exposed.sql.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureDatabases() {
    val database_str = System.getenv("database_str") ?: "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    val database_user =System.getenv("database_user") ?: "root"
    val database_password =System.getenv("database_password") ?: ""
    val database_driver =System.getenv("database_driver") ?: "org.h2.Driver"
    val database = Database.connect(
            url = database_str,
            user = database_user,
            driver = database_driver,
            password = database_password
        )
    val observationService = ObservationService(database)
    routing {
        // Create
        post("/observations") {
            val observation = call.receive<ExposedObservation>()
            val id = observationService.create(observation)
            call.respond(HttpStatusCode.Created, id)
        }
        // List
        get("/observations") {
            val observations = observationService.list()
            call.respond(HttpStatusCode.OK, observations)
        }
        // Read
        get("/observations/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val observation = observationService.read(id)
            if (observation != null) {
                call.respond(HttpStatusCode.OK, observation)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // Update
        put("/observations/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val observation = call.receive<ExposedObservation>()
            observationService.update(id, observation)
            call.respond(HttpStatusCode.OK)
        }
        // Delete
        delete("/observations/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            observationService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}
