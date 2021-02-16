package com.gokhana.userimporter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBatchCsvToDbApplication

fun main(args: Array<String>) {
    runApplication<SpringBatchCsvToDbApplication>(*args)
}
