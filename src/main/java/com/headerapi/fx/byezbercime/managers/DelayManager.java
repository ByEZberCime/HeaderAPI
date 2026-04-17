package com.headerapi.fx.byezbercime.managers;

import com.headerapi.fx.byezbercime.HeaderAPIClasses;

import java.io.File;

public class DelayManager {

    public boolean putDownloadingDelay(File generateZIP, Long cooldown) {
        boolean result = false;

        if (generateZIP != null && generateZIP.getParentFile().isDirectory() && generateZIP.exists() && !HeaderAPIClasses.getHeaderAPI().getSkinLoadingTexturesValidation().containsKey(generateZIP)) {
            HeaderAPIClasses.getHeaderAPI().getSkinLoadingTexturesValidation().put(generateZIP, cooldown);
            result = true;
        }
        return result;
    }

    public boolean putIfKeyExists(File generateZIP, Long cooldown) {
        boolean result = false;
        if (generateZIP != null && generateZIP.getParentFile().isDirectory() && generateZIP.exists() && HeaderAPIClasses.getHeaderAPI().getSkinLoadingTexturesValidation().containsKey(generateZIP) && HeaderAPIClasses.getHeaderAPI().getSkinLoadingTexturesValidation().get(generateZIP) != null) {

            HeaderAPIClasses.getHeaderAPI().getSkinLoadingTexturesValidation().replace(generateZIP, cooldown);
            result = true;
        }
        return result;
    }

    public long getLong(File generateZIP) {
        long delay = 0L;
        if (generateZIP != null && generateZIP.getParentFile().isDirectory() && generateZIP.exists() && HeaderAPIClasses.getHeaderAPI().getSkinLoadingTexturesValidation().containsKey(generateZIP) && HeaderAPIClasses.getHeaderAPI().getSkinLoadingTexturesValidation().get(generateZIP) != null) {
            delay = HeaderAPIClasses.getHeaderAPI().getSkinLoadingTexturesValidation().get(generateZIP);
        }
        return delay;
    }

}
