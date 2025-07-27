import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.1.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.willfp.libreforge-gradle-plugin") version "1.0.3"
}

group = "com.github.gaboss44"
version = findProperty("version")!!

base {
    archivesName.set(project.name)
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")
    // apply(plugin = "com.willfp.libreforge-gradle-plugin")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://jitpack.io")
        maven("https://repo.lucko.me/")
    }

    dependencies {
        compileOnly("com.willfp:eco:6.76.0")
        compileOnly("org.jetbrains:annotations:23.0.0")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    }

    java {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    val libreforgeVersion = findProperty("libreforge-version")!!

    tasks {
        jar { archiveFileName.set("EcoLPR-v${version}.jar") }

        shadowJar {
            // archiveBaseName.set("EcoLPR")
            // archiveClassifier.set("")
            // archiveVersion.set("v${project.version}")

            relocate("com.willfp.libreforge.loader", "com.github.gaboss44.ecolpr.libreforge.loader")
            relocate("com.willfp.ecomponent", "com.github.gaboss44.ecolpr.ecomponent")

            // mergeServiceFiles()

            // minimize()
        }

        compileKotlin {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_17
            }
        }

        compileJava {
            options.isDeprecation = true
            options.encoding = "UTF-8"

            dependsOn(clean)
        }

        processResources {
            filesMatching(listOf("**/plugin.yml", "**/eco.yml")) {
                expand(
                    "version" to project.version,
                    "libreforgeVersion" to libreforgeVersion,
                    "pluginName" to rootProject.name
                )
            }
        }

        build {
            dependsOn(shadowJar)
        }
    }
}
