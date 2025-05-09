plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // NAVEGACION
    alias(libs.plugins.jetbrainsKotlinSeialization)
    // CHARSHLYTICS Y FIREBASE
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = "com.lixferox.app_tfg_2024"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lixferox.app_tfg_2024"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // NAVEGACION
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    // CHRASHLYTICS
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    // FIREBASE
    implementation(libs.firebase.auth)
    // FIRESTORE
    implementation(libs.firebase.firestore)
    //DIAGRAMAS
    implementation (libs.compose.charts)
    // IMAGENES
    implementation(libs.coil.compose)
}