package com.renovavision.audiorecorder;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioRecorder extends Thread {

    private static final String TAG = AudioRecorder.class.getSimpleName();

    @NonNull
    private WeakReference<Context> weakRefContext;

    @NonNull
    private SampleFrequency sampleFrequency;

    @NonNull
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public AudioRecorder(@NonNull Context context, @NonNull SampleFrequency sampleFrequency) {
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

        try {

            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            // calculate min buffer size
            int minBufferSize = AudioRecord.getMinBufferSize(sampleFrequency.getFrequency(),
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            short[] audioData = new short[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleFrequency.getFrequency(),
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize);

            audioRecord.startRecording();

            isRunning.set(true);

            while (isRunning.get()) {
                int numberOfByte = audioRecord.read(audioData, 0, minBufferSize);
                for (int i = 0; i < numberOfByte; i++) {
                    dataOutputStream.writeShort(audioData[i]);
                }
            }

            audioRecord.stop();
            dataOutputStream.close();

        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
