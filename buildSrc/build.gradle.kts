

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
    implementation("org.jetbrains.compose:compose-gradle-plugin:0.3.0-build154")
    implementation("app.cash.exhaustive:exhaustive-gradle:0.1.1")
}

repositories {
    gradlePluginPortal()
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}
