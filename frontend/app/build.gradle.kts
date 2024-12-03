plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "iset.dsi.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "iset.dsi.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        //viewBinding = true
       // dataBinding = true
    }
}

dependencies {
    //add Material components
    implementation("com.google.android.material:material:1.7.0-beta01")

    // Retrofit 2.9.0 (la version la plus stable à ce jour)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

// Gson pour la conversion JSON
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.google.android.material:material:1.4.0")

    implementation ("androidx.appcompat:appcompat:1.4.1")
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.0")
    implementation ("com.google.android.material:material:1.9.0")
// Converter pour Gson, nécessaire pour utiliser Gson avec Retrofit
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.firebase:firebase-messaging:23.1.0")
    implementation("com.google.firebase:firebase-core:21.1.0")
}