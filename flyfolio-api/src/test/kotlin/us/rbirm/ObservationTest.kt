package us.rbirm


import us.rbirm.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.test.*


class ObservationTest {
    @Test
    fun testEmptyObservations() = testApplication {
        application {
            configureSerialization()
            configureDatabases()
            configureRouting()
        }
        client.get("/observations").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("[]", bodyAsText())
        }
    }

    @Test
    fun testPostObservation() = testApplication {
        application {
            configureSerialization()
            configureDatabases()
            configureRouting()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/observations") {
            contentType(ContentType.Application.Json)
            setBody(ExposedObservation("N314GT", 1.1f, 2.2f, 180f, 30000f))
        }.apply{
            assertEquals(HttpStatusCode.Created, status)
        }
        
    }
}
