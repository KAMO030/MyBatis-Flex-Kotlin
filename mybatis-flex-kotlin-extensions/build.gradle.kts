plugins {
    alias(libs.plugins.ksp)
}

version = libs.versions.mybatisflex.kotlin.extensions.get()

dependencies {

    compileOnly(libs.mybatisflex.core)

    testRuntimeOnly(libs.h2)
    testRuntimeOnly(libs.mysql)

    testImplementation(libs.mybatisflex.spring)
    testImplementation(libs.spring.test)

    kspTest(project(":mybatis-flex-kotlin-ksp"))
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("mybatis-flex-kotlin-extensions")
            description.set("Expanding Mybatis-Flex on Kotlin")
        }
    }
}

