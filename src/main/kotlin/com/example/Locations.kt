package com.example

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.br

@KtorExperimentalLocationsAPI
@Location("/index")
class Index(val message: String = "Hello from index!")

@KtorExperimentalLocationsAPI
@Location("/employee/{id}")
class Employee(
    val id: String,
    val project: String,
)

@KtorExperimentalLocationsAPI
@Location("/employee")
class EmployeeList

// See https://ktor.io/docs/features-locations.html
@KtorExperimentalLocationsAPI
fun Application.locations() {
    install(Locations)
    routing {
        get<Index> {
            call.respondText("Locations demo: ${it.message}")
        }
        get<Employee> {
            call.respondText("Employee: ${it.id}. Project: ${it.project}" )
        }
        get<EmployeeList> {
            val employees = getEmployeesFromDB()

            call.respondHtml {
                body {
                    employees.forEach {
                        a(locations.href(it)) { +it.id }
                        br
                    }
                }
            }
        }
    }
}

@KtorExperimentalLocationsAPI
fun getEmployeesFromDB(): List<Employee> = listOf(
    Employee("Anton", "Kotlin"),
    Employee("Hadi", "Wasabi"),
    Employee("Leonid", "Ktor"),
)
