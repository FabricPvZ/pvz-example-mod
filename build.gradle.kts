// The mod toolchain of this workspace: applying cn.enaium.fabricpvz.loom supplies the game - the
// game jar and the libraries the online version package's JSON next to it names (the real Fabric
// Loader, Mixin, ASM, SLF4J/Log4j and LWJGL with its natives), on the compile, test and launch class
// paths - plus the runClient task, the end-to-end test conventions and the publication of the mod jar
// under mavenLocal coordinates. It supplies nothing else: everything this mod uses besides the game
// is declared below, with the version this build was made with.
//
// The version is the plugin's own (every artifact of this workspace is released under its own
// version); the plugin resolves from the Gradle Plugin Portal, with mavenLocal after it as the
// fallback until the portal serves 1.0.14 (see settings.gradle.kts).

plugins {
    id("cn.enaium.fabricpvz.loom") version "1.0.14"
}

val fabricApi = "cn.enaium.fabricpvz:fabric-api:1.2.2"

dependencies {
    // Which game distribution this mod is built against: the version package of the game's release,
    // i.e. the asset pvz-portable-<version>.zip of FabricPvZ/pvz-portable. The toolchain downloads it
    // once into ~/.gradle/caches/pvz (recording its SHA-256 next to it) and unpacks it into
    // build/pvz/dist: the game jar goes on the class paths as a file, and every library the JSON
    // manifest next to it names (loader, Mixin, ASM, logging, LWJGL with this machine's natives) is a
    // Maven coordinate. While working on the game's own code, point it at the distribution that
    // build writes instead (pvz(files("../PvZ-Portable/build/distributions")) or
    // pvz(files("../PvZ-Portable/build/mcversion/PvZ-Portable-<version>"))).
    pvz("cn.enaium.fabricpvz:pvz-portable:1.2.5")

    // The API this mod uses: the aggregate jar, whose nested jars are the five feature modules.
    // Compilation carries the modules through its POM (javac and kotlinc read jar files, not jars
    // nested inside a jar); at runtime the aggregate alone is used, because Fabric Loader reads the
    // nested jars out of it. `testCompileOnly` because a test source set does not inherit
    // `compileOnly`.
    for (configuration in listOf("compileOnly", "testCompileOnly")) {
        add(configuration, fabricApi)
    }
    for (configuration in listOf("testImplementation", "pvzRunClasspath")) {
        (add(configuration, fabricApi) as ModuleDependency).isTransitive = false
    }
}

group = "cn.enaium.fabricpvz"
version = "1.1.0"
description = "Example mod for the pvz-fabric toolchain (API events + mixins)"
