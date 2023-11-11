version = libs.versions.mybatisflex.kotlin.ksp.get()

dependencies {
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.mybatisflex.annotation)
    implementation(libs.ksp.api)
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
