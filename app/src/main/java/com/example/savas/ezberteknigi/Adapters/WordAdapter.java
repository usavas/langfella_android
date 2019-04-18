package com.example.savas.ezberteknigi.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.savas.ezberteknigi.Models.Word;
import com.example.savas.ezberteknigi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordHolder> {
    private List<Word> words = new ArrayList<>();
    private boolean isRevision = false;
    private Context context;

    public WordAdapter(Context context) {
        this.context = context;
    }

    public WordAdapter(Context context, boolean isRevision) {
        this.context = context;
        this.isRevision = isRevision;
    }

    @NonNull
    @Override
    public WordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView;
        if (isRevision) {
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_word_revision, viewGroup, false);
        } else {
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_word_2, viewGroup, false);
        }
        return new WordHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordAdapter.WordHolder wordHolder, int i) {
        wordHolder.bind(words.get(i), wordHolder.itemView);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Word word);

        void onMarkClick(Word word);

        void onItemLongClick(Word word);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class WordHolder extends RecyclerView.ViewHolder {
        private TextView tvWord;
        private TextView tvTranslation;
        private TextView tvExampleSentence;
        private Button btnMark;

        private ViewFlipper wordFlipper;
        private View wordFlipper2ndView;

        private ViewFlipper wordDetailFlipper;
        private View wordFlipperDetail1stView;
        private View wordFlipperDetail2ndView;
        private View wordFlipperDetailOptions;

        WordHolder(@NonNull View itemView) {
            super(itemView);

            tvWord = itemView.findViewById(R.id.tvItemWord);
            tvTranslation = itemView.findViewById(R.id.sub_item_translation);
            tvExampleSentence = itemView.findViewById(R.id.sub_item_example_sentence);


            if (isRevision) {
                wordFlipper = itemView.findViewById(R.id.word_flipper);
                wordFlipper2ndView = itemView.findViewById(R.id.sub_item);

                itemView.setOnClickListener(v -> {
                    int pos = WordHolder.this.getAdapterPosition();
                    if (listener != null && pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(words.get(pos));
                    }
                });


            } else {

                ImageButton btnDetail = itemView.findViewById(R.id.button_details_word);
                ImageButton btnEdit = itemView.findViewById(R.id.button_edit_word);
                ImageButton btnShare = itemView.findViewById(R.id.button_share_word);
                ImageButton delete = itemView.findViewById(R.id.button_delete_word);
                ImageButton archive = itemView.findViewById(R.id.button_archive_word);

                wordDetailFlipper = itemView.findViewById(R.id.word_detail_flipper);
                wordFlipperDetail1stView = itemView.findViewById(R.id.flip_to_word);
                wordFlipperDetail2ndView = itemView.findViewById(R.id.flip_to_translation);
                wordFlipperDetailOptions = itemView.findViewById(R.id.flip_to_options);

                itemView.setOnLongClickListener(v -> {
                    if (listener != null & getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(words.get(getAdapterPosition()));
                    }
                    return true;
                });



//                btnMark = itemView.findViewById(R.id.button_sub_item_item_learn_mastered);
//                btnMark.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int pos = getAdapterPosition();
//                        if (listener != null && pos != RecyclerView.NO_POSITION){
//                            listener.onMarkClick(words.get(pos));
//                        }
//                    }
//                });
            }
        }

        private void bind(Word word, View view) {
            tvWord.setText(word.getWord());
            tvTranslation.setText(word.getTranslation());
            tvExampleSentence.setText(word.getExampleSentence());

            if (isRevision) {
                wordFlipper.setDisplayedChild(wordFlipper.indexOfChild(tvWord));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wordFlipper.showNext();
                    }
                });
            } else {
                view.setOnClickListener(v -> {
                    int displayedChild = wordDetailFlipper.getDisplayedChild();
                    if (displayedChild == wordDetailFlipper.indexOfChild(wordFlipperDetail1stView)) {
                        wordDetailFlipper.setDisplayedChild(wordDetailFlipper.indexOfChild(wordFlipperDetail2ndView));
                    } else {
                        wordDetailFlipper.setDisplayedChild(wordDetailFlipper.indexOfChild(wordFlipperDetail1stView));
                    }
                });


                view.setLongClickable(true);
                view.setOnLongClickListener(v -> {
                    Toast.makeText(context, "working", Toast.LENGTH_LONG).show();

                    //TODO: show other side -> save delete archive
                    if (wordDetailFlipper.getDisplayedChild() == wordDetailFlipper.indexOfChild(wordFlipperDetailOptions)){
                        wordDetailFlipper.setDisplayedChild(wordDetailFlipper.indexOfChild(wordFlipperDetail1stView));
                    } else {
                        wordDetailFlipper.setDisplayedChild(wordDetailFlipper.indexOfChild(wordFlipperDetailOptions));
                    }

                    return true;
                });
            }
        }
    }

    public Word getWordAt(int position) {
        return words.get(position);
    }

    public void removeItem(int position) {
        words.remove(words.get(position));
        notifyItemChanged(position);
    }

    public void setWords(List<Word> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setWordsRevision(List<Word> _words) {
        this.words = Word.getWordsToRevise(_words);
        notifyDataSetChanged();
    }
}
