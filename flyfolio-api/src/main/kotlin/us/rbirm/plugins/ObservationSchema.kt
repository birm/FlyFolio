package us.rbirm.plugins

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.serialization.Serializable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*

@Serializable
data class ExposedObservation(val tail: String, val lat: Float, val lon: Float, val track: Float, val alt: Float, val src: String="", val dest: String="")
class ObservationService(private val database: Database) {
    object Observations : Table() {
        val id = integer("id").autoIncrement()
        val tail = varchar("tail", length=10)
        val lat = float("lat")
        val lon = float("lon")
        val track = float("track")
        val alt = float("alt")
        val src = varchar("src", length=10)
        val dest = varchar("dest", length=10)
        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Observations)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(observation: ExposedObservation): Int = dbQuery {
        Observations.insert {
            it[lat] = observation.lat
            it[lon] = observation.lon
            it[track] = observation.track
            it[alt] = observation.alt
            it[src] = observation.src
            it[dest] = observation.dest
            it[tail] = observation.tail
        }[Observations.id]
    }

    suspend fun read(id: Int): ExposedObservation? {
        return dbQuery {
            Observations.select { Observations.id eq id }
                .map { ExposedObservation(it[Observations.tail], it[Observations.lat], it[Observations.lon], it[Observations.track], it[Observations.alt], it[Observations.src], it[Observations.dest]) }
                .singleOrNull()
        }
    }

    suspend fun list(): List<ExposedObservation>{
        return dbQuery{
            Observations.selectAll()
            .map { ExposedObservation(it[Observations.tail], it[Observations.lat], it[Observations.lon], it[Observations.track], it[Observations.alt], it[Observations.src], it[Observations.dest]) }
        }
    }

    suspend fun update(id: Int, observation: ExposedObservation) {
        dbQuery {
            Observations.update({ Observations.id eq id }) {
                it[lat] = observation.lat
                it[lon] = observation.lon
                it[track] = observation.track
                it[alt] = observation.alt
                it[src] = observation.src
                it[dest] = observation.dest
                it[tail] = observation.tail
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Observations.deleteWhere { Observations.id.eq(id) }
        }
    }
}