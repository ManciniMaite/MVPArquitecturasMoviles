import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") version "4.5.0"
}

android {
    namespace = "com.example.mvp_appgatos_tp2"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.mvp_appgatos_tp2"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val props = Properties()
        val archivo = rootProject.file("local.properties")
        if (archivo.exists()) props.load(archivo.inputStream())
        buildConfigField("String", "EXCHANGE_API_KEY", "\"${props.getProperty("EXCHANGE_API_KEY", "")}\"")
        buildConfigField("String", "MINDEE_API_KEY", "\"${props.getProperty("MINDEE_API_KEY", "")}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
}