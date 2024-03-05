# Datapack Editor

Small, simple Minecraft server plugin made for editing datapacks in-game.  
This plugin is built for **Spigot 1.19.4** and has **not** been tested with any other version/server software.

# Commands
Note: The main `/datapackeditor` command also has the alias `/de`. This alias is occasionally useful.

`/datapackeditor create [datapack/function] [name]`  
Creates a datapack or function with the specified name.  
Function names must be in the format of `namespace:path`.  
If a function is being created, the editor GUI will automatically open.  

`/datapackeditor edit [functionName]`  
Opens the function with the specified name in the editor GUI.

`/datapackeditor delete [datapack/function] [name]`  
Deletes the function or datapack with the specified name.  
**This is unreversable and does not require extra confirmation!**

`/datapackeditor macros`  
Shows you a list of the available macros.  
Speaking of macros...

# Macros

**Macros** are special commands that can be used in the function editor to do cool stuff.  
They are processed (turned into normal commands) when put into a slot.

Available macros include:  
`*hand` - Imports a command from the command block you are holding. Useful for long commands.  
`*fakechat [player] [message]` - Fakes a chat message using tellraw. The player doesn't have to exist.  
`*fakeserver [message]` - Same as `*fakechat`, but as [Server] instead of a player.  
`*null` - Always returns null. Used to remove commands.  

# Permission Management

Any player who has the permission `datapackeditor` is allowed to use all of the plugin's features.  
It's as simple as that! (Probably too simple.)