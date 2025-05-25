group = "com.github.gaboss44"
version = rootProject.version

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "${rootProject.name}-api"
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set("${rootProject.name}-api")
                description.set("API for ${rootProject.name}")
                url.set("https://github.com/gaboss44/EcoLPR")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("gaboss44")
                        name.set("Gabriel")
                        email.set("cpr180821.gs@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/gaboss44/EcoLPR.git")
                    developerConnection.set("scm:git:ssh://github.com/gaboss44/EcoLPR.git")
                    url.set("https://github.com/gaboss44/EcoLPR")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/gaboss44/EcoLPR")
            credentials {
                username = "gaboss44"
                password = System.getenv("MAVEN_PASSWORD")?: throw GradleException("Publish token not found")
            }
        }
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    implementation("com.willfp:ecomponent:1.4.1")
}