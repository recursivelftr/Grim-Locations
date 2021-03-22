import org.gradle.kotlin.dsl.*
import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.4.31"
    id("org.jetbrains.compose") version "0.4.0-build173"
    id("app.cash.exhaustive") version "0.1.1"
}

group = "io.grimlocations"
version = "1.0.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.useIR = true
compileKotlin.kotlinOptions.jvmTarget = "15"

dependencies {
    val exposedVersion = "0.29.1"
    val coroutinesVersion = "1.4.2"
    val sqliteVersion = "3.34.0"
    val junitVersion = "5.6.0"
    val log4jVersion = "2.14.0"
    val appDirsVersion = "1.2.1"

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")
    implementation("net.harawata:appdirs:$appDirsVersion")
    implementation(compose.desktop.currentOs)
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "io.grimlocations.MainKt"
        nativeDistributions {
            modules = arrayListOf("java.desktop,java.sql")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Grim Locations"

            windows {
                iconFile.set(File("./images/LocationsLayeredPurpleBlack.ico"))
                menuGroup = "Grim Locations"
            }
        }
    }
}

tasks.register<Delete>("deleteExtractedMsi") {
    delete("${buildDir}\\compose\\binaries\\main\\msi\\SourceDir")
}

tasks.register<Exec>("extractMsi") {
    dependsOn("packageMsi")
//    dependsOn("deleteExtractedMsi")

//    doLast {
    commandLine(
        "lessmsi",
        "x",
        "${buildDir}\\compose\\binaries\\main\\msi\\Grim Locations-$version.msi",
        "${buildDir}\\compose\\binaries\\main\\msi\\"
    )
//    }
}

tasks.register<Copy>("copyExtractedMsi") {
    dependsOn("extractMsi")
    dependsOn("cleanProdBuild")

//    doLast {
    from("${buildDir}\\compose\\binaries\\main\\msi\\SourceDir\\GrimLocations")
    into("$buildDir\\productionbuild")
//    eachFile {
//        if (this.relativePath.getFile(destinationDir).exists()) {
//            this.exclude()
//        }
//    }
//    }
}

tasks.register<Delete>("cleanProdBuild") {
    delete("$buildDir\\productionbuild", "$buildDir\\productionartifact")
}

tasks.register<Zip>("createProductionBundle") {
//    dependsOn("clean")
//    mustRunAfter("clean")
    dependsOn("copyExtractedMsi")

    from("$buildDir\\productionbuild")
    archiveFileName.set("GrimLocations.zip")
    destinationDirectory.set(File("$buildDir\\productionartifact"))
}