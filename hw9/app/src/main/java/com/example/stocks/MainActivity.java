package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.stocks.adapter.AutoSuggestAdapter;
import com.example.stocks.network.DataService;
import com.example.stocks.network.GsonCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static androidx.appcompat.R.id.search_src_text;


public class MainActivity extends AppCompatActivity {
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        AppCompatAutoCompleteTextView autoCompleteTextView = searchView.findViewById(search_src_text);

        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setDropDownHeight(1100);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);

        // listener to search view on dropdown item clicked
        autoCompleteTextView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    autoCompleteTextView.setText(autoSuggestAdapter.getItem(position));
                }
        );

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler((Handler.Callback) msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                    getSuggestions(autoCompleteTextView.getText().toString());
                }
            }
            return false;
        });

        return true;
    }

    private void getSuggestions(String text) {
        DataService.getInstance().getAutoSuggestions(text, new GsonCallBack<List<Map<String, String>>>() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                List<String> suggestions = new ArrayList<>();
                for (Map<String, String> entry : result) {
                    String ticker = entry.get("ticker");
                    String name = entry.get("name");
                    if (ticker != null && name != null) {
                        suggestions.add(ticker + " - " + name);
                    }
                }
                autoSuggestAdapter.setData(suggestions);
                autoSuggestAdapter.notifyDataSetChanged();
                Log.d("Get Suggestions Success", "success");
            }

            @Override
            public void onError(String result) {
                Log.e("Get Suggestions Error", "Error Message: " + result);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_item) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}