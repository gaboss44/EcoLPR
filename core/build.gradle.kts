group = rootProject.group
version = rootProject.version

dependencies {
    compileOnly(project(":api"))
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.5")
}

kotlin {
    jvmToolchain(17)
}
