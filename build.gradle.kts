import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

// 不要修改或内联此属性，否则 subporjcts 处将无法正常使用。
val lib: LibrariesForLibs = libs

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.noarg) apply false
    `maven-publish`
    `kotlin-dsl`
    signing
}

// 在 subprojets 处，不要使用属性 libs ，而是改为在此文件中定义的属性 lib。
subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = lib.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = lib.plugins.dokka.get().pluginId)
    apply(plugin = "org.gradle.kotlin.kotlin-dsl")

    group = providers.gradleProperty("group").get()

    dependencies {
        implementation(lib.kotlin.reflect)
        testCompileOnly(lib.junit.jupiter.api)
        testRuntimeOnly(lib.junit.jupiter.engine)
        implementation(lib.dokka)
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjvm-default=all"  // 允许 Java 不重写 Kotlin 接口中非抽象方法
        }
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
}