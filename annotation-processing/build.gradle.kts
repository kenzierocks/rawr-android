import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.techshroom.inciseblue.commonLib
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import sun.tools.jar.resources.jar

plugins {
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("com.techshroom.incise-blue")
    kotlin("jvm")
    kotlin("kapt")
    `java-library`
}

inciseBlue {
    util {
        setJavaVersion(JavaVersion.VERSION_1_8)
    }
    ide()
    license()
}

repositories {
    maven {
        url = uri("https://kotlin.bintray.com/kotlinx/")
    }
}

configurations {
    create("processor") {
        // this drags our dependencies along with us to the annotation processor
        extendsFrom(named("runtimeElements").get())
    }
    create("annotation") {
        extendsFrom(named("api").get())
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.0.5")
    implementation("com.squareup:javapoet:1.11.1")
    implementation("com.google.auto:auto-common:0.10")
    commonLib("com.google.auto.service", "auto-service", "1.0-rc4") {
        implementation(lib())
        kapt(lib())
    }

    // include @Generated for <9 on the target projects classpath
    // by including it with our annotations.
    api("javax.annotation:javax.annotation-api:1.3.2")
}

fun Jar.sourceFromJar(task: TaskContainer.() -> TaskProvider<out Jar>) {
    val jar = tasks.task().get()
    dependsOn(jar)
    from(zipTree(jar.archiveFile))
}

tasks.shadowJar {
    dependencies {
        include(dependency("org.jetbrains.kotlinx:kotlinx-metadata-jvm"))
    }
}

kapt

val pJar = tasks.register<Jar>("processorJar") {
    sourceFromJar { jar }

    exclude("net/octyl/rawr/ap/annotation/**")
    archiveClassifier.set("processor")
}
val aJar = tasks.register<Jar>("annotationJar") {
    sourceFromJar { jar }

    include("net/octyl/rawr/ap/annotation/**")
    archiveClassifier.set("annotations")
}
tasks.assemble {
    dependsOn(pJar, aJar)
}

artifacts {
    add("processor", pJar)
    add("annotation", aJar)
}
