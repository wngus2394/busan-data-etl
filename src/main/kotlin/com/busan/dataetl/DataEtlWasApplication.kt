package com.busan.dataetl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DataEtlWasApplication

fun main(args: Array<String>) {
	runApplication<DataEtlWasApplication>(*args)
}
