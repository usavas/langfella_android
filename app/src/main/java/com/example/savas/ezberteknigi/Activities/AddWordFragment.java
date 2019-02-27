package com.example.savas.ezberteknigi.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.savas.ezberteknigi.Models.Word;
import com.example.savas.ezberteknigi.R;
import com.example.savas.ezberteknigi.Repositories.WordRepository;
import com.example.savas.ezberteknigi.ViewModels.WordViewModel;

public class AddWordFragment extends AppCompatDialogFragment {
//    private DialogListener listener;
    private EditText editWord;
    private EditText editTranslation;
    private EditText editExampleSentence;

    private static int _readingTextId;


    public static AddWordFragment newInstance(String word, String translation, String exampleSentence, int readingTextId) {
        AddWordFragment f = new AddWordFragment();
        _readingTextId = readingTextId;

        Bundle args = new Bundle();
        args.putString("WORD", word);
        args.putString("TRANSLATION", translation);
        args.putString("EXAMPLE_SENTENCE", exampleSentence);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_word, null);

        builder.setView(view);

        editWord = view.findViewById(R.id.edit_word_word_fragment);
        editTranslation = view.findViewById(R.id.edit_word_translation_fragment);
        editExampleSentence = view.findViewById(R.id.edit_word_example_sentence_fragment);

        editWord.setText(getArguments().getString("WORD"));
        editTranslation.setText(getArguments().getString("TRANSLATION"));
        editExampleSentence.setText(getArguments().getString("EXAMPLE_SENTENCE"));

        Button btnAddLearning = view.findViewById(R.id.button_add_word_fragment);
        btnAddLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = editWord.getText().toString();
                String translation = editTranslation.getText().toString();
                String exampleSentence = editExampleSentence.getText().toString();

                if (word.trim() != "" && translation.trim() != ""){
                    saveWord(word, translation, exampleSentence, Word.WORD_LEARNING);
                } else {
                    showWordTranslationEmptyError();
                }
            }
        });

        Button btnAddMastered = view.findViewById(R.id.button_add_word_mastered_fragment);
        btnAddMastered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = editWord.getText().toString();
                String translation = editTranslation.getText().toString();
                String exampleSentence = editExampleSentence.getText().toString();

                if (word.trim() != "" && translation.trim() != ""){
                    saveWord(word, translation, exampleSentence, Word.WORD_MASTERED);
                } else {
                    showWordTranslationEmptyError();
                }
            }
        });

        return builder.create();
    }

    private void showWordTranslationEmptyError() {
        Toast.makeText(getContext(), "Kelime ve çevirisini giriniz!", Toast.LENGTH_LONG).show();
    }

    private void saveWord(String word, String translation, String exampleSentence, int learningMastered){
        WordRepository repo = new WordRepository(getActivity().getApplication());
        Word w = new Word(word, translation, _readingTextId, exampleSentence);
        w.setWordState(learningMastered);
        repo.insert(w);
        getDialog().dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        try {
//            listener = (DialogListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() +
//                    "must implement ExampleDialogListener");
//        }
    }
//
//    public interface DialogListener {
//        void insertWord(String word, String translation, String exampleSentence, int learningMastered);
//    }
}