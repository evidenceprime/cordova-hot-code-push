package com.nordnetab.chcp.main.handler;

import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.webkit.WebViewAssetLoader;

import org.apache.cordova.LOG;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("IOStreamConstructor")
public class CHCPPathHandler implements WebViewAssetLoader.PathHandler {
    public static final String CHCP_PLUGIN_PATH = "chcp/";
    private static final String LOG_TAG = "CHCP";

    @Nullable
    @Override
    public WebResourceResponse handle(@NonNull String path) {
        if (!path.startsWith(CHCP_PLUGIN_PATH)) {
            return null;
        }

        String filePath = path.replaceAll(CHCP_PLUGIN_PATH, "");
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }
        File fileToLoad = new File(filePath);

        LOG.d(LOG_TAG, "Trying to access file: " + fileToLoad.getAbsolutePath());

        if (!fileToLoad.exists()) {
            LOG.e(LOG_TAG, "File does not exist: " + fileToLoad.getAbsolutePath());
            return null;
        }

        String mimeType = "text/html";
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            if (path.endsWith(".js") || path.endsWith(".mjs")) {
                // Make sure JS files get the proper mimetype to support ES modules
                mimeType = "application/javascript";
            } else if (path.endsWith(".wasm")) {
                mimeType = "application/wasm";
            } else {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }

        try {
            InputStream is = new FileInputStream(fileToLoad);
            return new WebResourceResponse(mimeType, null, is);
        } catch (IOException ioException) {
            LOG.e(
                    LOG_TAG,
                    "Error while reading file: " + fileToLoad.getAbsolutePath(),
                    ioException
            );
            return null;
        }
    }
}
