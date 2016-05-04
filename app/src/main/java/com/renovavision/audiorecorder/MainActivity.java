package com.renovavision.audiorecorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.renovavision.audiorecoder.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.frequency)
    Spinner spinnerFrequency;

    @Bind(R.id.start_button)
    Button startButton;

    @Bind(R.id.stop_button)
    Button stopButton;

    @Bind(R.id.play_button)
    Button playButton;

    private SampleFrequencyAdapter adapter;

    private AudioPlayer audioPlayer;
    private AudioRecorder audioRecorder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        adapter = new SampleFrequencyAdapter(this, SampleFrequency.values());
        spinnerFrequency.setAdapter(adapter);

        stopButton.setEnabled(false);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (audioPlayer != null) {
            audioPlayer.interrupt();
        }
        if (audioRecorder != null) {
            audioRecorder.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.start_button)
    public void onStartClick(View view) {
        if (audioRecorder != null) {
            return;
        }

        audioRecorder = new AudioRecorder(this, adapter.getItem(spinnerFrequency.getSelectedItemPosition()));
        audioRecorder.start();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    @OnClick(R.id.stop_button)
    public void onStopClick(View view) {
        if (audioRecorder != null) {
            audioRecorder.interrupt();
            audioRecorder = null;
        }
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    @OnClick(R.id.play_button)
    public void onPlayClick(View view) {
        audioPlayer = new AudioPlayer(this, adapter.getItem(spinnerFrequency.getSelectedItemPosition()));
        audioPlayer.start();
    }

}
