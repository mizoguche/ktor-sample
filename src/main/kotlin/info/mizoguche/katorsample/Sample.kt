package info.mizoguche.katorsample

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

data class Model(val name: String, val item: Item)
data class Item(val id: String)

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        install(DefaultHeaders) {
            header("X-Custom-Header", "hogehoge")
        }
        install(CallLogging)
        install(ContentNegotiation) {
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
                registerModule(JavaTimeModule())
            }
        }
        install(Routing) {
            get("/items/{id}") {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    val item = Item(id)
                    val model = Model("name of $id", item)
                    call.respond(model)
                }
            }
        }
    }.start(wait = true)
}