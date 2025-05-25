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
                url.set("https://github.com/tunombredeusuario/turepositorio")

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
                    connection.set("scm:git:git://github.com/tunombredeusuario/turepositorio.git")
                    developerConnection.set("scm:git:ssh://github.com/tunombredeusuario/turepositorio.git")
                    url.set("https://github.com/tunombredeusuario/turepositorio")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tunombredeusuario/turepositorio")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    implementation("com.willfp:ecomponent:1.4.1")
}