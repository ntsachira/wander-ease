plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.ironcodesoftware.wanderease"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ironcodesoftware.wanderease"
        minSdk = 24
        targetSdk = 35
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
            buildConfigField(
                "String",
                "HOST_URL",
                "\"https://hopefully-exciting-buzzard.ngrok-free.app/WanderEase/\""
            )
        }
        debug {
            buildConfigField(
                "String",
                "HOST_URL",
                "\"https://hopefully-exciting-buzzard.ngrok-free.app/WanderEase/\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures{
        viewBinding = true
        android.buildFeatures.buildConfig = true
    }


}

secrets{
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.google.code.gson:gson:2.12.1")

    implementation("com.github.PayHereDevs:payhere-android-sdk:v3.0.17")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}