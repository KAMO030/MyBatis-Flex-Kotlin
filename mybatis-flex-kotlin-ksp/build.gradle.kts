version = libs.versions.mybatisflex.kotlin.ksp.get()

plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.mybatisflex.annotation)
    implementation(libs.ksp.api)

    kspTest(project(":mybatis-flex-kotlin-ksp"))
    testImplementation(libs.mybatisflex.core)
    testImplementation(project(":mybatis-flex-kotlin-extensions"))
}


publishing {
    publications.withType<MavenPublication> {
        // Provide artifacts information requited by Maven Central
        pom {
            name.set("mybatis-flex-kotlin-ksp")
            description.set("MyBatis-Flex KSP extension to help generate boilerplate code, just like MyBatis Flex Processor.")
        }
    }
}
