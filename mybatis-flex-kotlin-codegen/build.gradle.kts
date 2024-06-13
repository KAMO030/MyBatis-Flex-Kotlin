version = libs.versions.mybatisflex.kotlin.codegen.get()

dependencies {
    implementation(libs.mybatisflex.codegen)
    compileOnly(libs.mybatisflex.core)
    // https://mvnrepository.com/artifact/com.squareup/kotlinpoet-jvm
    implementation(libs.kotlinpoet)

    testImplementation(libs.h2)
    testImplementation(libs.mysql)
    testImplementation(libs.mybatisflex.spring)

    testImplementation(project(":mybatis-flex-kotlin-extensions"))
}
