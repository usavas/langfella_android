package com.example.savas.ezberteknigi.Repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Update;
import android.os.AsyncTask;

import com.example.savas.ezberteknigi.DAO.ReadingTextDao;
import com.example.savas.ezberteknigi.DAO.WordDao;
import com.example.savas.ezberteknigi.EzberTeknigiDatabase;
import com.example.savas.ezberteknigi.Models.ReadingText;
import com.example.savas.ezberteknigi.Models.Word;

import java.util.List;

public class WordRepository {
    private WordDao wordDao;
    private LiveData<List<Word>> allWords;

    public WordRepository(Application application) {
        EzberTeknigiDatabase ezberTeknigiDatabase = EzberTeknigiDatabase.getInstance(application);
        wordDao = ezberTeknigiDatabase.wordDao();
        allWords = wordDao.getAllWords();
    }

    public void Insert(Word word){
        new InsertWordAsyncTask(wordDao).execute(word);
    }

    public void Update(Word word){
        new UpdateWordAsyncTask(wordDao).execute(word);
    }

    public void Delete(Word word){
        new DeleteWordAsyncTask(wordDao).execute(word);
    }

    private static class InsertWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private InsertWordAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insert(words[0]);
            return null;
        }
    }

    private static class UpdateWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private UpdateWordAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.update(words[0]);
            return null;
        }
    }

    private static class DeleteWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private DeleteWordAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.delete(words[0]);
            return null;
        }
    }
}
