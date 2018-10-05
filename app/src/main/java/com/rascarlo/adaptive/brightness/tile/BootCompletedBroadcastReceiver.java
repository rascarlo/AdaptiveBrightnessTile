package com.rascarlo.adaptive.brightness.tile;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                AdaptiveBrightnessTileService.requestListeningState(context, new ComponentName(context, AdaptiveBrightnessTileService.class));
            }
        }
    }
}
