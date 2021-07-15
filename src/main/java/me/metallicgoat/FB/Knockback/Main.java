package me.metallicgoat.FB.Knockback;

import me.metallicgoat.FB.Knockback.Events.Knockback;
import me.metallicgoat.FB.Knockback.Events.KnockbackNoDep;
import me.metallicgoat.FB.Knockback.commands.Cmd;
import me.metallicgoat.FB.Knockback.commands.TabComp;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main extends JavaPlugin {

    private static Main instance;
    private final Server server = getServer();

    public void onEnable() {
        int pluginId = 11753;
        Metrics metrics = new Metrics(this, pluginId);

        instance = this;
        PluginDescriptionFile pdf = this.getDescription();

        log(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );

        loadConfig();
        registerEvents();
        registerCommands();

    }

    private void registerEvents() {
        PluginManager manager = this.server.getPluginManager();

        if(manager.getPlugin("MBedwars") != null &&
                manager.getPlugin("MBedwars").getDescription().getVersion().startsWith("5.")){
            log("MBedwars v5 detected");
            manager.registerEvents(new Knockback(), this);
        }else{
            manager.registerEvents(new KnockbackNoDep(), this);
        }
    }

    private void registerCommands() {
        getCommand("FB-knockback").setExecutor(new Cmd());
        getCommand("FB-knockback").setTabCompleter(new TabComp());
    }

    public static Main getInstance() {
        return instance;
    }

    private void loadConfig(){
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(this, "config.yml", configFile, Arrays.asList("Nothing", "here"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        reloadConfig();
    }

    private void log(String ...args) {
        for(String s : args)
            getLogger().info(s);
    }
}