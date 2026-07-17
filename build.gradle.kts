// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "9.3.0" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.github.ben-manes.versions") version "0.51.0"
}

buildscript {
    dependencies {
        classpath ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}