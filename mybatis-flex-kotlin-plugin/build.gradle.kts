version = libs.versions.mybatisflex.kotlin.plugin.get()

plugins {
    `java-gradle-plugin`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-noarg")
    implementation("org.jetbrains.kotlin:kotlin-allopen")
}

gradlePlugin {
    plugins {
        create("MybatisFlexKotlinPlugin") {
            id = "com.mybatisflex.kotlin.plugin.mybatisflex"
            implementationClass = "com.mybatisflex.kotlin.plugin.MybatisFlexKotlinPlugin"
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("mybatis-flex-kotlin-plugin")
            description.set("Gradle Kotlin Plugin for Mybatis Flex")
        }
    }
}
