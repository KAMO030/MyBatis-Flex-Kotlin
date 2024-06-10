version = libs.versions.mybatisflex.kotlin.codegen.get()

dependencies {
    implementation(libs.mybatisflex.codegen)
    implementation(libs.mybatisflex.core)
    // https://mvnrepository.com/artifact/com.squareup/kotlinpoet-jvm
    implementation("com.squareup:kotlinpoet:1.17.0")

    testImplementation(libs.mysql)

    testImplementation(project(":mybatis-flex-kotlin-extensions"))
}
