plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "de.christophhaefner.qrcodetoclipboard"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.christophhaefner.qrcodetoclipboard"
        minSdk = 36
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation("com.google.android.material:material:1.14.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}
