import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.maybatisflex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.mybatis-flex:mybatis-flex-core:1.5.7")

    testImplementation(kotlin("test"))
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("com.mybatis-flex:mybatis-flex-spring:1.5.7")
    testImplementation("org.springframework:spring-test:5.3.27")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    kaptTest("com.mybatis-flex:mybatis-flex-processor:1.5.7")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>{
    kotlinOptions{
        freeCompilerArgs += "-Xjvm-default=all"
    }
}

kotlin {
    jvmToolchain(8)
}

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("kapt") version "1.9.0"
    application
}

