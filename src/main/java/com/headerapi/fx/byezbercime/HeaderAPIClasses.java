package com.headerapi.fx.byezbercime;

import com.headerapi.fx.byezbercime.managers.ConfigurationManager;
import com.headerapi.fx.byezbercime.util.PlayerHeads;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class HeaderAPIClasses extends JavaPlugin {

    public static final String PLUGIN_PREFIX = "&b[&r  &eHeaderAPI  &b]&r";
    public static final String SKIN_ZIP = "header_generate.zip";
    public static final String SKIN_DOWNLOAD_URL = "ZIP_LINK";

    private static HeaderAPI headerAPI;

    private ConfigurationManager configuration;

    private static HeaderAPIClasses instance;
    private LoggerCore core;

    private File skinsDirectory;
    private File generateDirectory;
    private File registerDirectory;

    /*
     * First downloading
     * 1 step download to skinpack.zip to generated location
     * 2 step to .zip file is uzipping to skins location
     * 3 step load to .png file and added to .json to skins location
     * 4 step .json files added to unzipped files name is copied to .json files zipped all
     * 5 step to .zipped all json file i got unzip to register location
     * 6 step all register location .json file data i got read and add to localsession
     * 7 step all skins .png and .json i got delete to skins location
     * Task finish.
     * */

    @Override
    public void onLoad() {

        instance = this;
        this.configuration = new ConfigurationManager(this);
        this.core = new LoggerCore(this);
        this.skinsDirectory = new File(getDataFolder(), "skins");
        this.generateDirectory = new File(getDataFolder(), "generated");
        this.registerDirectory = new File(getDataFolder(), "register");

        headerAPI = new HeaderAPI();

        files();


    }

    private void loadHeaderGeneratorSkinsPack() {
        try {

            long startTime = System.currentTimeMillis();
            long skinLoadingTimeScheduler = 0L;

            File generateZip = new File(getDataDirectory(), SKIN_ZIP);
            File skinGenerateZip = new File(getSkinsDirectory(), SKIN_ZIP);

            if (getHeaderAPI().getAnalysis().isDownloadHeadsPack(skinGenerateZip,generateZip)) {
                getCore().broadcast(String.format("&7The &e%s &7is loading... &c%sms.", generateZip.getName(), (System.currentTimeMillis() - startTime)));

                Thread.sleep(2000L);

                if (skinGenerateZip.getParentFile().isDirectory() && skinGenerateZip.exists()) {
                    getHeaderAPI().getHeaderManager().registerUNZIP(skinGenerateZip);
                }

                Thread.sleep(5000L);

                getHeaderAPI().getSkinsGenerator().readSkinsData(skinGenerateZip);

                Thread.sleep(10000L);

                getCore().broadcast(String.format("&7The &e%s &7is success loaded, &c%sms.", generateZip.getName(), (System.currentTimeMillis() - startTime)));

                Thread.sleep(1025L);

            } else {

                getHeaderAPI().getHeaderManager().clearData(skinGenerateZip, generateZip, getSkinsDirectory(), getRegisterDirectory());

                getCore().broadcast(String.format("&7The &e%s &7is downloading...", generateZip.getName()));

                Thread.sleep(1000L);

                if (generateZip != null && generateZip.getParentFile().isDirectory() && !generateZip.exists() && SKIN_ZIP.equals(generateZip.getName())) {
                    getHeaderAPI().getHeaderManager().downloadZipEntry(getDataDirectory(), new URL(SKIN_DOWNLOAD_URL));
                }

                if (!getHeaderAPI().getDelayManager().putDownloadingDelay(generateZip, skinLoadingTimeScheduler)) {
                    getCore().broadcast("&cGenerate zip is not loading error...");
                    getCore().broadcast(String.format("&cFailed to download &e%s.", generateZip.getName()));
                    return;
                }

                getCore().broadcast(String.format("&7The &e%s &7is downloaded, &c%sms.", generateZip.getName(), (System.currentTimeMillis() - startTime)));
                getCore().broadcast(String.format("&7The &e%s &7is loading...", generateZip.getName()));

                Thread.sleep(2000L);

                long data = getHeaderAPI().getDelayManager().getLong(generateZip);
                data += 700;

                if (generateZip.exists()) {
                    getHeaderAPI().getHeaderManager().generatedZipConverter(getSkinsDirectory(), generateZip, data);
                }

                Thread.sleep(1000L);

                getHeaderAPI().getSkinsGenerator().loadSkins();

                long delay = getHeaderAPI().getDelayManager().getLong(generateZip);
                delay += 500L;
                Thread.sleep(delay);

                getCore().broadcast(String.format("&7The &e%s &7is zipping... &c%sms", generateZip.getName(), (System.currentTimeMillis() - startTime)));

                Thread.sleep(4000L);

                getHeaderAPI().getHeaderManager().generateZIP(getSkinsDirectory(), skinGenerateZip);

                Thread.sleep(2000L);

                if (skinGenerateZip.getParentFile().isDirectory() && skinGenerateZip.exists()) {
                    getHeaderAPI().getHeaderManager().registerUNZIP(skinGenerateZip);
                }

                Thread.sleep(5000L);

                getHeaderAPI().getSkinsGenerator().readSkinsData(skinGenerateZip);

                Thread.sleep(10000L);

                List<File> skinsJsonsFiles = Arrays.stream(getSkinsDirectory().listFiles()).toList().stream().filter(a -> a.getName().endsWith(".json")).toList();
                List<File> skinsPNGsFiles = Arrays.stream(getSkinsDirectory().listFiles()).toList().stream().filter(a -> a.getName().endsWith(".png")).toList();

                skinsPNGsFiles.stream().forEach(a -> {
                    a.delete();
                    a.deleteOnExit();
                });

                skinsJsonsFiles.stream().forEach(a -> {
                    a.delete();
                    a.deleteOnExit();
                });
                if (getHeaderAPI().getSkinLoadingTexturesValidation().containsKey(generateZip)) {
                    getHeaderAPI().getSkinLoadingTexturesValidation().remove(generateZip);
                }

                getCore().broadcast(String.format("&7The &e%s &7is success loaded, &c%sms.", generateZip.getName(), (System.currentTimeMillis() - startTime)));

            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
public void onEnable() {

        getHeaderAPI().getSkinsGenerator().registerAPI();

        /*
         *
         * If is not arrange to key is not download firstpack.
         *
         * */

        if (getHeaderAPI().getAnalysis().isAPIKey()) {

            loadHeaderGeneratorSkinsPack();

            Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

                //DEBUG MODE

                @EventHandler
                public void onPlayerJoin(PlayerJoinEvent event) {

                    for (Map.Entry<String, Map<String, PlayerHeads>> entry : getHeaderAPI().getProvider().getHeaderProviders().entrySet()) {
                        for (Map.Entry<String, PlayerHeads> heads : entry.getValue().entrySet()) {
                            PlayerHeads playerHeads = getHeaderAPI().getProvider().matchCustomHeadName(entry.getKey() +":" + heads.getKey());
                            event.getPlayer().getInventory().addItem(playerHeads.getHead());
                        }
                    }

                }
            }, this);

        } else {

            getCore().broadcast(String.format("&7The &e%s &7plugin is not found to &c%s &7api key...", getDescription().getName(), "https://account.mineskin.org/keys/"));
            getCore().broadcast("&7Please open one more account and generate new key...");
            getCore().broadcast("&7The config.yml paste api key, and try again \n&estart to server");

        }

    }

    @Override
    public void onDisable() {


    }

    private void files() {

        if (!registerDirectory.exists()) {
            registerDirectory.mkdirs();
        }

        if (!skinsDirectory.isDirectory()) {
            skinsDirectory.mkdirs();
            getCore().broadcast("&7The 'skins' folders has been loaded!");
        }

        if (!generateDirectory.isDirectory()) {
            generateDirectory.mkdirs();
            getCore().broadcast("&7The 'generated' folders has been loaded!");
        }

    }

    public File getDataDirectory() {
        return generateDirectory;
    }

    public File getSkinsDirectory() {
        return skinsDirectory;
    }

    public LoggerCore getCore() {
        return core;
    }

    public static HeaderAPIClasses getInstance() {
        return instance;
    }

    public ConfigurationManager getConfiguration() {
        return configuration;
    }

    public static HeaderAPI getHeaderAPI() {
        return headerAPI;
    }

    public File getRegisterDirectory() {
        return registerDirectory;
    }
}
