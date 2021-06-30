import org.gradle.kotlin.dsl.*
import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.compose") version "0.5.0-build228"
}

group = "io.grimlocations"
version = "1.0.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "15"

dependencies {
    val exposedVersion = "0.31.1"
    val coroutinesVersion = "1.5.0"
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

tasks.register<Copy>("copyExternal") {
    from(".\\external")
    into("$buildDir/compose/binaries/main/app/Grim Locations/external")
}


tasks.register<Zip>("createProductionArtifact") {
    val clean = tasks.getByName("clean")
    val createDistributable = tasks.getByName("createDistributable")
    val copyExternal = tasks.getByName("copyExternal")
    dependsOn(clean, createDistributable, copyExternal)
    createDistributable.mustRunAfter(clean)
    copyExternal.mustRunAfter(createDistributable)

    from("$buildDir\\compose\\binaries\\main\\app\\Grim Locations")
    archiveFileName.set("GrimLocations.zip")
    destinationDirectory.set(File("$buildDir\\productionartifact"))
}