package com.example.screenshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import com.example.screenshot.ShakeListener.OnShakeCallback;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class ScreenShotService extends Service {
    MediaProjectionManager mediaProjectManager;
    MediaProjection mediaProjection;
    private int screenshotRequestCode = 100;
    ImageReader imageReader;
    VirtualDisplay virtualDisplay;

    private Handler mHandler = new Handler();

    Vibrator mVibrator;
    ShakeListener mShakeListener;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mShakeListener = new ShakeListener(this);

        mVibrator = (Vibrator) getApplication()
                .getSystemService(VIBRATOR_SERVICE);

        mShakeListener.setmOnShakeCallback(new OnShakeCallback() {

            @Override
            public void onShake() {

                getPicture();

                mVibrator.vibrate(new long[] { 500, 300 }, -1);

                Log.i("shake ", "shake it");

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mVibrator.cancel();
                    }
                }, 2000);
            }
        });

        init();
    }

    private void init() {

        mediaProjectManager = ((ScreenShotApplication) getApplication())
                .getMediaProjectionManager();
        int resultCode = ((ScreenShotApplication) getApplication())
                .getResultCode();
        Intent data = ((ScreenShotApplication) getApplication()).getData();

        mediaProjection = mediaProjectManager.getMediaProjection(resultCode,
                data);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        
        Notification.Builder builder = new Notification.Builder(this);

        builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("screenshot")
                .setContentText("screenShotService has started").setWhen(new Date().getTime())
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(this, MainActivity.class), flags));

        startForeground(startId, builder.build());

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("NewApi")
    private void getPicture() {
        // TODO Auto-generated method stub

        DisplayMetrics displayMetrics = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) getSystemService(
                WINDOW_SERVICE);

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        imageReader = ImageReader.newInstance(width, height, 0x1, 2);
        virtualDisplay = mediaProjection.createVirtualDisplay("capture", width,
                height, displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, null);

        imageReader.setOnImageAvailableListener(new OnImageAvailableListener() {

            @Override
            public void onImageAvailable(ImageReader reader) {
                // TODO Auto-generated method stub
                Image image = imageReader.acquireLatestImage();

                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                int rowStride = image.getPlanes()[0].getRowStride();
                int pixelStride = image.getPlanes()[0].getPixelStride();

                int rowPadding = rowStride - pixelStride * image.getWidth();

                Log.i("screenshot",
                        rowStride + "  " + pixelStride + "  " + rowPadding + " "
                                + image.getWidth() + "   " + image.getHeight());

                Bitmap bitmap = Bitmap.createBitmap(
                        image.getWidth() + rowPadding / pixelStride,
                        image.getHeight(), Bitmap.Config.ARGB_8888);

                bitmap.copyPixelsFromBuffer(buffer);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, image.getWidth(),
                        image.getHeight());
                savePicture(bitmap);
                image.close();
                imageReader.close();
            }
        }, mHandler);

    }

    @SuppressLint("NewApi")
    private void savePicture(Bitmap bitmap) {
        try {
            String dirName = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/DCIM/" + "Screenshots/";
            String fileName = dirName + System.currentTimeMillis() + ".jpg";
            File dir = new File(dirName);
            if (!dir.exists()) {

                dir.mkdir();

            }

            Log.i("screenshot", fileName);
            File file = new File(fileName);
            if (!file.exists()) {

                file.createNewFile();

            }
            FileOutputStream fio = new FileOutputStream(file);
            if (fio != null) {
                bitmap.compress(CompressFormat.JPEG, 100, fio);
                fio.flush();
                fio.close();
                Toast.makeText(this, "saved", Toast.LENGTH_LONG).show();
            }

            MediaStore.Images.Media.insertImage(getContentResolver(),
                    file.getAbsolutePath(), fileName, "description");

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(file)));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mShakeListener.stop();
    }

}
