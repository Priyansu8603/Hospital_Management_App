
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.android.library") version "8.5.2" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    alias(libs.plugins.google.gms.google.services) apply false

}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath (libs.google.services)
        classpath (libs.kotlin.gradle.plugin)
        classpath (libs.androidx.navigation.safe.args.gradle.plugin)
        classpath (libs.hilt.android.gradle.plugin)


    }
}
