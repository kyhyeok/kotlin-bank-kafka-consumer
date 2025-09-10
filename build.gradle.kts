plugins {
    id("org.springframework.boot") version "3.2.3"
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "1.8.0"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"

    kotlin("plugin.serialization") version "1.8.0"
    kotlin("plugin.jpa") version "1.9.22"
}

group = "org.bank"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // mongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // kafka
    implementation("org.springframework.kafka:spring-kafka:3.1.0")

    // Jackson 의존성 추가
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.36.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}