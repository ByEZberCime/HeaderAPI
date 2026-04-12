package com.headerapi.fx.byezbercime;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.headerapi.fx.byezbercime.managers.HeaderDownloadManager;
import com.headerapi.fx.byezbercime.provider.SkinsGenerator;
import com.headerapi.fx.byezbercime.util.SkinTextures;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineskin.data.Skin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HeaderAPIClasses extends JavaPlugin {

    public static final String PLUGIN_PREFIX = "&b[&r  &eHeaderAPI  &b]&r";
    public static final String MINESKIN_API = "API_KEY";
    public static final String SKIN_ZIP = "header_generate.zip";
    public static final String SKIN_DOWNLOAD_URL = "HEADER_GENERATE.DOWNLOAD.ZIP";

    private static HeaderAPIClasses instance;
    private LoggerCore core;

    private Map<String, SkinTextures> skinsValidate;

    private HeaderDownloadManager headerManager;
    private SkinsGenerator skinsGenerator;

    private File skinsDirectory;
    private File dataDirectory;

    @Override
    public void onLoad() {

        instance = this;
        this.core = new LoggerCore(this);
        this.skinsGenerator =  new SkinsGenerator();
        this.skinsDirectory = new File(getDataFolder(), "skins");
        this.dataDirectory = new File(getDataFolder(), "generated");
        this.headerManager = new HeaderDownloadManager();

        files();

        headerManager.loadHeads();

    }

    @Override
    public void onEnable() {
        this.skinsValidate = new HashMap<>();

        getSkinsGenerator().loadSkins();

    }

    private void files() {

        if (!skinsDirectory.isDirectory()) {
            skinsDirectory.mkdirs();
            getCore().broadcast("&7The 'skins' folders has been loaded!");
        }

        if (!dataDirectory.isDirectory()) {
            dataDirectory.mkdirs();
            getCore().broadcast("&7The 'generated' folders has been loaded!");
        }

    }

    @Override
    public void onDisable() {



    }

    public HeaderDownloadManager getHeaderManager() {
        return headerManager;
    }

    public File getDataDirectory() {
        return dataDirectory;
    }

    public File getSkinsDirectory() {
        return skinsDirectory;
    }

    public Map<String, SkinTextures> getSkinsValidate() {
        return skinsValidate;
    }

    public LoggerCore getCore() {
        return core;
    }

    public static HeaderAPIClasses getInstance() {
        return instance;
    }

    public SkinsGenerator getSkinsGenerator() {
        return skinsGenerator;
    }
}
