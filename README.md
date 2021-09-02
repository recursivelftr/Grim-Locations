## Grim Locations

##### Current Version: 1.0

[Download](https://mega.nz/folder/eCZhVCbB#SiSgP5o_mnwQLmgP9C6EEA)

Grim Locations is a tool for Grim Dawn that accompanies and requires the mod [Grim Internals](https://forums.crateentertainment.com/t/tool-grim-internals).

GlockenGerda, the creator of Grim Internals, provided a v1.99f of Grim Internals which Grim Locations requires. It is not available from the Grim Internals forum page and can be downloaded with Grim Locations [here](https://mega.nz/folder/eCZhVCbB#SiSgP5o_mnwQLmgP9C6EEA).

All versions of Grim Internals past v1.99f will also work and should be available from the Grim Internals forum page.

<img src="https://i.imgur.com/vZVNNUR.png" width="672" height="378" />

### How it works

Grim Locations works by providing features for the Grim Internals teleport list seen in the screenshot below.
The Grim Internals teleport list is a set of locations the player can teleport to in-game, or save their current location to teleport to later.

Grim Locations should be kept running alongside the game to take advantage of the auto-sync features.

- Comes with Kortique's massive list of end-game teleport locations that are easily transferable to Grim Dawn even while in-game. (see 0.3.0 release below)

- Provides a teleport list for every character profile, mod, and difficulty rather than sharing the same one across all characters.
- Provides transfer, remove, reorder, and edit functionality for all your saved locations.
- Auto-detects your current character profile, mod, and difficulty in-game and creates a list if one doesn't exist.
- Auto-syncs any changes made in Grim Locations back to the game and visa versa.
- Create, update, and delete custom character profile, mod, and difficulty categories.
- Import any external locations list into Grim Locations.

<img src="https://i.imgur.com/PAIE2zr.png" width="672" height="378" />

### Release 1.0

- The top buttons have been removed as all active profile, mod, and difficulty features are now automated.

- Added a new Editor screen where profile, mods, and difficulties can be created, removed, and edited.
- GlockenGerda provided a [v1.99f](https://mega.nz/folder/eCZhVCbB#SiSgP5o_mnwQLmgP9C6EEA) of Grim Internals to help with the automation features.

### Release 0.3.0

- Grim Dawn folders are now autodetected when starting Grim Locations for the first time.

- Moved **Load Locations to Profile** out of the **Select Active** screen and gave it its own screen.
- Removed the **Sync Active** button. When making changes to your selected active Profile, Mod, and Difficulty they will now automatically sync to Grim Dawn.
- Added the **Location Edit** screen which can be accessed by selecting a location and clicking the edit icon. Changes made to the Location will automatically sync to Grim Dawn if the location is in the active Profile, Mod, and Difficulty.
- GlockenGerda was kind enough to provide a [v1.99e](https://drive.google.com/drive/u/4/folders/1-OJbOAZhdUjhuAOVJnHmgvPQZahabf7I) version of Grim Internals that loads locations every time the player opens the Grim Internals screen in-game. This means that any changes you make to your active Profile, Mod, and Difficulty using Grim Locations will immediately reflect in-game as you are playing. Also, as you save new locations in-game, those will immediately reflect in Grim Locations and can be edited.

### Release 0.2.0

Added all of Kortique's locations and categorized them by act, totem, nemesis, and other.

### Upgrading

To upgrade from one release to another, delete the old version's files and folders then unzip the new version and run the exe file. Do not delete the database located in C:\Users\\{user}\AppData\Local\Grim Locations. The database will be updated automatically when the new version is run.

### Moving to a new computer
Copy the database.db file from C:\Users\\{user}\\AppData\Local\Grim Locations\database.

### Bug Reporting

If you encounter any bugs please report them [at this link](https://github.com/recursivelftr/Grim-Locations/issues) and include the log file.
The logs can be found in GrimLocations/logs/GrimLocations.log.
If the issue happened on a different day please include the logs for that day. e.g. GrimLocations-04-03-2021-1.log.gz

### Tools

Grim Locations is built entirely in Kotlin for the JVM with the new IR backend. It uses Jetpack Compose for Desktop, Kotlin Coroutines, and Kotlin Exposed with a SQLite database.

The architecture of the app is MVI based with a custom framework built around Compose and Exposed in the shared/src/main/kotlin/io/grimlocations/shared/framework folder. The implementations of this framework exist in the other folders on the same level as the framework folder.

### Building
The project runs on Java 15 and is built using Gradle with several custom tasks defined. Currently, it has only been tested on Windows. The createProductionArtifact task runs the required tasks to create a distributable a zip file.
