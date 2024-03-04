package zane1117.datapackeditor;

import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditorFunction {
    private NamespacedKey location;
    private String[] commands;
    private World world;

    public EditorFunction(NamespacedKey location, World world) {
        this.commands = new String[36];
        this.location = location;
        this.world = world;
    }

    public static EditorFunction load(NamespacedKey key, World world) throws IOException {
        File worldFolder = world.getWorldFolder();
        File datapackFolder = new File(worldFolder, "datapacks/" + key.getNamespace());
        File functionDirFile = new File(datapackFolder, "data/" + key.getNamespace() + "/functions");
        File functionFile = new File(functionDirFile, "/"+key.getKey() + ".mcfunction");
        if (!functionFile.exists()) {DatapackEditor.LOGGER.log(Level.SEVERE, "Error loading function " + key.toString());return null;}
        FileReader reader = new FileReader(functionFile);
        BufferedReader bReader = new BufferedReader(reader);
        Stream<String> stream = bReader.lines();
        List<String> lineList = stream.collect(Collectors.toList());
        String[] lines = new String[36];
        lineList.toArray(lines);
        bReader.close();
        reader.close();
        return new EditorFunction(key, world).setCommands(lines);
    }

    public void save() throws IOException {
        File worldFolder = world.getWorldFolder();
        File datapackFolder = new File(worldFolder, "datapacks/" + this.location.getNamespace());
        if (!datapackFolder.exists()) {DatapackEditor.LOGGER.log(Level.SEVERE, "Cannot save function " + this.location.toString() + " as the datapack containing it does not exist");}
        File functionDirFile = new File(datapackFolder, "data/" + this.location.getNamespace() + "/functions");
        File functionFile = new File(functionDirFile, "/"+ this.location.getKey() + ".mcfunction");
        if (functionFile.exists()) {functionFile.delete();}
        FileWriter writer = new FileWriter(functionFile);
        StringBuilder finalFunctionBuilder = new StringBuilder();
        for (String command : commands) {
            if (command == null || command == "") {continue;}
            finalFunctionBuilder.append(command).append(System.lineSeparator());
        }
        writer.write(finalFunctionBuilder.toString());
        writer.close();
    }

    public void setLocation(NamespacedKey location) {
        this.location = location;
    }

    public void setCommand(int index, String command) {
        this.commands[index] = command;
    }

    private EditorFunction setCommands(String[] commands) {
        this.commands = commands;
        return this;
    }

    public String getCommand(int index) {
        return this.commands[index];
    }
}
