@file:Suppress("UnstableApiUsage")

version = libs.versions.mybatisflex.kotlin.plugin.get()

apply(plugin = libs.plugins.gradle.plugin.publish.get().pluginId)

dependencies {
    implementation(kotlin("noarg"))
    implementation(kotlin("allopen"))
}

gradlePlugin {
    website.set("https://github.com/KAMO030/mybatis-flex-kotlin")
    vcsUrl.set("https://github.com/KAMO030/mybatis-flex-kotlin.git")
    plugins {
        create("mybatisFlexKotlin") {
            id = "com.mybatis-flex.kotlin"
            implementationClass = "com.mybatisflex.kotlin.plugin.MybatisFlexKotlinPlugin"
            displayName = "Mybatis Flex Kotlin Plugin"
            description = "Gradle Kotlin Plugin for Mybatis Flex"
            tags.set(setOf("mybatis", "mybatis-flex", "kotlin"))
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
