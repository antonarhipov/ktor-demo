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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@KtorExperimentalLocationsAPI
@Location("/index")
class Index(val message: String = "Hello from index!")

@KtorExperimentalLocationsAPI

//@Location("/employee/{id}") //  employee/Anton?project=Kotlin
@Location("/employee/{id}/{project}") //  employee/Anton/Kotlin
class Employee(
    val id: String,
    val project: String,
    val dob: LocalDate
)

@KtorExperimentalLocationsAPI
@Location("/employee")
class EmployeeList

// See https://ktor.io/docs/features-locations.html
@KtorExperimentalLocationsAPI
fun Application.locations() {
    install(Locations)

    install(DataConversion) {
        convert<LocalDate> { // this: DelegatingConversionService
            val formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

            decode { values, _ -> // converter: (values: List<String>, type: Type) -> Any?
                values.singleOrNull()?.let { LocalDate.parse(it) }
            }

            encode { value -> // converter: (value: Any?) -> List<String>
                when (value) {
                    null -> listOf()
                    is LocalDate -> listOf(formatter.format(value))
                    else -> throw DataConversionException("Cannot convert $value as Date")
                }
            }
        }
    }

    routing {
        get<Index> {
            call.respondText("Locations demo: ${it.message}")
        }
        get<Employee> {
            call.respondText("Employee: ${it.id}. Project: ${it.project}. DoB: ${it.dob}")
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
    Employee("Anton", "Kotlin", LocalDate.now()),
    Employee("Hadi", "Wasabi", LocalDate.now()),
    Employee("Leonid", "Ktor", LocalDate.now()),
)
