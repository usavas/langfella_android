package com.example.savas.ezberteknigi.Data.Models.POJOs;

import com.example.savas.ezberteknigi.Data.Models.Reading;

public class ReadingFolder {
    private Reading reading;
    private int wordCount;

    public Reading getReading() {
        return reading;
    }

    public void setReading(Reading reading) {
        this.reading = reading;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }
}
