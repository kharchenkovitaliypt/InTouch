plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

val composeVer = "0.1.0-dev17"

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    defaultConfig {
        applicationId = "com.vitaliykharchenko.intouch"
        minSdkVersion(21)
        targetSdkVersion(30)
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
            "-XXLanguage:+InlineClasses",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
        jvmTarget = "1.8"
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
        kotlinCompilerVersion = "1.4.0"
    }
}
androidExtensions {
    isExperimental = true
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.core:core-ktx:1.3.1")

    implementation("androidx.compose.foundation:foundation-layout:$composeVer")
    implementation("androidx.compose.material:material:$composeVer")
    implementation("androidx.ui:ui-tooling:$composeVer")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.fragment:fragment-ktx:1.2.5")

    val coroutinesVersion = "1.3.5"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    val daggerVersion = "2.27"
    implementation("com.google.dagger:dagger-android:$daggerVersion")
    implementation("com.google.dagger:dagger-android-support:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")

    val epoxyVersion = "3.9.0"
    implementation("com.airbnb.android:epoxy:$epoxyVersion")
    kapt("com.airbnb.android:epoxy-processor:$epoxyVersion")

    api("com.michael-bull.kotlin-result:kotlin-result:1.1.6")

    testImplementation("junit:junit:4.13")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    val mockitoVersion = "3.3.3"
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
