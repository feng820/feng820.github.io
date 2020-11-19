package com.example.stocks.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoSuggestAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> suggestionList;

    public AutoSuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        suggestionList = new ArrayList<>();
    }

    public void setData(List<String> list) {
        suggestionList.clear();
        suggestionList.addAll(list);
    }

    public List<String> getSuggestionList() {
        return suggestionList;
    }

    @Override
    public int getCount() {
        return suggestionList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return suggestionList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                 FilterResults filterResults = new FilterResults();
                 if (constraint != null) {
                     filterResults.values = suggestionList;
                     filterResults.count = suggestionList.size();
                 }
                 return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

}
