package com.example.stocks.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stocks.R;
import com.example.stocks.utils.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<NewsItem> newsList;
    private final Context ctx;
    private static final String TWITTER_URL = "https://twitter.com/intent/tweet";
    private static final String TWITTER_TEXT_QUERY = "Check out this Link:\n";
    private static final String TWITTER_TAG_QUERY = "CSCI571StockApp";

    public NewsRecyclerViewAdapter(Context ctx, List<NewsItem> newsList) {
        this.newsList = newsList;
        this.ctx = ctx;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_body, parent, false);
        }

        return new NewsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);
        if (holder instanceof NewsItemViewHolder) {
            Glide.with(ctx)
                    .asBitmap()
                    .load(newsItem.imageUrl)
                    .error(R.drawable.no_image)
                    .fallback(R.drawable.no_image)
                    .into(((NewsItemViewHolder) holder).newsImage);

            ((NewsItemViewHolder) holder).newsSource.setText(newsItem.newsSource);
            ((NewsItemViewHolder) holder).daysAgo.setText(newsItem.publishedAt);
            ((NewsItemViewHolder) holder).newsTitle.setText(newsItem.newsTitle);

            ((NewsItemViewHolder) holder).newsCard.setOnClickListener(v -> {
                Uri url = Uri.parse(newsItem.articleUrl);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW);
                launchBrowser.setData(url);
                ctx.startActivity(launchBrowser);
            });

            ((NewsItemViewHolder) holder).newsCard.setOnLongClickListener(v -> {
                Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.article_dialog);

                ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
                TextView dialogText = dialog.findViewById(R.id.dialog_text);
                ImageView twitterButton = dialog.findViewById(R.id.twitter_button);
                ImageView chromeButton = dialog.findViewById(R.id.chrome_button);

                Glide.with(ctx)
                        .asBitmap()
                        .load(newsItem.imageUrl)
                        .error(R.drawable.no_image)
                        .fallback(R.drawable.no_image)
                        .into(dialogImage);
                dialogText.setText(newsItem.newsTitle);

                twitterButton.setOnClickListener(v1 -> {
                    Uri.Builder builder = Uri.parse(TWITTER_URL)
                            .buildUpon()
                            .appendQueryParameter("text", TWITTER_TEXT_QUERY + newsItem.articleUrl)
                            .appendQueryParameter("hashtags", TWITTER_TAG_QUERY);
                    Intent twitterShare = new Intent(Intent.ACTION_VIEW);
                    twitterShare.setData(builder.build());
                    ctx.startActivity(twitterShare);
                });

                chromeButton.setOnClickListener(v2 -> {
                    Uri url = Uri.parse(newsItem.articleUrl);
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW);
                    launchBrowser.setData(url);
                    ctx.startActivity(launchBrowser);
                });

                dialog.show();
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsItemViewHolder extends RecyclerView.ViewHolder {
        CardView newsCard;
        TextView newsSource;
        TextView daysAgo;
        TextView newsTitle;
        ImageView newsImage;

        public NewsItemViewHolder(@NonNull View itemView) {
            super(itemView);
            newsCard = itemView.findViewById(R.id.news_card);
            newsSource = itemView.findViewById(R.id.news_source);
            daysAgo = itemView.findViewById(R.id.days_ago);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsImage = itemView.findViewById(R.id.news_image);
        }
    }

}
