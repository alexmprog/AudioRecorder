package com.renovavision.audiorecorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioPlayer extends Thread {

    private static final String TAG = AudioPlayer.class.getSimpleName();

    @NonNull
    private WeakReference<Context> weakRefContext;

    @NonNull
    private SampleFrequency sampleFrequency;

    @NonNull
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public AudioPlayer(@NonNull Context context, @NonNull SampleFrequency sampleFrequency) {
        this.weakRefContext = new WeakReference<>(context);
        this.sampleFrequency = sampleFrequency;
    }


    @Override
    public void run() {
        Context context = weakRefContext.get();
        if (context == null) {
            return;
        }

        File file = new File(context.getExternalCacheDir(), Constants.TEMP_FILE_NAME);

        int shortSizeInBytes = Short.SIZE / Byte.SIZE;

        int bufferSizeInBytes = (int) (file.length() / shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes * 2];

        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

            int i = 0;
            while (dataInputStream.available() > 0) {
                audioData[i] = dataInputStream.readShort();
                i++;
            }

            dataInputStream.close();

            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleFrequency.getFrequency(),
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            audioTrack.write(audioData, 0, bufferSizeInBytes);


        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
