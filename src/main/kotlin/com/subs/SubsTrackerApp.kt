package com.subs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class SubsTrackerApp

fun main(args: Array<String>) {
    runApplication<SubsTrackerApp>(*args)
}
