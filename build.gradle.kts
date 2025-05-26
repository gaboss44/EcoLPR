plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.1.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.gaboss44"
version = findProperty("version")!!
val libreforgeVersion = findProperty("libreforge-version")

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") { name = "spigotmc-repo" }
        maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype" }
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://jitpack.io")
    }

    dependencies {
        compileOnly("com.willfp:eco:6.75.0")
        compileOnly("org.jetbrains:annotations:24.0.1")
        implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")
        implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.21")
    }

    kotlin {
        jvmToolchain(17)
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            compilerOptions {
                apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
                languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            }
        }

        processResources {
            filesMatching(listOf("**plugin.yml", "**eco.yml")) {
                expand(
                    "version" to project.version,
                    "libreforgeVersion" to libreforgeVersion,
                    "pluginName" to rootProject.name
                )
            }
        }
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        dependsOn(":api:build", ":core:build")

        from(project(":api").sourceSets.main.get().output)
        from(project(":core").sourceSets.main.get().output)

        configurations = listOf(project.configurations.runtimeClasspath.get())

        relocate("com.willfp.libreforge.loader", "com.github.gaboss44.ecolpr.libreforge.loader")
        relocate("com.willfp.ecomponent", "com.github.gaboss44.ecolpr.ecomponent")

        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}
