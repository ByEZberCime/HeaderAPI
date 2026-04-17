package com.headerapi.fx.byezbercime;

import java.io.File;

public class HeaderProviderAnalysis {

    public boolean isHeaderNamespaceId(String id){
        boolean result = false;

        return result;
    }

    public boolean isDownloadHeadsPack(File skinZIP,File generateZIP) {
        boolean result = false;

        if (skinZIP != null && generateZIP != null && generateZIP.getParentFile().isDirectory() && generateZIP.exists() && skinZIP.getParentFile().isDirectory() && skinZIP.exists()) {
            result = true;
        }

        return result;
    }

    public boolean isAPIKey() {
        boolean isKey = true;

        String key = HeaderAPIClasses.getHeaderAPI().getHeaderManager().getMineSkinAPIKey();

        if (
                (key != null && !key.isEmpty() && key.equals("key_is_not_valid")) ||
                        (key != null && !key.isEmpty() && key.equals("api-key"))) {

            isKey = false;

        }

        return isKey;
    }
}

