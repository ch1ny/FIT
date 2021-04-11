package io.dcloud.FIT;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;

public class Texture {

    private Context context;

    public Texture(Activity activity) {
        context = activity;
    }

    public int getPicNum(String from) throws IOException {
        AssetManager asset = context.getAssets();
        String path = "apps/H5B405ED9/www/icon/Style/" + from;
        String[] files = asset.list(path);
        return files.length;
    }

}
