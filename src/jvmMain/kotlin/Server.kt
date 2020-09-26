import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {

    val shoppingList =
            mutableListOf(ShoppingListItem("Banana", 1),
                          ShoppingListItem("Potato", 2),
                          ShoppingListItem("Pizda", 3))

    embeddedServer(Netty, 9090) {

        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Delete)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            anyHost()
        }
        install(Compression) {
            gzip()
        }

        routing {

            get("/") {
                call.respondText(this::class.java.classLoader.getResource("index.html")!!.readText(), ContentType.Text.Html)
            }

            static("/") {
                resources("")
            }

            route (ShoppingListItem.path) {
                get {
                    call.respond(shoppingList)
                }

                post {
                    shoppingList += call.receive<ShoppingListItem>()
                    call.respond(HttpStatusCode.OK)
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid request!")

                    shoppingList.removeIf { it.id == id }

                    call.respond(HttpStatusCode.OK)
                }
            }
        }

    }.start(wait = true)
}