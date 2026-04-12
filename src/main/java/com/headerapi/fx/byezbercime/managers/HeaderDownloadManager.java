package com.headerapi.fx.byezbercime.managers;

import com.headerapi.fx.byezbercime.HeaderAPIClasses;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HeaderDownloadManager {

    private Long start;

    public void loadHeads() {
        try {
            Thread.sleep(1000L);

            this.start  = System.currentTimeMillis();

            List<File> skinsFile = Arrays.stream(HeaderAPIClasses.getInstance().getSkinsDirectory().listFiles()).toList();

            if (skinsFile!= null && !skinsFile.isEmpty()) {
                for (File skinFile : skinsFile) {

                    String skinFileName = skinFile.getName();
                    String skinName = skinFileName.substring(0, skinFileName.length() - 4);

                    if (!skinFile.exists()) {
                        downloadZipEntry(HeaderAPIClasses.getInstance().getDataDirectory(),new URL(HeaderAPIClasses.SKIN_DOWNLOAD_URL));
                    } else {
                        HeaderAPIClasses.getInstance().getCore().broadcast("&e" + skinName + " &7skin loaded &8 " + (System.currentTimeMillis()  - start) + " ms.");
                    }

                }
            } else {
                downloadZipEntry(HeaderAPIClasses.getInstance().getDataDirectory(),new URL(HeaderAPIClasses.SKIN_DOWNLOAD_URL));
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void downloadZipEntry(File to,URL downloadURL) {
        try {
            Thread.sleep(1000L);

            if (downloadURL != null) {

                try {
                    InputStream inputStream = downloadURL.openStream();
                    File generateFile = new File(downloadURL.toURI().getPath());

                    if (generateFile.getName().equals(HeaderAPIClasses.SKIN_ZIP)) {
                        File targetFile = new File(to, generateFile.getName());

                        if (!targetFile.isDirectory() && !targetFile.exists()) {
                            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            HeaderAPIClasses.getInstance().getCore().broadcast("&7The " + generateFile.getName() + " loaded &8" + (System.currentTimeMillis() - start) + "ms.");
                        }

                        if (targetFile != null && targetFile.exists()) {
                            generatedZipConverter(HeaderAPIClasses.getInstance().getSkinsDirectory(), targetFile);
                        }

                    }

                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void generatedZipConverter(File to,File generatedFile){
        try {
            Thread.sleep(1000L);

            try {
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(generatedFile));
                ZipEntry generatedEntryZip = zipInputStream.getNextEntry();

                String generateName = generatedFile.getName();
                String GENERATE_NAME = generateName.substring(0, generateName.length() - 4);

                if (generatedEntryZip != null) {
                    while (generatedEntryZip != null) {

                        File skinFile = new File(to,generatedEntryZip.getName());
                        FileOutputStream  outputStream = new FileOutputStream(skinFile);
                        outputStream.write(zipInputStream.readAllBytes());

                        HeaderAPIClasses.getInstance().getCore().broadcast("&e" + generatedEntryZip.getName() + " &7skin file is unconvorted to "+ HeaderAPIClasses.SKIN_ZIP + "&8 " + (System.currentTimeMillis()  - start) + " ms.");
                        generatedEntryZip = zipInputStream.getNextEntry();

                    }
                }

                zipInputStream.closeEntry();
                HeaderAPIClasses.getInstance().getCore().broadcast("&7The skins file is unconvorted to &e"+ HeaderAPIClasses.SKIN_ZIP + "&8 " + (System.currentTimeMillis()  - start) + " ms.");

                List<File> pngFilesList = Arrays.stream(to.listFiles()).toList();
                if (!pngFilesList.isEmpty()) {
                    for (File pngs : pngFilesList) {
                        File targetFile = new File(to,GENERATE_NAME + ":"+pngs.getName());
                        String skinFileName = targetFile.getName();

                        pngs.renameTo(targetFile);

                        String skinName = skinFileName.substring(0, skinFileName.length() - 4);
                        HeaderAPIClasses.getInstance().getCore().broadcast("&e" + skinName + " &7skin loaded &8 " + (System.currentTimeMillis()  - start) + " ms.");

                    }
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
