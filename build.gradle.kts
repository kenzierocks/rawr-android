import java.util.Properties

buildscript {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0-alpha02")
        classpath(kotlin("gradle-plugin", version = "1.3.20"))
        classpath("gradle.plugin.com.techshroom:incise-blue:0.3.13")
    }
}

tasks.register("build") {
    dependsOn(":app:build")
}
