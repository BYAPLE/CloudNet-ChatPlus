package de.dytanic.cloudnet.ext.chat;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class CloudNetChatPlusPlugin extends JavaPlugin implements Listener {

  private String format;
  
  public void onEnable() {
    this.getConfig().options().copyDefaults(true);
    this.saveConfig();

    this.format = getConfig().getString("format");

    this.getServer().getPluginManager().registerEvents(this, (Plugin)this);

    this.getLogger().log(Level.INFO, "You have loaded CloudNet-ChatPlus. This is not an official build from cloudnet.");
  }
  
  @EventHandler(priority = EventPriority.HIGH)
  public void handleChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();

    IPermissionUser user = CloudNetDriver.getInstance().getPermissionManagement().getUser(player.getUniqueId());

    if (user == null) {
      return;
    }

    IPermissionGroup group = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(user);

    String message = event.getMessage().replace("%", "%%");
    if (player.hasPermission("cloudnet.chat.color")) {
      message = ChatColor.translateAlternateColorCodes('&', message);
    }

    if (ChatColor.stripColor(message).trim().isEmpty()) {
      event.setCancelled(true);
      return;
    }

    String format = this.format
            .replace("%name%", player.getName())
            .replace("%uniqueId%", player.getUniqueId().toString());

    if (group != null) {
      format = ChatColor.translateAlternateColorCodes('&',
              format
                      .replace("%group%", group.getName())
                      .replace("%display%", group.getDisplay())
                      .replace("%prefix%", group.getPrefix())
                      .replace("%suffix%", group.getSuffix())
                      .replace("%color%", group.getColor())
      );

      if(PlaceholderAPI.containsPlaceholders(format)) {
        format = PlaceholderAPI.setPlaceholders(event.getPlayer(), format);
      }
      
    } else {
      format = ChatColor.translateAlternateColorCodes('&',
              format
                      .replace("%group%", "")
                      .replace("%display%", "")
                      .replace("%prefix%", "")
                      .replace("%suffix%", "")
                      .replace("%color%", "")
      );
    }

    event.setFormat(format.replace("%message%", message));
  }
}
