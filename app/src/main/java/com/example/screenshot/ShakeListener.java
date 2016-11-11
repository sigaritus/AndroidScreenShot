package com.example.screenshot;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeListener implements SensorEventListener {

    private OnShakeCallback mOnShakeCallback;

    private SensorManager sensorManager;

    private Sensor sensor;

    private Context mContext;
    
    private int MIN_SHAKE_DURATION = 70;
    
    private long lastShakeTime;

    public ShakeListener(Context context) {

        mContext = context;

        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (sensor != null) {

            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }

    }

    public void setmOnShakeCallback(OnShakeCallback onShakeCallback) {
        mOnShakeCallback = onShakeCallback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentShakeTime = System.currentTimeMillis();
        
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        
        
        if (currentShakeTime - lastShakeTime > MIN_SHAKE_DURATION) {
                
            if (Math.abs(x)>15||Math.abs(y)>15||Math.abs(z)>15) {
                
                Log.i("shake--", x+"------"+y+"-----"+z);
                mOnShakeCallback.onShake();
                
                
            }
            
            
            
        }
        
        lastShakeTime = currentShakeTime;
        
        
        
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    public interface OnShakeCallback {
        void onShake();
    }
    
    public void stop(){
        if (sensorManager!=null) {
            
            sensorManager.unregisterListener(this);
            
        }
    }

}
