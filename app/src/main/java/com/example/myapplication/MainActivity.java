package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.insta_test.IGClient;
import com.example.insta_test.models.media.timeline.TimelineMedia;
import com.example.insta_test.requests.feed.FeedTimelineRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class MainActivity extends AppCompatActivity {

    private static IGClient client;
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String BLANK_STRING_KEY = "";
    public static final String WRONG_PAIR = "Key-Value pair cannot be blank or null";
    private static final String SHARED_PREFERENCE_NAME = "INSTASOCIAL";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences savedSession;

    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    SHARED_PREFERENCE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // use the shared preferences and editor as you normally would
            editor = sharedPreferences.edit();
            savedSession = sharedPreferences;


            Scanner scanner = new Scanner(System.in);

// Callable that returns inputted code from System.in


// handler for challenge login
//            IGClient.Builder.LoginHandler challengeHandler = (client, response) -> {
//                // included utility to resolve challenges
//                // may specify retries. default is 3
//                Callable<String> inputCode = () -> {
//                    System.out.print("Please input code: ");
//                    return "074812";
//                };
//                return IGChallengeUtils.resolveChallenge(client, response, inputCode);
//            };


            String internalStorage = getFilesDir().getAbsolutePath();
            String rootPath = internalStorage + "/instaSave";
            File file = new File(rootPath);
            if (!file.exists()) {
                if(file.mkdir()){
                    Log.e(TAG,"Root path Created");
                }
            }
            File clientFile = new File(rootPath, "client");
            File cookieFile = new File(rootPath, "cookie");

            if (clientFile.createNewFile()) {
                Log.e(TAG, "Client File Created");

            }
            if (cookieFile.createNewFile()) {
                Log.e(TAG, "Cookie File Created");
            }

            if (!savedSession.getBoolean("session", false)) {
                client = IGClient.builder()
                        .username("anonymousfriend426@gmail.com")
                        .password("Abc.1234")
//                    .onChallenge(challengeHandler)
                        .login();

                try {
                    client.serialize(clientFile, cookieFile);
                    put("session", true);
                    Log.e(TAG, "Saved in file" + client.getAuthorization());
                } catch (IOException e) {
                    put("session", false);
                    Log.e(TAG, "Got Exception saving client " + e.getMessage());
                }
            } else {
                client = IGClient.deserialize(clientFile, cookieFile);
//                Log.e(TAG, "Static" + client.getAuthorization());
            }


            // Alternatively use client.sendRequest
            new FeedTimelineRequest().execute(client)
                    .thenAccept(response -> {

                        for (TimelineMedia media : response.getFeed_items()) {
                            Log.e(TAG, "\n [ Media" + media.toString() + " ]\n");
                        }
                    })
                    .join();


            File file1 =new File(rootPath+"/four.jpeg");
            if (file1.exists()) {
                Bitmap bitmap=resizeBitmap(file1.getPath(),1080,1347);
                savebitmap(rootPath,bitmap);
                File f = savebitmap(rootPath,bitmap);


                if (f.exists()) {
                    Log.e(TAG, "File found");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                client.actions()
                                        .timeline()
                                        .uploadPhoto(f, "My fourth pic for Demo")
                                        .thenAccept(response -> {
                                            Log.e(TAG, "Successfully uploaded photo!");
                                        })
                                        .join();
                            }
                            , 1000);
                }
            }else{
                Log.e(TAG,"File not found");
            }
//            Log.e(TAG, "Got " + client.getAuthorization());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Got Exception ");
        } catch (GeneralSecurityException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean put(final String key, final boolean value) {
        if (key == null || key.equals("")) {
            throw new IllegalArgumentException(WRONG_PAIR);
        }
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static Boolean getBoolean(final String key) {
        return savedSession.getBoolean(key, false);
    }


    public Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }


    public static File savebitmap(String rootPath, Bitmap bmp) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(
                rootPath + "/testimage.jpg");
        if (f.exists())
        {
            f.delete();
        }
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }
}