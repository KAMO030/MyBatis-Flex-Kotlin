version = libs.versions.mybatisflex.kotlin.codegen.get()

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.noarg)
    alias(libs.plugins.allopen)
}

ksp {
    arg("flex.project.path", project.projectDir.absolutePath)
}

dependencies {
    implementation(libs.mybatisflex.codegen)
    implementation(libs.mybatisflex.core)
    testImplementation(libs.mysql)
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // https://mvnrepository.com/artifact/com.alibaba/easyexcel-core
    testImplementation("com.alibaba:easyexcel-core:3.3.2")
// https://mvnrepository.com/artifact/com.alibaba/druid
    testImplementation("com.alibaba:druid:1.2.20")


    kspTest(project(":mybatis-flex-kotlin-ksp"))
    testImplementation(project(":mybatis-flex-kotlin-extensions"))
}

noArg {
    annotation("com.mybatisflex.annotation.Table")
}

allOpen {
    annotation("com.mybatisflex.annotation.Table")
}