import java.util.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    `kotlin-dsl`
    signing
}

group = providers.gradleProperty("group").get()
version = libs.versions.mybatisflex.kotlin.ksp.get()

dependencies {
    testCompileOnly(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.mybatisflex.annotation)
    implementation(libs.kotlin.reflect)
    implementation(libs.ksp.api)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}


// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null


// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile: File = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.forEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}
val sourceJar by tasks.registering(Jar::class) {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

fun getExtraString(name: String) = ext[name]?.toString()

publishing {
    // Configure maven central repository
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
        }
    }

    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(javadocJar)
        artifact(sourceJar)

        // Provide artifacts information requited by Maven Central
        pom {
            name.set("mybatis-flex-kotlin-ksp")
            description.set("MyBatis-Flex KSP extension to help generate boilerplate code, just like MyBatis Flex Processor.")
            url.set("https://github.com/KAMO030/MyBatis-Flex-Kotlin")

            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }
            developers {
                developer {
                    id.set("CloudPlayer")
                    name.set("CloudPlayer")
                    email.set("2909078582@qq.com")
                }
            }
            scm {
                url.set("https://github.com/KAMO030/MyBatis-Flex-Kotlin.git")
            }
        }
    }
}

// Signing artifacts. Signing.* extra properties values will be used
signing {
    sign(publishing.publications)
}
