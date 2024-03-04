package zane1117.datapackeditor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatEventListener implements Listener {
    @EventHandler
    public void onChat(PlayerChatEvent e) {
        if (DatapackEditor.guisAcceptingInput.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            Player plr = e.getPlayer();
            DEEditorGui gui = DatapackEditor.guisAcceptingInput.get(e.getPlayer());
            gui.acceptCommandInput(e.getMessage(), plr);
        }
    }
}
