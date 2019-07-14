buildscript {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.6.0-alpha04")
        classpath(kotlin("gradle-plugin", version = "1.3.20"))
        classpath("gradle.plugin.com.techshroom:incise-blue:0.3.14")
    }
}

tasks.register("build") {
    dependsOn(":app:build")
}
