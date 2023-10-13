plugins {
    id("java")
    kotlin("jvm") version "1.8.20"
    id("com.google.devtools.ksp") version "1.8.20-1.0.11"
}

group = "com.mybatis-flex"
version = "1.0.1"

repositories {
    mavenCentral()
}

ksp {
    arg("flex.project.path", projectDir.path)
    arg("flex.root.project.path", rootProject.projectDir.absolutePath)
    arg("flex.generate.lazy", "true")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("com.mysql:mysql-connector-j:8.0.33")
    testImplementation("com.mybatis-flex:mybatis-flex-spring:1.6.2")
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.springframework:spring-test:5.3.27")
    testImplementation(kotlin("test"))


    compileOnly("com.mybatis-flex:mybatis-flex-core:1.6.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    kspTest(project(":mybatis-flex-kotlin-ksp"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjvm-default=all"
    }
}