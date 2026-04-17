package com.headerapi.fx.byezbercime.util;

import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.headerapi.fx.byezbercime.HeaderAPIClasses;
import com.headerapi.fx.byezbercime.provider.HeadProvider;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PlayerHeads {

    private Map<String, PlayerHeads> headerRegistery;

    private UUID headId;
    private String name;
    private String signature;
    private String value;

    public PlayerHeads() {
        this.headerRegistery = new HashMap<>();
    }

    PlayerHeads(UUID headId, String name, String signature, String value) {
        this.headId = headId;
        this.name = name;
        this.signature = signature;
        this.value = value;
    }

    public static String HEADER_API_ID = "header_api_generated:";
    public static String MODEL_ID = null;

    public String getID() {
        return HEADER_API_ID;
    }

    public String getModelID() {
        return MODEL_ID;
    }

    public ItemStack getHead() {
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), getName());
        gameProfile.getProperties().clear();

        gameProfile.getProperties().put("properties", new Property("textures", value, signature));

        ItemStack head = XSkull.createItem().profile(Profileable.of(gameProfile, true)).apply();
        if (head != null) {
            ItemMeta headMeta = head.getItemMeta();
            headMeta.setDisplayName(HeaderAPIClasses.getInstance().getCore().getLoggers().color(String.format("&e%s's &cHead", getName())));
            head.setItemMeta(headMeta);
        }

        return head;
    }

    public void registerHead(String namespace, SkinTextures properties) {
        if (namespace != null && !namespace.isEmpty() && properties != null) {

            String[] idSplitter = namespace.split(":");
            String id = idSplitter[0];
            String model = idSplitter[1];

            setNamespaceID(id, model);
            if (isMAPIId(id)) {

                PlayerHeads head = new PlayerHeads(UUID.randomUUID(), properties.getName(), properties.getSignature(), properties.getValue());
                if (!getHeaderRegistery().containsKey(model) && getHeaderRegistery().get(model) == null) {
                    getHeaderRegistery().put(model, head);
                    HeaderAPIClasses.getInstance().getCore().info(String.format("&e%s:%s &aregistered successfully.", id, model));
                }

            }

        }
    }

    public void registerProviders() {
        if (getHeaderRegistery() != null && !getHeaderRegistery().isEmpty()) {
            HeadProvider.registerProvider(getID().substring(0,getID().length() - 1),getHeaderRegistery());
        }
    }

    public String getNamespace() {
        return HEADER_API_ID + MODEL_ID;
    }

    protected static void setNamespaceID(String api_id, String model_id) {
        HEADER_API_ID = api_id.toLowerCase(Locale.ENGLISH).toLowerCase() + ":";
        MODEL_ID = model_id.toLowerCase(Locale.ENGLISH);
    }

    protected static boolean isMAPIId(String apiId) {
        boolean result = false;

        if (HeaderAPIClasses.getHeaderAPI().getSkinsValidate() != null && !HeaderAPIClasses.getHeaderAPI().getSkinsValidate().isEmpty()) {
            if (HeaderAPIClasses.getHeaderAPI().getSkinsValidate().containsKey(apiId)) {
                result = true;
            }
        }

        return result;
    }


    public PlayerHeads valueOf(String namespaceId) {
        PlayerHeads headUtil = null;

        String[] splitter = namespaceId.split(":");
        String apiId = splitter[0];
        String modelId = splitter[1];

        setNamespaceID(apiId, modelId);

        if (getHeaderRegistery().containsKey(modelId)) {
            if (HeaderAPIClasses.getHeaderAPI().getSkinsValidate().containsKey(apiId)) {



            }
        }

        return headUtil;
    }

    protected Map<String, PlayerHeads> getHeaderRegistery() {
        return headerRegistery;
    }

    protected String getValue() {
        return value;
    }

    protected String getSignature() {
        return signature;
    }

    protected String getName() {
        return name;
    }

    protected UUID getHeadId() {
        return headId;
    }
}
