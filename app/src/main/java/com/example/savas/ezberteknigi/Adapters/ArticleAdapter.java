package com.example.savas.ezberteknigi.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.savas.ezberteknigi.Data.Models.Article;
import com.example.savas.ezberteknigi.Data.Models.Reading;
import com.example.savas.ezberteknigi.R;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ReadingTextHolder> {
    private List<Reading> readings = new ArrayList<>();
    private OnItemClickListener listener;
    private OnOptionsClickListener optionsClickListener;


    /*
     * override methods */

    @NonNull
    @Override
    public ReadingTextHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_r_webarticle, viewGroup, false);
        return new ReadingTextHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadingTextHolder readingTextHolder, int i) {
        readingTextHolder.bind(readings.get(i), readingTextHolder.itemView);
    }

    @Override
    public int getItemCount() {
        return readings.size();
    }


    /*
     * ViewHolder declaration */
    class ReadingTextHolder extends RecyclerView.ViewHolder {
        private TextView header;
        private TextView content;
        private ImageView imageView;

        private ViewFlipper viewFlipper;
        private View articleContainer;
        private View optionsContainer;

        private ImageButton btnDelete;
        private ImageButton btnArchive;
        private ImageButton btnShare;

        /*
         * ViewHolder constructor */
        ReadingTextHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.tvItemHeader);
            content = itemView.findViewById(R.id.tvItemContent);

            viewFlipper = itemView.findViewById(R.id.view_flipper_reading_options);
            articleContainer = itemView.findViewById(R.id.article_container);
            optionsContainer = itemView.findViewById(R.id.options_container);

            btnDelete = itemView.findViewById(R.id.delete_reading);
            btnArchive = itemView.findViewById(R.id.archive_reading);
            btnShare = itemView.findViewById(R.id.share_reading);
        }

        private void bind(Reading reading, View view) {

            if (reading.getDocumentType() == Reading.DOCUMENT_TYPE_BOOK)
                return;

            Article article = (reading.getDocumentType() == Reading.DOCUMENT_TYPE_PLAIN)
                    ? reading.getSimpleArticle()
                    : reading.getWebArticle();

            header.setText(article.getTitle());
            content.setText(
                    article.getContentForPreview().replace("\n\n", "\n"));

            view.setOnClickListener(v -> {
                if (viewFlipper.getDisplayedChild()
                        == viewFlipper.indexOfChild(articleContainer)){
                    if (listener != null) listener.onItemClick(reading);
                } else {
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(articleContainer));
                }
            });

            view.setOnLongClickListener(f -> {
                    viewFlipper.showNext();
                    return true;
            });

            btnDelete.setOnClickListener(l -> {
                optionsClickListener.onDeleteClick(reading);
            });

            btnArchive.setOnClickListener(l -> {
                optionsClickListener.onArchiveClick(reading, getAdapterPosition());
            });

            btnShare.setOnClickListener(l -> {
                optionsClickListener.onShareClick(reading);
            });
        }
    }


    /*
     * interfaces */

    public interface OnItemClickListener {
        void onItemClick(Reading reading);

        void onItemLongClick(Reading reading);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnOptionsClickListener {
        void onDeleteClick(Reading reading);

        void onArchiveClick(Reading reading, int position);

        void onShareClick(Reading reading);
    }

    public void setOnOptionsClickListener(OnOptionsClickListener listener) {
        this.optionsClickListener = listener;
    }



    /*
     * other helper methods */

    public Reading getReadingTextAt(int position) {
        return readings.get(position);
    }

    public void setReadings(List<Reading> readings) {
        this.readings = readings;
        notifyDataSetChanged();
    }
}
