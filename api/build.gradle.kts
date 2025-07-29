group = rootProject.group
version = rootProject.version

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "ecolpr-api"

            pom {
                name.set("EcoLPR-API")
                description.set("API module for EcoLPR plugin")
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/gaboss44/EcoLPR")
            credentials(PasswordCredentials::class) {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

