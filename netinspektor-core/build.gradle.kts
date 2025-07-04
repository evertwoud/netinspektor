import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    id("maven-publish")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinxSerialization)
}

group = rootProject.extra.properties["library_group"] as String
version = rootProject.extra.properties["library_version"] as String

publishing {
    repositories {
        maven {}
    }
}

kotlin {
    @Suppress("UnstableApiUsage")
    androidLibrary {
        namespace = "com.evertwoud.netinspektor.core"
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
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.datetime)
        }
    }
}