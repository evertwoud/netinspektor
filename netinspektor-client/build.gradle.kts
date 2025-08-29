import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.mavenPublish)
}

group = "com.evertwoud.netinspektor"
version = "1.0.0"

mavenPublishing {
    signAllPublications()
    coordinates(group.toString(), "netinspektor-client", version.toString())
    pom {
        name = "netinspektor-client"
        description = "Client library for the network inspection tool netinspektor."
        inceptionYear = "2025"
        url = "https://github.com/evertwoud/netinspektor"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "evertwoud"
                name = "Evert Woud"
                url = "https://github.com/evertwoud/"
            }
        }
        scm {
            url = "https://github.com/evertwoud/netinspektor"
            connection = "scm:git:git://github.com/evertwoud/netinspektor.git"
            developerConnection = "scm:git:ssh://git@github.com/evertwoud/netinspektor.git"
        }
    }
}

kotlin {
    @Suppress("UnstableApiUsage")
    androidLibrary {
        namespace = "com.evertwoud.netinspektor.client"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }
    jvm("desktop")
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.netinspektorCore)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.server.websockets)
            implementation(libs.ktor.server.engine)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.engine)
            implementation(libs.konnection)
            implementation(libs.logging)
        }
    }
}