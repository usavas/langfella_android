package com.example.savas.ezberteknigi.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.savas.ezberteknigi.Models.ReadingText;
import com.example.savas.ezberteknigi.R;
import com.example.savas.ezberteknigi.Repositories.ReadingTextRepository;

public class MainActivity extends AppCompatActivity {

    private Button btnReadingTexts;
    private Button btnWords;

    private Button btnAddSampleReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnReadingTexts = findViewById(R.id.button_reading_texts);
        btnReadingTexts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(v.getContext(), ReadingTextsActivity.class);
                startActivity(intent);
            }
        });

        btnWords = findViewById(R.id.button_words);
        btnWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WordsActivity.class);
                startActivity(intent);
            }
        });

        btnAddSampleReading = findViewById(R.id.button_add_sample_reading_text);
        btnAddSampleReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSampleNews();
            }
        });
    }

    private void AddSampleNews() {
        ReadingTextRepository repository = new ReadingTextRepository(getApplication());
        String newsContent = "this is the content of a BBC news pagethis is the content of a BBC news pagethis is the content of a BBC news pagethis is the content of a BBC news page";
        int wordCount = newsContent.length();
        repository.insert(new ReadingText("BBC", "sample header","news", 7, newsContent, wordCount));

        Log.d("MainAct", "news inserted");
        Toast.makeText(MainActivity.this, "row saved", Toast.LENGTH_SHORT).show();
    }
}