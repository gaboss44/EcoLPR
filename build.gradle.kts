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
val libreforgeVersion = findProperty("libreforge-version")

base {
    archivesName.set("project.name")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenLocal()
        mavenCentral()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            name = "spigotmc-repo"
        }
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://jitpack.io")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("com.willfp:eco:6.75.0")
        compileOnly("org.jetbrains:annotations:24.0.1")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")
    }

    java {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }


    tasks {
        shadowJar {
            relocate("com.willfp.libreforge.loader", "com.github.gaboss44.ecolpr.libreforge.loader")
            relocate("com.willfp.ecomponent", "com.github.gaboss44.ecolpr.ecomponent")
        }

        kotlin {
            jvmToolchain(17)
        }

        compileJava {
            options.isDeprecation = true
            options.encoding = "UTF-8"
            dependsOn(clean)
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

        build {
            dependsOn(shadowJar)
        }
    }
}