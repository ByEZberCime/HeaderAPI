package com.headerapi.fx.byezbercime.managers;

import com.headerapi.fx.byezbercime.HeaderAPIClasses;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class HeaderDownloadManager {

    private String MINESKIN_API = "api-key";

    public HeaderDownloadManager() {

        if (HeaderAPIClasses.getInstance().getConfiguration().getHeaderConfigurations().containsKey("mineskin_api")) {
            this.MINESKIN_API = (String) HeaderAPIClasses.getInstance().getConfiguration().getHeaderConfigurations().get("mineskin_api");
        }

    }

    public void downloadZipEntry(File to, URL downloadURL) {
        try {
            Thread.sleep(1000L);

            if (downloadURL != null) {

                try {
                    InputStream inputStream = downloadURL.openStream();
                    OutputStream fileStream = null;
                    File generateFile = new File(downloadURL.toURI().getPath());

                    File targetFile = new File(to, generateFile.getName());

                    if (!targetFile.isDirectory() && !targetFile.exists()) {

                        fileStream = new FileOutputStream(targetFile);
                        byte[] buffer = new byte[1024];
                        int length;

                        while ((length = inputStream.read(buffer)) != -1) {
                            fileStream.write(buffer, 0, length);
                        }
                        fileStream.flush();

                    }

                    fileStream.close();
                    inputStream.close();

                    fileStream = null;
                    inputStream = null;
                    downloadURL.openStream().close();
                    downloadURL = null;
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

    public void generatedZipConverter(File to, File generatedFile, long threadZIPPINGDelay) {
        try {
            Thread.sleep(1000L);

            try {
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(generatedFile));
                ZipEntry generatedEntryZip = zipInputStream.getNextEntry();
                FileOutputStream fos = null;

                String generateName = generatedFile.getName();
                String GENERATE_NAME = generateName.substring(0, generateName.length() - 4);

                if (generatedEntryZip != null) {
                    while (generatedEntryZip != null) {

                        File skinFile = new File(to, generatedEntryZip.getName());
                        skinFile.getParentFile().mkdirs();

                        fos = new FileOutputStream(skinFile);

                            byte[] buffer = new byte[1024];
                            int len;

                            while ((len = zipInputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.flush();

                        threadZIPPINGDelay += 1000;
                        generatedEntryZip = zipInputStream.getNextEntry();

                    }
                }

                zipInputStream.closeEntry();
                fos.close();

                zipInputStream = null;
                fos = null;

                List<File> pngFilesList = Arrays.stream(to.listFiles()).toList();
                if (!pngFilesList.isEmpty()) {
                    for (File pngs : pngFilesList) {
                        File targetFile = new File(to, GENERATE_NAME + "-" + pngs.getName());

                        pngs.renameTo(targetFile);

                        threadZIPPINGDelay += 200;
                    }
                }

                HeaderAPIClasses.getHeaderAPI().getDelayManager().putIfKeyExists(generatedFile, threadZIPPINGDelay);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateZIP(File directory, File zipFile) {

        ZipOutputStream zipInputStream = null;
        FileInputStream fileInputStream = null;

        try {

            zipInputStream = new ZipOutputStream(new FileOutputStream(zipFile));

            List<File> skinsJsonData = Arrays.asList(directory.listFiles()).stream().filter(a -> a.getName().endsWith(".json")).toList();

            if (skinsJsonData != null && !skinsJsonData.isEmpty()) {
                for (File jsons : skinsJsonData) {

                    fileInputStream = new FileInputStream(jsons);
                    ZipEntry zipEntry = new ZipEntry(jsons.getName());
                    zipInputStream.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;

                    while ((len = fileInputStream.read(buffer)) >= 0) {
                        zipInputStream.write(buffer, 0, len);
                    }

                }
            }

            zipInputStream.close();
            fileInputStream.close();

            fileInputStream = null;
            zipInputStream = null;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void registerUNZIP(File zipFile) {

        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            OutputStream outputStream = null;

            if (zipEntry != null) {
                while (zipEntry != null) {

                    File objectFile = new File(HeaderAPIClasses.getInstance().getRegisterDirectory(), zipEntry.getName());

                    objectFile.getParentFile().mkdirs();

                    outputStream = new FileOutputStream(objectFile);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipInputStream.read(buffer)) >= 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.flush();

                    zipEntry = zipInputStream.getNextEntry();
                }
            }

            outputStream.close();
            zipInputStream.closeEntry();

            outputStream = null;
            zipInputStream = null;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void clearData(File skinGenerator, File generateZip, File skinsDirectory, File registerDirectory) {

        if (skinGenerator != null && skinGenerator.getParentFile().isDirectory() && skinGenerator.exists()) {
            skinGenerator.delete();
            HeaderAPIClasses.getInstance().getCore().info(String.format("The cleaned %s file!", generateZip.getName()));
        }
        if (generateZip != null && generateZip.getParentFile().isDirectory() && generateZip.exists()) {
            generateZip.delete();
            HeaderAPIClasses.getInstance().getCore().info(String.format("The cleaned %s file!", generateZip.getName()));
        }
        if (skinsDirectory != null && Arrays.stream(skinsDirectory.listFiles()).toList() != null && !Arrays.stream(skinsDirectory.listFiles()).toList().isEmpty()) {
            List<File> files = Arrays.asList(skinsDirectory.listFiles()).stream().toList();
            for (File file : files) {
                file.delete();
            }
            HeaderAPIClasses.getInstance().getCore().info("The cleaned all skin files!");
        }
        if (registerDirectory != null && Arrays.stream(registerDirectory.listFiles()).toList() != null && !Arrays.stream(registerDirectory.listFiles()).toList().isEmpty()) {
            List<File> files = Arrays.asList(registerDirectory.listFiles()).stream().toList();
            for (File file : files) {
                file.delete();
            }
            HeaderAPIClasses.getInstance().getCore().info("The cleaned all register files!");
        }
    }

    public String getMineSkinAPIKey() {
        if (MINESKIN_API != null && !MINESKIN_API.isEmpty() && HeaderAPIClasses.getInstance().getConfiguration().getHeaderConfigurations().containsKey("mineskin_api")) {
            return MINESKIN_API;
        }
        return "key_is_not_valid";
    }

}
