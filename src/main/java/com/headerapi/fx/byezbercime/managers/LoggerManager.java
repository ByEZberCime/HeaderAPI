package com.headerapi.fx.byezbercime.managers;

import com.headerapi.fx.byezbercime.HeaderAPIClasses;
import org.bukkit.plugin.java.JavaPlugin;

public class LoggerManager {

    private JavaPlugin plugin;

    public LoggerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void info(String message) {
        plugin.getServer().getConsoleSender().sendMessage(color(message));
    }

    public String color(String message) {
        return message.replaceAll("&","§");
    }

    public void broadcast(String s) {
        plugin.getServer().getConsoleSender().sendMessage(color(HeaderAPIClasses.PLUGIN_PREFIX +" "+s));
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

}
