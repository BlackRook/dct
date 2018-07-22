package ru.domclicktest.dct

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DctApplication

fun main(args: Array<String>) {
    runApplication<DctApplication>(*args)
}
