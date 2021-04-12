## Grim Locations

<img src="https://i.imgur.com/KHDboPF.png" width="200" height="200" />

##### Current Version: 0.1.0

Download: [Google Drive](https://drive.google.com/drive/folders/1xpsOa0gtNde1nMqunC3jxWIcMzT3qGoz)

Grim Locations is a tool for Grim Dawn that accompanies and requires the mod [Grim Internals](https://forums.crateentertainment.com/t/tool-grim-internals).
It is currently pre 1.0 but most features are present and it is 

### Features
- Provides transfer, remove, reorder, and edit functionality for all your Grim Internals locations.
- Auto-create profiles for your Grim Dawn characters to easily manage their locations.
- Categorize locations by profile, mod, and difficulty.
- Comes with a massive list of locations out of the box that are easily transferable to any profile.
- Detects new locations while playing Grim Dawn and saves them to the specified active profile.
- Each location saved gets a date and time, this makes it easy to tell where you left off last.
- Load any external location list into any profile.

### How to use
See [this video instruction](https://imgur.com/RUcxB0U) for general use. Other than the general use, it is also recommended to run Grim Locations alongside Grim Dawn. This allows syncing to and from Grim Locations in-game.

###### Capture locations from Grim Dawn to Grim Locations while in-game

1. Start Grim Locations and select an active profile, mod, and difficulty.
   - Optional: Modify the active profile in the editor and click "Sync to Grim Dawn".
2. Start Grim Dawn via the Grim Internals exe file.
3. Choose your character, mod (if using one), and difficulty from the Grim Dawn main screen (this should match the active ones chosen in Grim Locations).
4. Start the game, when you open Grim Internals you should see that your locations match that of the active profile in Grim Locations.
5. Save a location via Grim Internals and alt-tab over to Grim Locations. You should see that the Grim Dawn status is "Running" and your new location was added to the locations of the active profile, mod, and difficulty.

###### Sync locations from Grim Locations to Grim Dawn (Grim Locations to Grim Dawn)
- From out of game (Grim Dawn is closed):
  1. Select the active profile, mod, and difficulty and make any changes to their locations.
  2. Click "Sync to Grim Dawn".
- From in-game:
    1. Exit to the Grim Dawn main menu.
    2. Alt-tab over to Grim Locations.
    3. Make changes to the locations of the active profile, mod, and difficulty or select new actives.
    4. Click "Sync Active to Grim Dawn".
    5. Alt-tab back to Grim Dawn and start the game with your selected active character.
    
##### Loading mod character profiles
1. From in Grim Locations, click the settings or gear icon in the top right.
2. Change the "GD Save Folder" to the folder containing your characters for the mod and click OK.
3. On the main Grim Locations screen click "Load Character Profiles".
    - Note that this button can be clicked multiple times without worry. Grim Locations does not allow duplicates and it will not override existing profiles.

### Road to 1.0
- [ ] Remove Sync Active to Grim Dawn button. If the active is changed it will auto sync.
- [ ] Add Profile management screen.
- [ ] Add Mod management screen.
- [ ] Add Difficulty management screen.
- [ ] Add edit screen for Locations. (Will show coordinates as display only)
- [ ] Import Kortique's location list and create corresponding internal profiles
- [ ] Improve splash screen

### Post 1.0
- Look at ways to improve the UI
- Add "Saved Game Mode" that rearranges the last save to the top and provides an indicator that will show in Grim Internals
  - Dependent on the Grim Internals developer to make the change where the location list is read every time Grim Internals is opened rather than when the character enters the game

### Bug Reporting

If you encounter any bugs please report them [at this link](https://github.com/recursivelftr/Grim-Locations/issues) and include the log file.
The logs can be found in GrimLocations/logs/GrimLocations.log.
If the issue happened on a different day please include the logs for that day. e.g. GrimLocations-04-03-2021-1.log.gz

### Tools

Grim Locations is built entirely in Kotlin for the JVM with the new IR backend. It uses Jetpack Compose for Desktop, Kotlin Coroutines, and Kotlin Exposed with a SQLite database.

The architecture of the app is MVI based with a custom framework built around Compose and Exposed in the shared/src/main/kotlin/io/grimlocations/shared/framework folder. The implementations of this framework exist in the other folders on the same level as the framework folder.

### Building
The project runs on Java 15 and is built using Gradle with several custom tasks defined. Currently, it has only been tested on Windows. The createProductionBundle task runs the required tasks to create a distributable a zip file. For this to work [lessmsi](https://lessmsi.activescott.com/) must be downloaded and its root directory put on the system's PATH environment variable.

If an error occurs when importing the project into IntelliJ run 'gradlew clean && gradlew build' form the project root, then refresh the gradle project in IntelliJ.

If a "Cannot expand ZIP 'projectRoot/build/wixToolset/wix311.zip' as it is not a file." error occurs when building the project, go to root/build/wixToolset/wix311.zip (yes this is a folder), and place the wix311-binaries.zip from within /wix311.zip into /wixToolset. Then delete the wix311.zip folder and rename wix311-binaries.zip to wix311.zip. Run the build again and it will pick up the correct file.
