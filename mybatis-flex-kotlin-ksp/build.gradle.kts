plugins {
    kotlin("jvm") version "1.8.20"
}

group = "com.mybatis-flex"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    // https://mvnrepository.com/artifact/com.google.devtools.ksp/symbol-processing-api
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.20-1.0.11")
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.squareup:kotlinpoet-ksp:1.12.0")
    compileOnly("com.mybatis-flex:mybatis-flex-annotation:1.6.8")
    implementation(kotlin("reflect"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}