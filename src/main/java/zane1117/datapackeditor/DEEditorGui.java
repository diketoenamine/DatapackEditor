package zane1117.datapackeditor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.persistence.PersistentDataType;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DEEditorGui implements Listener {
    private final Inventory inv;
    public static NamespacedKey BUTTON_TYPE = new NamespacedKey("datapackeditor", "button_type");
    private EditorFunction loadedFunction;
    private boolean acceptingCommandInput;
    private int acceptingIndex;

    public DEEditorGui(EditorFunction function) {
        this.inv = Bukkit.createInventory(null, 54, "Function Editor");
        initializeSlots();
        this.loadFunction(function);
        this.acceptingCommandInput = false;
    }

    private void initializeSlots() {

        for (int i = 0; i < 8;i++) {
            inv.setItem(i, wall());
        }
        for (int i = 45; i < 54;i++) {
            inv.setItem(i, wall());
        }
        // 47 Discard, 51 Save (8 Tips?)
        inv.setItem(47, discardButton());
        inv.setItem(51, saveButton());
    }

    @EventHandler
    public void inventoryClickEvent(final InventoryClickEvent e) {
        if (!(e.getInventory().equals(inv))) {return;}
        if (e.getSlot() > 53) {return;}
        e.setCancelled(true);
        switch (Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getPersistentDataContainer().get(BUTTON_TYPE, PersistentDataType.STRING))) { // What is this abomination
            case "WALL":
                return; // Do nothing
            case "DISCARD":
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage(DatapackEditor.buildMessage("Your changes have been discarded."));
                break;
            case "SAVE":
                e.getWhoClicked().closeInventory();
                try {
                    this.loadedFunction.save();
                    e.getWhoClicked().sendMessage(DatapackEditor.buildMessage("The function has been saved."));
                }catch (IOException exception) {
                    e.getWhoClicked().sendMessage(DatapackEditor.buildMessage("There was an error saving the function, sorry"));
                }
                break;

            case "COMMAND":
                this.beginCommandInput(e.getSlot()-9, e.getWhoClicked());
                break;
        }
    }

    public void loadFunction(EditorFunction function) {
        this.loadedFunction = function;
        this.refreshCommandSlots();
    }

    private void refreshCommandSlots() {
        for (int i = 9; i < 45;i++) {
            inv.setItem(i, this.commandSlot(i-9));
        }
    }

    private void beginCommandInput(int index, HumanEntity plr) {
        plr.closeInventory();
        plr.sendMessage(DatapackEditor.buildMessage("Enter a command in chat. (without the slash!)"));
        this.acceptingCommandInput = true;
        this.acceptingIndex = index;
        DatapackEditor.guisAcceptingInput.put(plr, this);
    }

    public void acceptCommandInput(String input,HumanEntity plr) {
        if (input.startsWith("*")) { // Macro Processing
            String asteriskless = input.substring(1);
            String[] splits = asteriskless.split(" ");
            String macro = splits[0];
            List<String> argsList = new ArrayList<>(Arrays.asList(splits));
            argsList.remove(0);
            String[] args = argsList.toArray(new String[]{});
            String processedCommand = DatapackEditor.processMacro(macro, args, plr);
            if (Objects.equals(processedCommand, "*MACRO_FAIL")) {plr.sendMessage(DatapackEditor.buildMessage("The macro processing failed."));processedCommand = null;}
            this.loadedFunction.setCommand(this.acceptingIndex, processedCommand);
        } else {
            this.loadedFunction.setCommand(this.acceptingIndex, input);
        }
        plr.sendMessage(DatapackEditor.buildMessage("Command set successfully."));
        DatapackEditor.guisAcceptingInput.remove(plr);
        this.refreshCommandSlots();
        this.openGui((Player) plr);
    }

    public void openGui(Player plr) {
        plr.openInventory(inv);
    }

    private static ItemStack wall() {
        final ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        final ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(" ");
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, "WALL");
        stack.setItemMeta(meta);
        return stack;
    }

    private static ItemStack saveButton() {
        final ItemStack stack = new ItemStack(Material.GREEN_CONCRETE);
        final ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "Save Function");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "Saves your function to the world.", ChatColor.WHITE + "It will be available when you reload."));
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, "SAVE");
        stack.setItemMeta(meta);
        return stack;
    }

    private static ItemStack discardButton() {
        final ItemStack stack = new ItemStack(Material.RED_CONCRETE);
        final ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.RED + "Discard Function");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "Discards the function you've made and closes this GUI."));
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, "DISCARD");
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack commandSlot(int index) {
        String command = this.loadedFunction.getCommand(index);
        Material material;
        String name;
        if (command == null || command.equals("")) {
            material = Material.WHITE_CONCRETE;
            name = "Empty Command Slot";
        } else {
            material = Material.LIGHT_BLUE_CONCRETE;
            name = command;
        }
        final ItemStack stack = new ItemStack(material);
        final ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET + name);
        meta.setLore(Arrays.asList(ChatColor.RESET + "" + ChatColor.WHITE + "Click to change the command in this slot."));
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, "COMMAND");
        stack.setItemMeta(meta);
        return stack;
    }
}
