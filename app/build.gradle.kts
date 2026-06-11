plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.pozornik.mypetmon"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pozornik.mypetmon"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")

    // Glide для загрузки аватарок
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- ЕДИНЫЙ БЛОК FIREBASE ---
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-firestore:24.7.1")

    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
}