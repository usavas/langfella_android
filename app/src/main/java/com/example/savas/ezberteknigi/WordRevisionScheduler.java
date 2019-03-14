package com.example.savas.ezberteknigi;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.savas.ezberteknigi.Activities.MainActivity;
import com.example.savas.ezberteknigi.Activities.NavigatorActivity;
import com.example.savas.ezberteknigi.Models.Word;
import com.example.savas.ezberteknigi.Repositories.WordRepository;
import com.example.savas.ezberteknigi.Services.WordRevisedService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;
import static com.example.savas.ezberteknigi.AppStarter.CHANNEL_WORD_REVISION;

public class WordRevisionScheduler extends JobService {

    public static final String EXTRA_WORD_TO_SAVE_REVISED = "EXTRA_WORD_TO_SAVE_REVISED";
    private static final String TAG = "XXXXXXXXXXXXXXXX";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob: WordRevisionJob started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Word> wordsToRevise = getWordsToRevise();
                if (wordsToRevise.size() > 0) {
                    showMultipleNotificationsInGroup(wordsToRevise);
                }
            }
        }).start();
    }

    private List<Word> getWordsToRevise() {
        WordRepository r = new WordRepository(getApplication());
        List<Word> words = r.getAllWordsAsList();
        return Word.getWordsToRevise(words);
    }

    @NonNull
    private String getWordListInString(List<Word> wordsToRevise) {
        StringBuilder wordsToDisplay = new StringBuilder();
        for (Word w : wordsToRevise) {
            wordsToDisplay.append(w.getWord()).append(": ").append(w.getTranslation()).append(", ");
        }
        return wordsToDisplay.toString().substring(0, wordsToDisplay.length() - 2);
    }

    private void showMultipleNotificationsInGroup(List<Word> wordsToRevise) {

        Intent intent = new Intent(this, NavigatorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        for (int i = 0; i < wordsToRevise.size(); i++) {

            Word word = wordsToRevise.get(i);
            Log.d(TAG, "showMultipleNotificationsInGroup: word: " + word.toString());
            Intent wordRevisedIntent = new Intent(this, WordRevisedService.class);
            wordRevisedIntent.putExtra(EXTRA_WORD_TO_SAVE_REVISED, word.getWordId());
            PendingIntent deletePendingIntent = PendingIntent.getService(getApplicationContext(), word.getWordId(), wordRevisedIntent, 0);

            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_WORD_REVISION)
                    .setSmallIcon(R.drawable.button_revision)
                    .setContentTitle(word.getWord())
                    .setContentText(String.format("%s :\n %s", word.getTranslation(), word.getExampleSentence()))
                    .setGroup("words")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOnlyAlertOnce(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
//                    .addAction(R.drawable.button_revision, "TEKRAR ETTİM", deletePendingIntent)
                    .setDeleteIntent(deletePendingIntent)
                    .build();

            notificationManager.notify(word.getWordId(), notification);
        }

        Locale l = new Locale("tr");
        if(Build.VERSION.SDK_INT >= 24){

            Notification summaryNotification = new NotificationCompat.Builder(this, CHANNEL_WORD_REVISION)
                    .setSmallIcon(R.drawable.button_revision)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .setSummaryText(String.format(l, "%d Tekrar Edilecek Kelime", wordsToRevise.size())))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setGroup("words")
                    .setGroupSummary(true)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setContentIntent(pendingIntent)
                    .build();

            notificationManager.notify(-1, summaryNotification);
        } else {
            Notification summaryNotification = new NotificationCompat.Builder(this, CHANNEL_WORD_REVISION)
                    .setSmallIcon(R.drawable.button_revision)

                    .setContentTitle("Tekrar Edilecek Kelimeler")
                    .setContentText(getWordListInString(wordsToRevise))

                    .setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle("Tekrar Edilecek Kelimeler")
                            .setSummaryText(String.format(l, "%d Tekrar Edilecek Kelime", wordsToRevise.size()))
                            .bigText(getWordListInString(wordsToRevise)))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setGroup("words")
                    .setGroupSummary(true)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setContentIntent(pendingIntent)
                    .build();

            notificationManager.notify(-1, summaryNotification);
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
