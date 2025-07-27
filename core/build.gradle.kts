group = rootProject.group
version = rootProject.version

dependencies {
    implementation("com.willfp:ecomponent:1.4.1")
    compileOnly(project(":api"))
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.2.0")
    compileOnly("net.luckperms:api:5.5")
}

kotlin {
    jvmToolchain(17)
}
