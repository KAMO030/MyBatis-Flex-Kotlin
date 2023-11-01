version = libs.versions.mybatisflex.kotlin.codegen.get()

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.noarg)
}

dependencies {
    implementation(libs.mybatisflex.codegen)
    implementation(libs.mybatisflex.core)
    testImplementation(libs.mysql)
}

noArg {
    annotation("com.mybatisflex.annotation.Table")
}