package com.headerapi.fx.byezbercime.util;

public class SkinTextures {

    private String uuidOfString;
    private String name;
    private String signature;
    private String value;
    private String url;

    public SkinTextures(String uuidOfString, String name, String signature, String value, String url) {
        this.uuidOfString = uuidOfString;
        this.name = name;
        this.signature = signature;
        this.value = value;
        this.url = url;
    }

    public SkinTextures(String name, String signature, String value, String url) {
        this.name = name;
        this.signature = signature;
        this.value = value;
        this.url = url;
    }

    public SkinTextures(String signature, String value, String url) {
        this.signature = signature;
        this.value = value;
        this.url = url;
    }

    public String getUuidOfString() {
        return uuidOfString;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public String getValue() {
        return value;
    }

    public String getUrl() {
        return url;
    }
}
