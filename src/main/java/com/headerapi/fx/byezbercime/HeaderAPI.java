package com.headerapi.fx.byezbercime;

import com.headerapi.fx.byezbercime.managers.DelayManager;
import com.headerapi.fx.byezbercime.managers.HeaderDownloadManager;
import com.headerapi.fx.byezbercime.provider.HeadProvider;
import com.headerapi.fx.byezbercime.util.PlayerHeads;
import com.headerapi.fx.byezbercime.provider.SkinsGenerator;
import com.headerapi.fx.byezbercime.util.SkinTextures;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HeaderAPI {

    private Map<String, Map<String,SkinTextures>> skinsValidate;
    private Map<File, Long> skinLoadingTexturesValidation;
    private PlayerHeads heads;
    private HeadProvider  headProvider;

    private HeaderDownloadManager headerManager;
    private SkinsGenerator skinsGenerator;
    private HeaderProviderAnalysis analysis;
    private DelayManager delayManager;

    public HeaderAPI() {
        try {

            this.skinLoadingTexturesValidation = new HashMap<>();
            this.skinsValidate = new HashMap<>();
            this.headerManager = new HeaderDownloadManager();
            this.analysis = new HeaderProviderAnalysis();
            this.skinsGenerator = new SkinsGenerator();
            this.delayManager = new DelayManager();
            this.heads = new PlayerHeads();
            this.headProvider = new HeadProvider();

        } catch (NullPointerException e) {
            return;
        } catch (ExceptionInInitializerError e) {
            return;
        }

    }

    public HeadProvider getProvider() {
        return headProvider;
    }

    public PlayerHeads getDefaultCategory() {
        return heads;
    }

    public Map<File, Long> getSkinLoadingTexturesValidation() {
        return skinLoadingTexturesValidation;
    }

    public DelayManager getDelayManager() {
        return delayManager;
    }

    public Map<String, Map<String,SkinTextures>> getSkinsValidate() {
        return skinsValidate;
    }

    public HeaderDownloadManager getHeaderManager() {
        return headerManager;
    }

    public SkinsGenerator getSkinsGenerator() {
        return skinsGenerator;
    }

    public HeaderProviderAnalysis getAnalysis() {
        return analysis;
    }
}
