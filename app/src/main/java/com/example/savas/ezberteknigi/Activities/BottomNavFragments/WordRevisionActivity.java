package com.example.savas.ezberteknigi.Activities.BottomNavFragments;

import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.savas.ezberteknigi.Adapters.WordRevisionAdapter;
import com.example.savas.ezberteknigi.Data.Models.Word;
import com.example.savas.ezberteknigi.R;
import com.example.savas.ezberteknigi.ViewModels.WordRevisionViewModel;

import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

public class WordRevisionActivity extends AppCompatActivity {

    WordRevisionViewModel wordRevisionViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_word_revision);

        WordRevisionAdapter wordRevisionAdapter;
        RecyclerView recyclerView = findViewById(R.id.rv_word_revision);
        makeRecyclerViewSingleItemAtATime(recyclerView);

        wordRevisionAdapter = new WordRevisionAdapter(getBaseContext());

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(wordRevisionAdapter);

        wordRevisionViewModel = ViewModelProviders.of(this).get(WordRevisionViewModel.class);
        wordRevisionViewModel.getRevisionWords().observe(this, new Observer<List<Word>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(@Nullable List<Word> words) {

                List<Word> wordsToRevise = Word.getWordsToRevise(words);

                if (wordsToRevise.size() > 0){
                    wordRevisionAdapter.setWordsRevision(wordsToRevise);
                    setTitle("Tekrar Edilecek Kelimeler");
                } else {
                    Toast.makeText(WordRevisionActivity.this,
                            "Bütün kelimeler tekrar edildi!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        wordRevisionAdapter.setOnItemClickListener(new WordRevisionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Word word) {

            }
        });

        ImplementOnSwipedOnWords(recyclerView, wordRevisionAdapter, recyclerView);
    }


    /**
     * Makes the given recyclerView item to show one item at a time
     *
     * @param recyclerView
     */
    private void makeRecyclerViewSingleItemAtATime(RecyclerView recyclerView) {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void ImplementOnSwipedOnWords(View view, WordRevisionAdapter wordRevisionAdapter, RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if (i == ItemTouchHelper.LEFT || i == ItemTouchHelper.RIGHT) {

                    Word word = wordRevisionAdapter.getWordAt(viewHolder.getAdapterPosition());
                    Date prevDateToRollBack = word.getDateLastRevision();
                    updateRevision(word);

                    clearWordFromNotificationMenuIfExists(word);

                    Snackbar snackbar = Snackbar.make(view,
                            "Kelime tekrar edildi: " + word.getWord(),
                            Snackbar.LENGTH_LONG);
                    snackbar.setAction("İPTAL ET", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rollBackRevision(word, prevDateToRollBack);
                        }
                    });
                    snackbar.show();

                }
            }
        }).attachToRecyclerView(recyclerView);
    }


    private void updateRevision(Word word) {
        word.revisionCompleted();
        wordRevisionViewModel.update(word);
    }

    private void rollBackRevision(Word word, Date prevDateToRollBack) {
        word.setRevisionPeriodCount(word.getRevisionPeriodCount() - 1);
        word.setDateLastRevision(prevDateToRollBack);
        wordRevisionViewModel.update(word);
    }

    /**
     * Checks whether there is any notification currently showing up related to a given word and clears the notification with the given word's id
     *
     * @param word the word which might be shown in the notification
     */
    private void clearWordFromNotificationMenuIfExists(Word word) {
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(word.getWordId());

            //TODO: if this is the last notification of notification group, then delete it, else leave it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (notificationManager.getActiveNotifications().length == 1) {
                    notificationManager.cancel(-1);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "clearWordFromNotificationMenuIfExists: " + e.getMessage());
        }
    }
}
