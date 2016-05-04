package com.renovavision.audiorecorder;

import android.support.annotation.NonNull;

public enum SampleFrequency {

    KHZ_11("11.025 KHz (Lowest)", 11025),
    KHZ_16("16.000 KHz", 16000),
    KHZ_22("22.050 KHz", 22050),
    KHZ_44("44.100 KHz (Highest)", 44100);

    private String text;

    private int frequency;

    SampleFrequency(@NonNull String text, int frequency) {
        this.text = text;
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getText() {
        return text;
    }
}
