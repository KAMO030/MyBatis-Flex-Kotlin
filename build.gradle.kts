import org.gradle.internal.impldep.org.codehaus.plexus.util.MatchPatterns.from
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

group = "com.mybatis-flex"
version = "1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("com.mybatis-flex:mybatis-flex-core:1.6.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")


    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.springframework:spring-test:5.3.27")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("com.mysql:mysql-connector-j:8.0.33")
    testImplementation("com.mybatis-flex:mybatis-flex-spring:1.6.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjvm-default=all"
    }
}

kotlin {
    jvmToolchain(8)
}

plugins {
    kotlin("jvm") version "1.8.20"
    `maven-publish`
    `kotlin-dsl`
    signing
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






