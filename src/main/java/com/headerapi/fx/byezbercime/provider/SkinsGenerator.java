package com.headerapi.fx.byezbercime.provider;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.headerapi.fx.byezbercime.HeaderAPIClasses;
import com.headerapi.fx.byezbercime.api.Java11RequestHandler;
import com.headerapi.fx.byezbercime.util.SkinTextures;
import org.mineskin.JsoupRequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.CodeAndMessage;
import org.mineskin.data.JobInfo;
import org.mineskin.data.Skin;
import org.mineskin.data.Visibility;
import org.mineskin.exception.MineSkinRequestException;
import org.mineskin.request.GenerateRequest;
import org.mineskin.response.MineSkinResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SkinsGenerator {

    protected Executor EXECUTOR = Executors.newSingleThreadExecutor();

    protected MineSkinClient JSOUP = null;
    protected MineSkinClient JAVA = null;

    public SkinsGenerator registerAPI() {

        this.JSOUP = MineSkinClient.builder()
                .requestHandler(JsoupRequestHandler::new)
                .userAgent("MineSkinClient-Jsoup/Tests")
                .apiKey(HeaderAPIClasses.getHeaderAPI().getHeaderManager().getMineSkinAPIKey())
                .generateExecutor(EXECUTOR)
                .build();
        this.JAVA = MineSkinClient.builder()
                .requestHandler(Java11RequestHandler::new)
                .userAgent("MineSkinClient-Java11/Tests")
                .apiKey(HeaderAPIClasses.getHeaderAPI().getHeaderManager().getMineSkinAPIKey())
                .generateExecutor(EXECUTOR)
                .build();

        return this;
    }

    public void generateSkins(File skinFile) {
        if (skinFile.exists()) {

            long start = System.currentTimeMillis();

            String skin = skinFile.getName();
            String skinName = skin.substring(0, skin.length() - 4);
            GenerateRequest skinFileGenerate = GenerateRequest.upload(skinFile).name(skinName).visibility(Visibility.PUBLIC);
            JAVA.queue().submit(skinFileGenerate)
                    .thenCompose(queueResponse -> {
                        JobInfo job = queueResponse.getJob();
                        // wait for job completion
                        return job.waitForCompletion(JSOUP);
                    })
                    .thenCompose(jobResponse -> {
                        // get skin from job or load it from the API
                        return jobResponse.getOrLoadSkin(JSOUP);
                    })
                    .thenAccept(skinInfo -> {
                        // do stuff with the skin

                        if (applySkinJsonFile(skinName, skinInfo));

                    })
                    .exceptionally(throwable -> {
                        throwable.printStackTrace();
                        if (throwable instanceof CompletionException completionException) {
                            throwable = completionException.getCause();
                        }

                        if (throwable instanceof MineSkinRequestException requestException) {
                            // get error details
                            MineSkinResponse<?> response = requestException.getResponse();
                            Optional<CodeAndMessage> detailsOptional = response.getErrorOrMessage();
                            detailsOptional.ifPresent(details -> {
                                System.out.println(details.code() + ": " + details.message());
                            });
                        }
                        return null;
                    });

        }

    }

    public Skin getGenerateSkin(File skinFile) {
        if (!skinFile.exists()) {
            return null;
        }

        Skin skin = null;

        try {
            JsonElement skinFileJsonElement = JsonParser.parseReader(new InputStreamReader(new FileInputStream(skinFile)));
            JsonElement skinUUID = skinFileJsonElement.getAsJsonObject().get("uuid");

            skin = JAVA.skins().get(skinUUID.getAsString()).get().getSkin();

            return skin;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean applySkinJsonFile(String skinName, Skin skinData) {

        Gson gson = new Gson();
        File skinFile = new File(HeaderAPIClasses.getInstance().getSkinsDirectory(), skinName + ".json");

        if (!skinFile.exists()) {
            try {
                skinFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {

            FileWriter writer = new FileWriter(skinFile, StandardCharsets.UTF_8);
            writer.write(gson.toJson(skinData));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    boolean isHeadTextureFormat(List<File> jsons, String fileName) {
        boolean result = false;

        if (fileName == null && !fileName.isEmpty()) {
            return result;
        }

        File skinJsonFile = new File(HeaderAPIClasses.getInstance().getSkinsDirectory(), fileName + ".json");
        File skinPNGFile = new File(HeaderAPIClasses.getInstance().getSkinsDirectory(), fileName + ".png");

        if (skinJsonFile.isDirectory() && skinJsonFile.exists()) {
            if (jsons.contains(skinJsonFile)) {
                result = false;
            }
        } else if (skinPNGFile.isDirectory() && skinPNGFile.exists()) {
            if (!jsons.contains(skinPNGFile)) {
                result = true;
            }
        }

        return result;
    }

    @Deprecated
    public void loadSkins() {

        List<File> skinsFile = Arrays.asList(HeaderAPIClasses.getInstance().getSkinsDirectory().listFiles()).stream().toList();
        List<File> skinsPNGFile = skinsFile.stream().filter(a -> a.getName().endsWith(".png")).toList();
        List<File> skinsJsons = skinsFile.stream().filter(a -> a.getName().endsWith(".json")).toList();

        if (skinsJsons != null && !skinsJsons.isEmpty()) {
            if (skinsPNGFile != null && !skinsPNGFile.isEmpty()) {
                for (File pngs : skinsPNGFile) {

                    String pngName = pngs.getName().substring(0, pngs.getName().length() - 4);

                    if (isHeadTextureFormat(skinsJsons, pngName)) {
                        generateSkins(pngs);
                    }

                }
            }
        } else if (skinsPNGFile != null && !skinsPNGFile.isEmpty()) {
            for (File skinFile : skinsPNGFile) {
                generateSkins(skinFile);
            }
        }


    }

    public void readSkinsData(File idFiles) {

        List<File> skinsFile = Arrays.stream(HeaderAPIClasses.getInstance().getRegisterDirectory().listFiles()).toList().stream().filter(a -> a.getName().endsWith(".json")).toList();
        HashMap<String,SkinTextures> headerProviderMap = new HashMap<>();

        if (skinsFile != null && !skinsFile.isEmpty()) {
            for (File skins : skinsFile) {

                Skin skin = getGenerateSkin(skins);

                String skinFileJsonName = skins.getName().substring(0, skins.getName().length() - 5);
                String[] skinFileSplitter = skinFileJsonName.split("-");
                String skinID = skinFileSplitter[0].toLowerCase(Locale.ENGLISH);
                String skinModel = skinFileSplitter[1].toLowerCase(Locale.ENGLISH);

                String headerNamespace = skinID + ":" + skinModel;

                SkinTextures texturesProerties = new SkinTextures(
                        skin.uuid(),
                        skinModel.toLowerCase(Locale.ENGLISH),
                        skin.texture().data().signature(),
                        skin.texture().data().value(),
                        skin.texture().url().skin()
                );

                if (!headerProviderMap.containsKey(headerNamespace.toLowerCase(Locale.ENGLISH))) {
                    headerProviderMap.put(headerNamespace, texturesProerties);
                    skins.delete();
                    skins.deleteOnExit();
                }

            }
        }

        String IdFilesName = idFiles.getName().substring(0, idFiles.getName().length() - 4);
        String id = IdFilesName.split("-")[0].toLowerCase(Locale.ENGLISH);

        if (!HeaderAPIClasses.getHeaderAPI().getSkinsValidate().containsKey(id)) {
            HeaderAPIClasses.getHeaderAPI().getSkinsValidate().put(id,headerProviderMap);
        }

        if (headerProviderMap != null && !headerProviderMap.isEmpty()) {
            for (Map.Entry<String, SkinTextures> entry : headerProviderMap.entrySet()) {
                HeaderAPIClasses.getHeaderAPI().getDefaultCategory().registerHead(entry.getKey(),entry.getValue());
            }
        }

        HeaderAPIClasses.getHeaderAPI().getDefaultCategory().registerProviders();
    }

    protected MineSkinClient getJAVA() {
        return JAVA;
    }

    protected MineSkinClient getJSOUP() {
        return JSOUP;
    }
}
