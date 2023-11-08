version = libs.versions.mybatisflex.kotlin.codegen.get()

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.noarg)
//    alias(libs.plugins.allopen)
}

ksp {
    arg("flex.project.path", project.projectDir.absolutePath)
}

dependencies {
    implementation(libs.mybatisflex.codegen)
    implementation(libs.mybatisflex.core)
    testImplementation(libs.mysql)
    kspTest(project(":mybatis-flex-kotlin-ksp"))
    testImplementation(project(":mybatis-flex-kotlin-extensions"))
}

noArg {
    annotation("com.mybatisflex.annotation.Table")
}

//allOpen {
//    annotation("com.mybatisflex.annotation.Table")
//}