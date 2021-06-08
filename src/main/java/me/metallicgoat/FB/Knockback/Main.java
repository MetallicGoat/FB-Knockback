package me.metallicgoat.FB.Knockback;

import me.metallicgoat.FB.Knockback.Events.Knockback;
import me.metallicgoat.FB.Knockback.commands.cmd;
import me.metallicgoat.FB.Knockback.commands.tabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private final Server server = getServer();

    public void onEnable() {
        registerEvents();
        registerCommands();
        instance = this;
        PluginDescriptionFile pdf = this.getDescription();

        log(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    private void registerEvents() {
        PluginManager manager = this.server.getPluginManager();
        manager.registerEvents(new Knockback(), this);
    }

    private void registerCommands() {
        getCommand("FB-knockback").setExecutor(new cmd());
        getCommand("FB-knockback").setTabCompleter(new tabCompleter());
    }

    public static Main getInstance() {
        return instance;
    }

    public ConsoleCommandSender getConsole() {
        return console;
    }

    private void log(String ...args) {
        for(String s : args)
            getLogger().info(s);
    }
}