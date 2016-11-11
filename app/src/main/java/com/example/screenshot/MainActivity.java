package com.example.screenshot;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    TextView mCapture;
    MediaProjectionManager mediaProjectManager;

    private int screenshotRequestCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCapture = (TextView) findViewById(R.id.capture);
        mCapture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isServiceRunning()) {

                    initCaptureUtils();
                    mCapture.setText("关闭");
                } else {
                    if (mCapture.getText().toString().equals("关闭")) {

                        stopService(new Intent(MainActivity.this,
                                ScreenShotService.class));
                        mediaProjectManager = null;
                        mCapture.setText("开启");

                    }
                }

            }

        });

    }

    private boolean isServiceRunning() {

        ActivityManager activityManager = (ActivityManager) getSystemService(
                ACTIVITY_SERVICE);

        for (RunningServiceInfo service : activityManager
                .getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.screenshot.ScreenShotService".equals(service.service.getClassName())) {
                    return true;
            }
        }

        return false;

    }
    
    
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isServiceRunning()) {
            mCapture.setText("关闭");
        }else{
            mCapture.setText("开启");
        }
    }

    private void initCaptureUtils() {
        mediaProjectManager = (MediaProjectionManager) getApplication()
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        startActivityForResult(mediaProjectManager.createScreenCaptureIntent(),
                screenshotRequestCode);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == screenshotRequestCode) {

            if (resultCode == RESULT_OK && data != null) {

                ((ScreenShotApplication) getApplication())
                        .setMediaProjectionManager(mediaProjectManager);

                ((ScreenShotApplication) getApplication())
                        .setResultCode(resultCode);

                ((ScreenShotApplication) getApplication()).setData(data);

                startService(new Intent(this, ScreenShotService.class));
            } else {

                Toast.makeText(this, "您需要允许应用获取权限", Toast.LENGTH_SHORT)
                        .show();
                mCapture.setText("开启");
                mediaProjectManager = null;
            }

        }

    }
}
