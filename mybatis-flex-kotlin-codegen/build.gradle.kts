version = libs.versions.mybatisflex.kotlin.codegen.get()

dependencies {
    compileOnly(libs.mybatisflex.core)
    // https://mvnrepository.com/artifact/com.squareup/kotlinpoet-jvm
    implementation(libs.kotlinpoet)

    testImplementation(libs.h2)
    testImplementation(libs.mysql)
    testImplementation(libs.mybatisflex.spring)

    testImplementation(libs.mybatisflex.codegen)
    testImplementation(project(":mybatis-flex-kotlin-extensions"))
}
