import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("maven-publish")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinxSerialization)
}

group = "com.evertwoud.netinspektor"
version = "1.0.0"

publishing {
    repositories {
        maven {}
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