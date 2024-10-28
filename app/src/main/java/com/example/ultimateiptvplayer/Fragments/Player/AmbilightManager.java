package com.example.ultimateiptvplayer.Fragments.Player;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;

public class AmbilightManager {
    private boolean isPaused = false;
    private final Object lock = new Object();
    private Thread ambilightThread;
    private final Activity activity;
    private final SurfaceView surfaceView; // View à capturer

    public AmbilightManager(Activity activity, SurfaceView surfaceView) {
        this.activity = activity;
        this.surfaceView = surfaceView;
        startAmbilightThread();
    }

    private void startAmbilightThread() {
        ambilightThread = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    while (isPaused) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }

                capturePartialScreenAndAnalyze();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        ambilightThread.start();
    }

    private void capturePartialScreenAndAnalyze() {
        if (surfaceView.getHolder().getSurface().isValid()) {
            // Définir la région rectangulaire à capturer
            int left = 50;
            int top = 50;
            int width = 200;
            int height = 200;
            Rect captureRect = new Rect(left, top, left + width, top + height);

            final Bitmap bitmap = Bitmap.createBitmap(captureRect.width(), captureRect.height(), Bitmap.Config.ARGB_8888);
            final Handler handler = new Handler(Looper.getMainLooper());

            PixelCopy.request(surfaceView, captureRect, bitmap, copyResult -> {
                if (copyResult == PixelCopy.SUCCESS) {
                    analyzePixels(bitmap);
                }
            }, handler);
        }
    }

    private void analyzePixels(Bitmap bitmap) {
        // Exemple d'analyse de pixels dans la région capturée
        int centerX = bitmap.getWidth() / 2;
        int centerY = bitmap.getHeight() / 2;

        if (centerX < bitmap.getWidth() && centerY < bitmap.getHeight()) {
            int pixel = bitmap.getPixel(centerX, centerY);
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            Log.d("AmbilightManager", "Pixel au centre - R: " + red + ", G: " + green + ", B: " + blue);
        }
    }

    public void pauseAmbilight() {
        synchronized (lock) {
            isPaused = true;
        }
    }

    public void resumeAmbilight() {
        synchronized (lock) {
            isPaused = false;
            lock.notify();
        }
    }

    public void stopAmbilight() {
        ambilightThread.interrupt();
    }
}
