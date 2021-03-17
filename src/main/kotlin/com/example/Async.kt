package com.example

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.routing.*
import kotlinx.coroutines.*
import kotlinx.html.*
import java.util.*
import java.util.concurrent.Executors
import kotlin.system.*

typealias DelayProvider = suspend (ms: Int) -> Unit

val compute = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

fun Application.async(random: Random = Random(), delayProvider: DelayProvider = { delay(it.toLong()) }) {
//    install(DefaultHeaders)
//    install(CallLogging)
    routing {
        // Tabbed browsers can wait for first request to complete in one tab before making a request in another tab.
        // Presumably they assume second request will hit 304 Not Modified and save on data transfer.
        // If you want to verify simultaneous connections, either use "curl" or use different URLs in different tabs
        // Like localhost:8080/1, localhost:8080/2, localhost:8080/3, etc
        get("/async") {
            val startTime = System.currentTimeMillis()
            call.respondHandlingLongCalculation(random, delayProvider, startTime)
        }
    }
}

private suspend fun ApplicationCall.respondHandlingLongCalculation(random: Random, delayProvider: DelayProvider, startTime: Long) {
    val queueTime = System.currentTimeMillis() - startTime
    var number = 0
    val computeTime = measureTimeMillis {
        // We specify a coroutine context, that will use a thread pool for long computing operations.
        // In this case it is not necessary since we are "delaying", not sleeping the thread.
        // But serves as an example of what to do if we want to perform slow non-asynchronous operations
        // that would block threads.
        withContext(compute) {
            for (index in 0 until 300) {
                delayProvider(10)
                number += random.nextInt(100)
            }
        }
    }

    respondHtml {
        head {
            title { +"Ktor: async" }
        }
        body {
            p {
                +"Hello from Ktor Async sample application"
            }
            p {
                +"We calculated number $number in $computeTime ms of compute time, spending $queueTime ms in queue."
            }
        }
    }
}

/**
Testing:

~$ ab -n 100 -c 100 http://localhost:8080/async
This is ApacheBench, Version 2.3 <$Revision: 1826891 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient).....done


Server Software:        ktor-server-core/1.5.2
Server Hostname:        localhost
Server Port:            8080

Document Path:          /async
Document Length:        253 bytes

Concurrency Level:      100
Time taken for tests:   6.638 seconds
Complete requests:      100
Failed requests:        0
Total transferred:      43900 bytes
HTML transferred:       25300 bytes
Requests per second:    15.07 [#/sec] (mean)
Time per request:       6637.657 [ms] (mean)
Time per request:       66.377 [ms] (mean, across all concurrent requests)
Transfer rate:          6.46 [Kbytes/sec] received

Connection Times (ms)
min  mean[+/-sd] median   max
Connect:        0   11   5.6     10      20
Processing:  3280 3334  17.8   3339    3358
Waiting:     3259 3333  18.6   3339    3358
Total:       3280 3345  18.2   3351    3374

Percentage of the requests served within a certain time (ms)
50%   3351
66%   3356
75%   3358
80%   3359
90%   3362
95%   3364
98%   3369
99%   3374
100%   3374 (longest request)
*/