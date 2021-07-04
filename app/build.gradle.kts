 plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

val composeVer = "1.0.0-rc01"

android {
    compileSdk = 30
    buildToolsVersion= "30.0.3"

    defaultConfig {
        applicationId = "com.vitaliykharchenko.intouch"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
//            "-Xallow-jvm-ir-dependencies",
//            "-Xskip-prerelease-check"
        )
        jvmTarget = "1.8"
        useIR = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVer
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")
}

kotlin {
    sourceSets.all {
        languageSettings.apply {
            progressiveMode = true
            useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
            useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.activity:activity-ktx:1.3.0-rc01")
    implementation("androidx.fragment:fragment-ktx:1.3.5")

    implementation("androidx.compose.foundation:foundation:$composeVer")
    implementation("androidx.compose.foundation:foundation-layout:$composeVer")
    implementation("androidx.compose.ui:ui:$composeVer")
    implementation("androidx.compose.material:material:$composeVer")
    implementation("androidx.compose.material:material-icons-extended:$composeVer")
    implementation("androidx.compose.animation:animation:$composeVer")
//    implementation("androidx.ui:ui-tooling:$composeVer")
    implementation("androidx.activity:activity-compose:1.3.0-rc01")


//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")

    val coroutinesVer = "1.5.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVer")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVer")

    val daggerVer = "2.37"
    implementation("com.google.dagger:dagger-android:$daggerVer")
    implementation("com.google.dagger:dagger-android-support:$daggerVer")
    kapt("com.google.dagger:dagger-compiler:$daggerVer")
    kapt("com.google.dagger:dagger-android-processor:$daggerVer")

    implementation("com.google.android.gms:play-services-nearby:18.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.9")

    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.8")

    testImplementation("junit:junit:4.13.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVer")

    val mockitoVer = "3.3.3"
    testImplementation("org.mockito:mockito-core:$mockitoVer")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
