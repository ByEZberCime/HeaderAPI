package com.headerapi.fx.byezbercime.provider;

import com.headerapi.fx.byezbercime.util.PlayerHeads;

import java.util.HashMap;
import java.util.Map;

public class HeadProvider {

    protected static Map<String, Map<String, PlayerHeads>> headerProviders;

    public static void registerProvider(String id, Map<String, PlayerHeads> texuresProperties) {
        if (texuresProperties != null && !texuresProperties.isEmpty()) {
            headerProviders.put(id, texuresProperties);
        }
    }

    public PlayerHeads matchCustomHeadName(String namespaceId) {
        PlayerHeads playerHeads = null;

        String[] modelAndId = namespaceId.split(":");
        String id = modelAndId[0];
        String model = modelAndId[1];

        if (headerProviders.containsKey(id)) {
            if (headerProviders.get(id).containsKey(model)) {
                playerHeads = headerProviders.get(id).get(model);
            }
        }

        return playerHeads;
    }

    public HeadProvider() {
        headerProviders = new HashMap<>();
    }

    public Map<String, Map<String, PlayerHeads>> getHeaderProviders() {
        return headerProviders;
    }
}
