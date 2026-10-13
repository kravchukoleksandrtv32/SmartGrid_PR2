// Файл збірки модуля app
plugins {
    alias(libs.plugins.android.application) // Android-застосунок
    alias(libs.plugins.kotlin.android)      // підтримка Kotlin
    alias(libs.plugins.kotlin.compose)      // компілятор Jetpack Compose (Kotlin 2.x)
    alias(libs.plugins.ksp)                 // генерація коду Room
}

android {
    namespace = "ua.kpi.smartgrid"
    compileSdk = 35

    defaultConfig {
        applicationId = "ua.kpi.smartgrid"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // debug-збірка використовується під час розробки
        debug {
            isMinifyEnabled = false
        }
        // release-збірка: можна увімкнути R8 для оптимізації
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Базові бібліотеки AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose (версії узгоджує BOM)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Стороння бібліотека іконок Material
    implementation(libs.androidx.material.icons.extended)

    // Навігація між екранами (заняття 5)
    implementation(libs.androidx.navigation.compose)

    // ViewModel для Compose (заняття 7)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room Database (заняття 8)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Тести
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Інструменти лише для debug-збірки (Preview, Layout Inspector)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
