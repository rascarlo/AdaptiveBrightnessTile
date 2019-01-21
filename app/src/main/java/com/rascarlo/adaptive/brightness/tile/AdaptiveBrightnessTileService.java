package com.rascarlo.adaptive.brightness.tile;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.appcompat.app.AlertDialog.Builder;

public class AdaptiveBrightnessTileService extends TileService {

    private static final int PERMISSION_DIALOG = 42;
    private static final int SETTING_NOT_FOUND_DIALOG = 24;

    @Override
    public void onCreate() {
        requestListeningState(this, new ComponentName(this, getClass()));
        AdaptiveBrightnessTileJobService.scheduleUpdateJob(this);
        super.onCreate();
    }

    @Override
    public void onStartListening() {
        updateTileResources();
        super.onStartListening();
    }

    @Override
    public void onTileRemoved() {
        AdaptiveBrightnessTileJobService.cancelJob(this);
        super.onTileRemoved();
    }

    @Override
    public void onClick() {
        if (Settings.System.canWrite(this)) {
            changeBrightnessMode();
        } else {
            showDialog(PERMISSION_DIALOG);
        }
        super.onClick();
    }

    private void updateTileResources() {
        if (this.getQsTile() != null) {
            Tile tile = this.getQsTile();
            tile.setLabel(getString(R.string.adaptive_brightness));
            try {
                if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_brightness_auto_white_24dp));
                    tile.setState(Tile.STATE_ACTIVE);
                } else {
                    tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_brightness_auto_off_white_24dp));
                    tile.setState(Tile.STATE_INACTIVE);
                }
            } catch (Settings.SettingNotFoundException e) {
                tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_brightness_auto_white_24dp));
                tile.setState(Tile.STATE_INACTIVE);
            }
            tile.updateTile();
        }
    }

    private void changeBrightnessMode() {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)
                            == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ?
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL :
                            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } catch (Settings.SettingNotFoundException e) {
            showDialog(SETTING_NOT_FOUND_DIALOG);
        }
    }

    private void showDialog(int whichDialog) {
        Builder builder = new Builder(this, R.style.AppTheme_AlertDialog);
        builder.setCancelable(true)
                .setIcon(R.drawable.ic_brightness_auto_white_24dp)
                .setTitle(R.string.app_name)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        switch (whichDialog) {
            case PERMISSION_DIALOG:
                builder.setMessage(R.string.permission_alert_dialog_message);
                builder.setPositiveButton(R.string.settings, (dialog, which) ->
                        startActivityAndCollapse(new Intent(getApplicationContext(), MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
                break;
            case SETTING_NOT_FOUND_DIALOG:
                builder.setMessage(R.string.setting_not_found_alert_dialog_message);
                builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel());
                break;
        }
        showDialog(builder.create());
    }
}