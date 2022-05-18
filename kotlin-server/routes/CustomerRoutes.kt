package com.example.routes

import com.example.models.Customer
import com.example.models.customerStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRouting() {

    route("/customer") {
        // GET all customers
        get {
            if (customerStorage.isNotEmpty()) {
                // this is automatically serialized
                call.respond(customerStorage)
            } else {
                call.respondText("No customers found", status = HttpStatusCode.OK)
            }
        }

        // GET customer by ID

        get("{id?}") {
            // If no parameter is added, return from upper defined function with Bad Request code
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            // We have extracted the ID, and we further check if a client with this ID exists
            // x ?: y <=> if x != null return, else return y
            val customer =
                customerStorage.find { it.id == Integer.parseInt(id) } ?: return@get call.respondText(
                    "No customer with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(customer)
        }

        // POST new customer
        post {
            try {
                // The json response will be deserialized in a Customer object
                val customer = call.receive<Customer>()
                customerStorage.add(customer)
                call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
            } catch (e: Exception){
                // If JSON response fields don't match the structure of the Customer object, an internal error code
                // will be returned.
                call.respondText("Error while deserializing data", status = HttpStatusCode.InternalServerError)
            }
        }

        // DELETE customer by ID
        delete("{id?}") {
            // If no ID comes on the request, return bad request status code
            val id = call.parameters["id"] ?: return@delete call.respondText("Missing ID", status = HttpStatusCode.BadRequest)
            if (customerStorage.removeIf { it.id == Integer.parseInt(id) }) {
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}