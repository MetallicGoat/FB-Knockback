package me.metallicgoat.FB.Knockback.commands;

import me.metallicgoat.FB.Knockback.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Main plugin = Main.getInstance();
        if (args.length == 1 && args[0].equalsIgnoreCase("Reload")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("FBknockback.admin")) {
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "[FB-Knockback] Config reloaded!");
                    return true;
                }
            } else {
                plugin.reloadConfig();
                plugin.getLogger().info("[FB-Knockback] Config reloaded!");
                return true;
            }
        }
        return false;
    }
}