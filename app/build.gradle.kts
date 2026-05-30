plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.medicoapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.medicoapplication"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ViewModel + viewModels() delegate
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    // lifecycleScope + collect on StateFlow
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    implementation("androidx.activity:activity-ktx:1.9.3")

    //Sessão
    implementation("androidx.datastore:datastore-preferences:1.1.1")
}
