import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.format.DateTimeFormatter
import java.time.Instant

plugins {
    kotlin("jvm") version "1.4.0"
    application
    id("com.google.cloud.tools.jib") version "2.5.0"
}
group = "xyz.quaver"
version = "1.2"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("net.dv8tion:JDA:4.2.0_195") {
        exclude("opus-java", "opus-java")
    }
    implementation("xyz.quaver:libpupil:1.7.2")
    testImplementation(kotlin("test-junit"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "Main"
}

jib {
    from {
        image = "openjdk:14"
    }
    to {
        image = "pupilbot"
    }
    container {
        creationTime = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
    }
}