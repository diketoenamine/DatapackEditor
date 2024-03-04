package zane1117.datapackeditor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DETabCompletor implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> subcommandList = Arrays.asList("create", "edit", "delete", "macros");
        List<String> typeList = Arrays.asList("datapack", "function");
        switch (args.length) {
            case 1:
                return subcommandList;
            case 2:
                switch (args[0]) {
                    case "edit":
                    case "macros":
                        return new ArrayList<>();
                    default:
                        return typeList;
                }
            default:
                return new ArrayList<>();
        }

    }
}
