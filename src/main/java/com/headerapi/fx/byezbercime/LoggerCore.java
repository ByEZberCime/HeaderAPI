package com.headerapi.fx.byezbercime;

import com.headerapi.fx.byezbercime.managers.LoggerManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LoggerCore {

    private JavaPlugin plugin;
    private LoggerManager loggers;

    public LoggerCore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loggers = new LoggerManager(getPlugin());
    }

    public void info(String s) {
        loggers.info(getLoggers().color(s));
    }

    public void broadcast(String s) {
        loggers.broadcast(getLoggers().color(s));
    }

    public LoggerManager getLoggers() {
        return loggers;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
