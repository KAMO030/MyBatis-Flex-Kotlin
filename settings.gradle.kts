pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "mybatis-flex-kotlin"
include("mybatis-flex-kotlin-ksp")
include("mybatis-flex-kotlin-extensions")
include("untitled")
include("untitled")
