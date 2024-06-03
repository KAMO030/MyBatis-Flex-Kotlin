version = libs.versions.mybatisflex.kotlin.codegen.get()

dependencies {
    implementation(libs.mybatisflex.codegen)
    implementation(libs.mybatisflex.core)
    implementation(libs.kotlinpoet)
    testImplementation(libs.mysql)

    testImplementation(project(":mybatis-flex-kotlin-extensions"))
}
