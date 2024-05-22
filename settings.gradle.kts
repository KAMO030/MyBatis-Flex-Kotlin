rootProject.name = "mybatis-flex-kotlin"
include("mybatis-flex-kotlin-ksp")
include("mybatis-flex-kotlin-extensions")
include("mybatis-flex-kotlin-codegen")
include("mybatis-flex-kotlin-plugin")

pluginManagement {
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
        gradlePluginPortal()
        mavenCentral()
    }
}

