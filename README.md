**What's this?** <br>
A simple and easy to use plugin with which it is possible to configure cubes, which in turn can start a countdown after
being filled with blocks (as well as other actions). The countdown as well as the actions can be fully customized and it
even includes an API to customize the plugin perfectly for yourself (Java knowledge required)

**Compatible Software:** TikFinity <br>
**Compatible Plugins:** DelayedTNT, PlaceholderAPI

**Installation:** <br>
**1.** Download the newest version from Releases <br>
**2.** Drag and Drop the newest release from the Downloads to the plugins folder of your Minecraft-Server <br>
**3.** Start/Restart your server <br>
**4.** You're ready to go!<br>

**How to use the plugin?:** <br>
Setup is very easy! Once the plugin is installed, run the **/cc setup** command and follow the plugin's instructions

**Commands:**
<br> /cubiccountdown **setup** - Starts the setup process in which 5 simple steps must be followed to add a cube (MAIN
COMMAND)
<br> /cubiccountdown **fill MAPNAME BLOCKTYPE1,BLOCKTYPE2,... AMOUNT1,AMOUNT2,...** - Fills the cube with an specific
amount of specific blocks
<br> /cubiccountdown **clear MAPNAME** - Clears the whole area inside the provided cube
<br> /cubiccountdown **cancel MAPNAME** - Cancels a running countdown of a map
<br> /cubiccountdown **delete MAPNAME** - Deletes an existing cube (which was created before with /cc setup)
<br> /cubiccountdown **reload** - Reloads the Config and data files
<br> /cubiccountdown **cubes** - Shows you all the cubes which you already created
<br> /cubiccountdown **help** - Opens the help menu
<br> /cubiccountdown **simulate win/lose/help MAPNAME** - Simulates a win/lose/help
<br> /cubiccountdown **setEntry CONFIGKEY CONFIGVALUE** - With this command you are able to edit config entries easily
without opening a file so it's easier for me to help people out on my discord. **Important:** Don't use this command if
you don't know what you're doing

**PlaceholderAPI Support:**
<br>You want to use the CubicCountdown statistics (like the wins, loses or games played) in another plugin (like a
scoreboard plugin)? Now it is possible! If you're using a plugin which supports the PlaceholderAPI (the PlaceholderAPI
plugin needs to be installed too), you're now able to use these placeholders in it:
<br>
<br>*Show the number of total wins on a specific cube/map:* **%cc_total_win_counter_MAPNAME%**
<br>*Show the number of session wins on a specific cube/map:* **%cc_session_win_counter_MAPNAME%**
<br>*Show the number of total loses on a specific cube/map:* **%cc_total_lose_counter_MAPNAME%**
<br>*Show the number of session loses on a specific cube/map:* **%cc_session_lose_counter_MAPNAME%**
<br>*Show the number of total games played on a specific cube/map:* **%cc_total_games_played_MAPNAME%**
<br>*Show the number of session games played on a specific cube/map:* **%cc_session_games_played_MAPNAME%**
<br>*Show the number of total helps on a specific cube/map:* **%cc_total_help_counter_MAPNAME%**
<br>*Show the number of session helps on a specific cube/map:* **%cc_session_help_counter_MAPNAME%**
<br>*Show the current cube height:* **%cc_current_cube_height_MAPNAME%**
<br>*Show the cube height:* **%cc_cube_height_MAPNAME%**
<br>
<br>**By the way:** If you're using version 1.2.0 or higher, you're also able to use placeholders from other plugins in
the titles of the CubicCountdown plugin

**QAndA:**
<br>**Q:** *Help! The plugin says that I do not have the permission to use the plugin commands!*
<br>**A:** To solve this issue just open your server console and type the following command into the console: **op
yourminecraftusername**
<br>**Q:** *What is the first and second edge?*
<br>**A:** This means the bottom corner and the diagonal top corner. The order doesn't matter
<br>**Q:** *How can I change the title formatting, the sound effects,... of the plugin?*
<br>**A:** This specific settings cannot be changed ingame. To change it, open your minecraft server folder, open the
plugins and after that the CubicCountdown folder. If you open the config.yml you'll see many settings. Change it to your
preferences, save the file, go back ingame and type **/cc reload** into the chat.

**API:**
<br> Currently there's no maven or gradle repository out yet. To get access to the api you need to add the JAR file
from the releases tab to the build path of your project (in your IDEA). After that you're able to proceed:
<br> **COMING SOON**

**Tutorials:**
<br>English Tutorial by TikTokLive with Harry: https://www.youtube.com/watch?v=7I4ENO1Km4Q
<br>Spanish Tutorial by thanitoASMR: https://www.youtube.com/watch?v=CkP_1YHGhW8

**Support:** <br>
You got any wishes or found any bug? Feel free to join my discord or open an issue on this github
page: https://discord.tikmc.de/

**License:** <br>
The source code as well as the JAR file may be used and modified for commercial as well as private
purposes <br>
