package com.example.savas.ezberteknigi.Activities.BottomNavFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.savas.ezberteknigi.Activities.ReadingDetailActivity;
import com.example.savas.ezberteknigi.Adapters.ArticleAdapter;
import com.example.savas.ezberteknigi.Models.Reading;
import com.example.savas.ezberteknigi.R;
import com.example.savas.ezberteknigi.ViewModels.ReadingTextViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ReadingsFragment extends Fragment {

    ReadingTextViewModel readingTextViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main_readings, container, false);
        getActivity().setTitle("Makaleler");

        final ArticleAdapter articleAdapter = new ArticleAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_reading_text);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(articleAdapter);

        readingTextViewModel = ViewModelProviders.of(this).get(ReadingTextViewModel.class);
        readingTextViewModel.getAllReadingTexts().observe(this, readingTexts -> {

            List<Reading> articles = new ArrayList<>();

            for (Reading readingText : readingTexts) {
                if(readingText.getDocumentType() == Reading.DOCUMENT_TYPE_PLAIN
                        || readingText.getDocumentType() == Reading.DOCUMENT_TYPE_WEB){
                    articles.add(readingText);
                }
            }

            articleAdapter.setReadings(articles);
        });

        articleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Reading reading) {
                    Intent intent = new Intent(getActivity(), ReadingDetailActivity.class);
                    intent.putExtra(ReadingDetailActivity.EXTRA_READING_TEXT_DETAIL_ID, reading.getReadingId());
                    startActivity(intent);
            }

            @Override
            public void onItemLongClick(Reading reading) {

            }
        });



        Log.d(TAG, "onCreateView: recyclerView: " + recyclerView.toString());
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if (i == ItemTouchHelper.LEFT | i == ItemTouchHelper.RIGHT) {
                    Reading rt = articleAdapter.getReadingTextAt(viewHolder.getAdapterPosition());
                    ReadingTextViewModel rtViewModel = new ReadingTextViewModel(getActivity().getApplication());
                    rtViewModel.delete(rt);

                    Snackbar snackbar = Snackbar.make(view,
                            "Okuma metni silindi",
                            Snackbar.LENGTH_LONG);
                    snackbar.setAction("GERİ AL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rtViewModel.insert(rt);
                        }
                    });
                    snackbar.show();
                }
            }
        }).attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}