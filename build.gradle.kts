import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//plugins {
//    kotlin("jvm") version "1.4.20" apply false
//    id("org.jetbrains.compose") version "0.3.0-build133" apply false
//}



repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

tasks.register<Delete>("deleteEditorExtractedMsi") {
    delete("${project(":editor").buildDir}\\compose\\binaries\\main\\msi\\SourceDir")
}

tasks.register<Delete>("deleteLauncherExtractedMsi") {
    delete("${project(":launcher").buildDir}\\compose\\binaries\\main\\msi\\SourceDir")
}

tasks.register<Exec>("extractEditor") {
    dependsOn(":editor:packageMsi")
    dependsOn("deleteEditorExtractedMsi")

    commandLine(
        "lessmsi",
        "x",
        "${project(":editor").buildDir}\\compose\\binaries\\main\\msi\\GrimLocationsEditor-1.0.msi",
        "${project(":editor").buildDir}\\compose\\binaries\\main\\msi\\"
    )
}

tasks.register<Exec>("extractLauncher") {
    dependsOn(":launcher:packageMsi")
    dependsOn("deleteLauncherExtractedMsi")

    commandLine(
        "lessmsi",
        "x",
        "${project(":launcher").buildDir}\\compose\\binaries\\main\\msi\\GrimLocationsLauncher-1.0.msi",
        "${project(":launcher").buildDir}\\compose\\binaries\\main\\msi\\"
    )
}

tasks.register("extractAll") {
    dependsOn("extractEditor")
    dependsOn("extractLauncher")
}

tasks.register<Copy>("copyEditor") {
    dependsOn("extractEditor")

    from("${project(":editor").buildDir}\\compose\\binaries\\main\\msi\\SourceDir\\GrimLocationsEditor")
    into("$buildDir\\productionbuild")
    eachFile {
        if (this.relativePath.getFile(destinationDir).exists()) {
            this.exclude()
        }
    }
}

tasks.register<Copy>("copyLauncher") {
    dependsOn("extractLauncher")

    from("${project(":launcher").buildDir}\\compose\\binaries\\main\\msi\\SourceDir\\GrimLocationsLauncher")
    into("$buildDir\\productionbuild")
    eachFile {
        if (this.relativePath.getFile(destinationDir).exists()) {
            this.exclude()
        }
    }
}

tasks.register<Copy>("copyDatabase") {
    from(".\\sqlite\\empty")
    into("$buildDir\\productionbuild")
}

tasks.register("copyAll") {
    dependsOn("copyDatabase")
    dependsOn("copyEditor")
    dependsOn("copyLauncher")
}

tasks.register<Delete>("cleanProdBuild") {
    delete("$buildDir\\productionbuild", "$buildDir\\productionartifact")
}

tasks.register("cleanAll") {
    dependsOn(":editor:clean")
    dependsOn(":launcher:clean")
    dependsOn("cleanProdBuild")
}


tasks.register<Zip>("createProductionBundle") {
    dependsOn("cleanAll")
    mustRunAfter("cleanAll")
    dependsOn("copyAll")

    from("$buildDir\\productionbuild")
    archiveFileName.set("GrimLocations.zip")
    destinationDirectory.set(File("$buildDir\\productionartifact"))
}

tasks.register<Delete>("hardClean") {
    delete(
        "${project(":editor").buildDir}",
        "${project(":launcher").buildDir}",
        "${project(":shared").buildDir}",
        ".\\buildSrc\\build",
        ".\\buildSrc\\.gradle",
        "$buildDir\\productionbuild",
        "$buildDir\\productionartifact",
        "$buildDir\\kotlin"
    )
}