package com.example.screenshot;

import android.app.Application;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

public class ScreenShotApplication extends Application{
    
    private int resultCode;
    private Intent data;
    private MediaProjectionManager mediaProjectionManager;
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

    public MediaProjectionManager getMediaProjectionManager() {
        return mediaProjectionManager;
    }

    public void setMediaProjectionManager(
            MediaProjectionManager mediaProjectionManager) {
        this.mediaProjectionManager = mediaProjectionManager;
    }

    
    
    

}
