package zane1117.datapackeditor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;

public class DECommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {return false;}
        switch (args[0]) {
            case "create":
                switch (args[1]) {
                    case "datapack": {
                        String datapackName = args[2];
                        if (datapackName == null || datapackName == "") {commandSender.sendMessage(DatapackEditor.buildMessage("Please supply a datapack name!")); return true;}
                        if (!(commandSender instanceof Player)) {commandSender.sendMessage(DatapackEditor.buildMessage("Only players can do this... sorry...")); return true;}
                        Player plr = (Player) commandSender;
                        World world = plr.getWorld();
                        try {
                            DatapackEditor.createDatapack(datapackName, world);
                            plr.sendMessage(DatapackEditor.buildMessage("Datapack created successfully."));
                        } catch (IOException e) {
                            plr.sendMessage(DatapackEditor.buildMessage("There was an error making the datapack. Give the following to Zane:"));
                            plr.sendMessage(Arrays.toString(e.getStackTrace()));
                        }
                        break;
                    }
                    case "function": {
                        if (!(commandSender instanceof Player)) {commandSender.sendMessage(DatapackEditor.buildMessage("Only players can do this... sorry...")); return true;}
                        Player plr = (Player) commandSender;
                        String functionName = args[2];
                        if (functionName == null || functionName == "") {plr.sendMessage(DatapackEditor.buildMessage("Please supply a function name!")); return true;}

                        // Parse function resource key (namespace:function)
                        NamespacedKey parsedKey = DatapackEditor.parseStringToKey(functionName);
                        if (parsedKey == null) {plr.sendMessage(DatapackEditor.buildMessage("There was an error parsing the function name. The name should be in the form of " + ChatColor.GOLD + "namespace:function" + ChatColor.RESET + "."));return true;}

                        // Check if datapack exists
                        File worldFolder = plr.getWorld().getWorldFolder();
                        File datapackFolder = new File(worldFolder, "datapacks/" + parsedKey.getNamespace());
                        if (!datapackFolder.exists()) {plr.sendMessage(DatapackEditor.buildMessage("This datapack doesn't exist!!!!"));return true;}

                        // Start editing function
                        DEEditorGui gui = new DEEditorGui(new EditorFunction(parsedKey, plr.getWorld()));
                        DatapackEditor.INSTANCE.registerThing(gui);
                        gui.openGui(plr);
                        plr.sendMessage(DatapackEditor.buildMessage("You are now editing the function " + functionName + "."));
                    }
                }
                break;
            case "edit": {
                String functionName = args[1];
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(DatapackEditor.buildMessage("Only players can do this... sorry..."));
                    return true;
                }
                Player plr = (Player) commandSender;
                if (functionName == null || functionName == "") {
                    plr.sendMessage(DatapackEditor.buildMessage("Please supply a function name!"));
                    return true;
                }

                // Parse function resource key (namespace:function)
                NamespacedKey parsedKey = DatapackEditor.parseStringToKey(functionName);
                if (parsedKey == null) {
                    plr.sendMessage(DatapackEditor.buildMessage("There was an error parsing the function name. The name should be in the form of " + ChatColor.GOLD + "namespace:function" + ChatColor.RESET + "."));
                    return true;
                }

                // Check if datapack exists
                File worldFolder = plr.getWorld().getWorldFolder();
                File datapackFolder = new File(worldFolder, "datapacks/" + parsedKey.getNamespace());
                if (!datapackFolder.exists()) {
                    plr.sendMessage(DatapackEditor.buildMessage("This datapack doesn't exist!!!!"));
                    return true;
                }

                // Load function and check if it exists
                EditorFunction function = null;
                try {
                    function = EditorFunction.load(parsedKey, plr.getWorld());
                } catch (IOException e) {
                    plr.sendMessage(DatapackEditor.buildMessage("There was an error loading the function. Give the following to zane:"));
                    plr.sendMessage(Arrays.toString(e.getStackTrace()));
                    return true;
                }
                if (function == null) {
                    plr.sendMessage(DatapackEditor.buildMessage("This function does not exist."));
                    return true;
                }

                // Start editing function
                DEEditorGui gui = null;

                gui = new DEEditorGui(function);

                DatapackEditor.INSTANCE.registerThing(gui);
                gui.openGui(plr);
                plr.sendMessage(DatapackEditor.buildMessage("You are now editing the function " + functionName + "."));
                break;
            }
            case "delete": { // TODO: add datapack/function deletion
                switch (args[1]) {
                    case "datapack":
                        String datapackName = args[2];
                        if (datapackName == null || datapackName.equals("")) {
                            commandSender.sendMessage(DatapackEditor.buildMessage("Please supply a datapack name!"));
                            return true;
                        }
                        if (!(commandSender instanceof Player)) {
                            commandSender.sendMessage(DatapackEditor.buildMessage("Only players can do this... sorry..."));
                            return true;
                        }
                        Player plr = (Player) commandSender;
                        World world = plr.getWorld();
                        DatapackEditor.deleteDatapack(datapackName, world);
                        plr.sendMessage(DatapackEditor.buildMessage("Datapack deleted successfully."));
                        break;
                    case "function": {
                        String functionName = args[2];
                        if (!(commandSender instanceof Player)) {
                            commandSender.sendMessage(DatapackEditor.buildMessage("Only players can do this... sorry..."));
                            return true;
                        }
                        Player plr2 = (Player) commandSender;
                        if (functionName == null || functionName == "") {
                            plr2.sendMessage(DatapackEditor.buildMessage("Please supply a function name!"));
                            return true;
                        }

                        // Parse function resource key (namespace:function)
                        NamespacedKey parsedKey = DatapackEditor.parseStringToKey(functionName);
                        if (parsedKey == null) {
                            plr2.sendMessage(DatapackEditor.buildMessage("There was an error parsing the function name. The name should be in the form of " + ChatColor.GOLD + "namespace:function" + ChatColor.RESET + "."));
                            return true;
                        }

                        // Check if datapack exists
                        File worldFolder = plr2.getWorld().getWorldFolder();
                        File datapackFolder = new File(worldFolder, "datapacks/" + parsedKey.getNamespace());
                        if (!datapackFolder.exists()) {
                            plr2.sendMessage(DatapackEditor.buildMessage("This datapack doesn't exist!!!!"));
                            return true;
                        }

                        File functionDirFile = new File(datapackFolder, "data/" + parsedKey.getNamespace() + "/functions");
                        File functionFile = new File(functionDirFile, "/"+parsedKey.getKey() + ".mcfunction");
                        if (!functionFile.exists()) {
                            DatapackEditor.LOGGER.log(Level.SEVERE, "Error loading function " + parsedKey.toString());
                            plr2.sendMessage(DatapackEditor.buildMessage("That function doesn't exist."));
                            return true;
                        }
                        boolean result = functionFile.delete();
                        if (result) {
                            plr2.sendMessage(DatapackEditor.buildMessage("The function was deleted sucessfully."));
                        } else {
                            plr2.sendMessage(DatapackEditor.buildMessage("The function could not be deleted."));
                        }
                        return true;
                    }
                }
            }
            case "macros":
                commandSender.sendMessage(DatapackEditor.buildMessage(ChatColor.GOLD + "Macros" + ChatColor.RESET + " are special commands that can be used in the function editor to do cool stuff. They are processed (turned into normal commands) when put into a slot."));
                commandSender.sendMessage(DatapackEditor.buildMessage("Here's a macro list:"));
                commandSender.sendMessage(ChatColor.GOLD + "*hand" + ChatColor.RESET + " - Imports a command from the command block you are holding. Useful for long commands.");
                commandSender.sendMessage(ChatColor.GOLD + "*fakechat <player> <message>" + ChatColor.RESET + " - Fakes a chat message using tellraw.");
                commandSender.sendMessage(ChatColor.GOLD + "*fakeserver <message>" + ChatColor.RESET + " - Same as *fakechat, but as [Server].");
                commandSender.sendMessage(ChatColor.GOLD + "*null" + ChatColor.RESET + " - Always processes into null. Used to remove commands.");
                return true;
            default:
                return false;
        }
        return true;
    }
}
