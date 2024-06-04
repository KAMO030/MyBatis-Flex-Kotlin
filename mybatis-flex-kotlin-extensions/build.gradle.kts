version = libs.versions.mybatisflex.kotlin.extensions.get()

apply(plugin = libs.plugins.com.mybatis.flex.kotlin.get().pluginId)

dependencies {
    compileOnly(libs.mybatisflex.core)

    testRuntimeOnly(libs.h2)
    testRuntimeOnly(libs.mysql)

    testImplementation(libs.mybatisflex.spring)
    testImplementation(libs.spring.test)
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("mybatis-flex-kotlin-extensions")
            description.set("Expanding Mybatis-Flex on Kotlin")
        }
    }
}

