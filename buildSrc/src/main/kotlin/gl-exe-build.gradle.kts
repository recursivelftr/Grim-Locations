import org.gradle.kotlin.dsl.*
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("gl-base-build")
}

dependencies {
    implementation(project(":shared"))
}

compose.desktop {
    application {
        mainClass = "io.grimlocations.${project.name}.MainKt"
        nativeDistributions {
            modules = arrayListOf("java.desktop,java.sql")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            if(project.name == "editor")
                packageName = "GrimLocationsEditor"
            else
                packageName = "GrimLocationsLauncher"
        }
    }
}