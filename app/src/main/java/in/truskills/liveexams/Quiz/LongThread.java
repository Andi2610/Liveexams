package in.truskills.liveexams.Quiz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadPoolExecutor;

public class LongThread implements Runnable {

    int threadNo;
    Handler handler;
    String imageUrl;
    Bitmap bmp;
    String group;
    Context c;
    public static final String TAG = "LongThread";
    SharedPreferences prefs;
    ThreadPoolExecutor executor;

    public LongThread() {
    }

    public LongThread(int threadNo, String imageUrl, Handler handler, String group, Context c, ThreadPoolExecutor executor) {
        this.threadNo = threadNo;
        this.handler = handler;
        this.imageUrl = imageUrl;
        this.group = group;
        this.executor = executor;
        this.c = c;
    }

    @Override
    public void run() {
        Log.i(TAG, "Starting Thread : " + threadNo);
        bmp = getBitmap(imageUrl);
        try {
            savebitmap(bmp, group);
        } catch (Exception e) {
            Log.e("in", "exception:" + e.toString());
            e.printStackTrace();
            return;
        }
        sendMessage(threadNo, "Thread Completed");
        Log.i(TAG, "Thread Completed " + threadNo);
    }

    public static File savebitmap(Bitmap bmp, String gr) throws Exception {
        Log.d("hereeeee", "inSaveBitmap");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        String folder_main = "LiveExams";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        String pp = f.getAbsolutePath();
        File file = new File(pp
                + File.separator + gr);
        file.createNewFile();
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }

    public void sendMessage(int what, String msg) {

        Message message = handler.obtainMessage(what, msg);
        message.sendToTarget();
    }

    private Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(url).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);


            // Do extra processing with the bitmap
        } catch (Exception e) {
            Log.e("in", "exception:" + e.toString());
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

}

