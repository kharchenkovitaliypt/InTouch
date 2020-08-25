// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha07")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
