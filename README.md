## Grim Locations

### Overview
Grim Locations is a tool for Grim Dawn that accompanies and requires the mod [Grim Internals](https://forums.crateentertainment.com/t/tool-grim-internals).

One of the features of Grim Internals is the ability to save the player's current location. Grim Locations allows these locations to be categorized and loaded depending on the character, mod, and difficulty the player chooses to play for that session.

When locations have been saved to Grim Locations they can be edited, transferred between characters, deleted, reordered, and more.

### Tools

Grim Locations is built entirely in Kotlin for the JVM with the new IR backend. It uses Jetpack Compose for Desktop, Kotlin Coroutines, and Kotlin Exposed with a SQLite database.

The architecture of the app is viewmodel based with a custom framework built around Compose and Exposed in the shared/src/main/kotlin/io/grimlocations/shared/framework folder. The implementations of this framework exist in the other folders on the same level as the framework folder.

### Building
The project runs on Java 15 and is built using Gradle with several custom tasks defined. Currently, it has only been tested on Windows. The createProductionBundle task runs the required tasks to create a distributable a zip file. For this to work [lessmsi](https://lessmsi.activescott.com/) must be downloaded and its root directory put on the system's PATH environment variable.

If an error occurs when importing the project into IntelliJ run 'gradlew hardClean && gradlew build' form the project root, then refresh the gradle project in IntelliJ.

If a "Cannot expand ZIP 'projectRoot/build/wixToolset/wix311.zip' as it is not a file." error occurs when building the project, go to root/build/wixToolset/wix311.zip (yes this is a folder), and place the wix311-binaries.zip from within /wix311.zip into /wixToolset. Then delete the wix311.zip folder and rename wix311-binaries.zip to wix311.zip. Run the build again and it will pick up the correct file.


### Launching

There are two entry points to the app. One by opening the editor and another by opening the launcher. The launcher is meant to be added to Steam or to the user's desktop to launch the game. The editor allows the user to edit their saved locations. Each entry point is also accessible from each other, meaning the launcher can launch the editor and visa versa.


