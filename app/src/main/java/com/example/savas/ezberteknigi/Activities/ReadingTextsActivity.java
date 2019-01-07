package com.example.savas.ezberteknigi.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.savas.ezberteknigi.Models.ReadingText;
import com.example.savas.ezberteknigi.R;
import com.example.savas.ezberteknigi.Adapters.ReadingTextAdapter;
import com.example.savas.ezberteknigi.ViewModels.ReadingTextViewModel;

import java.util.List;

public class ReadingTextsActivity extends AppCompatActivity {
    ReadingTextViewModel readingTextViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_texts);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_reading_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final ReadingTextAdapter readingTextAdapter = new ReadingTextAdapter();
        recyclerView.setAdapter(readingTextAdapter);

        readingTextViewModel = ViewModelProviders.of(this).get(ReadingTextViewModel.class);
        readingTextViewModel.getAllReadingTexts().observe(this, new Observer<List<ReadingText>>() {
            @Override
            public void onChanged(@Nullable List<ReadingText> readingTexts) {
                readingTextAdapter.setReadingTexts(readingTexts);
            }
        });
    }
}
