import java.util.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    `kotlin-dsl`
    signing
}

group = providers.gradleProperty("group")
version = libs.versions.mybatisflex.kotlin.ksp

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    // https://mvnrepository.com/artifact/com.google.devtools.ksp/symbol-processing-api
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.20-1.0.11")
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.squareup:kotlinpoet-ksp:1.12.0")
    compileOnly("com.mybatis-flex:mybatis-flex-annotation:1.6.8")
    implementation(kotlin("reflect"))
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
            name.set("mybatis-flex-kotlin")
            description.set("Expanding Mybatis-Flex on Kotlin")
            url.set("https://github.com/KAMO030/MyBatis-Flex-Kotlin")

            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }
            developers {
                developer {
                    id.set("KAMO030")
                    name.set("KAMOsama")
                    email.set("837080904@qq.com")
                }

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
