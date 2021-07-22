plugins {
    java
    id("fabric-loom") version "0.8-SNAPSHOT"
}

group = "io.github.emilyy-dev"
version = "1.0-SNAPSHOT"

val minecraftVersion: String = "1.17.1"
val yarnMappings: String = "1.17.1+build.24"
val loaderVersion: String = "0.11.6"
val fabricVersion: String = "0.37.0+1.17"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenLocal()
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    modImplementation(include("me.lucyy:squirtgun-platform-fabric:2.0.0-pre6-SNAPSHOT")!!)
}

tasks.processResources {
    filesMatching("**/fabric.mod.json") { expand("version" to version) }
}
