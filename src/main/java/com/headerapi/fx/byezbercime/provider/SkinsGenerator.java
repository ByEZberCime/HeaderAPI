package com.headerapi.fx.byezbercime.provider;

import com.google.gson.Gson;
import com.headerapi.fx.byezbercime.HeaderAPIClasses;
import com.headerapi.fx.byezbercime.api.Java11RequestHandler;
import com.headerapi.fx.byezbercime.util.SkinTextures;
import org.mineskin.JsoupRequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.*;
import org.mineskin.exception.MineSkinRequestException;
import org.mineskin.request.GenerateRequest;
import org.mineskin.response.MineSkinResponse;
import org.mineskin.response.QueueResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SkinsGenerator {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private static final MineSkinClient JSOUP = MineSkinClient.builder()
            .requestHandler(JsoupRequestHandler::new)
            .userAgent("MineSkinClient-Jsoup/Tests")
            .apiKey(HeaderAPIClasses.MINESKIN_API)
            .generateExecutor(EXECUTOR)
            .build();
    private static final MineSkinClient JAVA11 = MineSkinClient.builder()
            .requestHandler(Java11RequestHandler::new)
            .userAgent("MineSkinClient-Java11/Tests")
            .apiKey(HeaderAPIClasses.MINESKIN_API)
            .generateExecutor(EXECUTOR)
            .build();

    public void generateSkins(File skinFile) {

        if (skinFile.exists()) {

            long start = System.currentTimeMillis();

            String skin = skinFile.getName();
            String skinName = skin.substring(0, skin.length() - 4);
            GenerateRequest skinFileGenerate = GenerateRequest.upload(skinFile).name(skinName).visibility(Visibility.PUBLIC);
            JAVA11.queue().submit(skinFileGenerate)
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

                        boolean isSuccess = applySkinJsonFile(skinName, skinInfo);

                        if (isSuccess) {
                            HeaderAPIClasses.getInstance().getCore().broadcast("&e" + skin +" &7skin successfully generated");
                        }

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

    public boolean applySkinJsonFile(String skinName, SkinInfo skinData) {

        SkinTextures texturesProperties = new SkinTextures(
                skinData.uuid(),
                skinData.name(),
                skinData.texture().data().signature(),
                skinData.texture().data().value(),
                skinData.texture().url().skin());

        if (!HeaderAPIClasses.getInstance().getSkinsValidate().containsKey(skinName)) {
            HeaderAPIClasses.getInstance().getSkinsValidate().put(skinName,texturesProperties);
        }

        Gson gson = new Gson();
        try {
            gson.toJson(skinData,new FileWriter(HeaderAPIClasses.getInstance().getSkinsDirectory()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
