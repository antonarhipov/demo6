package com.example.demo

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<Demo6Application>().with(TestcontainersConfiguration::class).run(*args)
}
