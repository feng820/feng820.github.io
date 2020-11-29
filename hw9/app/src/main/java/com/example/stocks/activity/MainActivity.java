package com.example.stocks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.stocks.R;
import com.example.stocks.adapter.AutoSuggestAdapter;
import com.example.stocks.network.DataService;
import com.example.stocks.network.GsonCallBack;
import com.example.stocks.utils.PreferenceStorageManager;
import com.example.stocks.utils.StockApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static androidx.appcompat.R.id.search_src_text;


public class MainActivity extends AppCompatActivity {
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 200;
    private static final String TAG = "MainActivity";
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;
    private static boolean hasClickedSuggestion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize Shared Preference singleton
        PreferenceStorageManager.init();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, StockDetailActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setIconifiedByDefault(true);

        AppCompatAutoCompleteTextView autoCompleteTextView = searchView.findViewById(search_src_text);
        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setDropDownHeight(1100);
        autoCompleteTextView.setMaxLines(1);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (hasClickedSuggestion) {
                    return false;
                } else {
                    String errorMsg = "You have to choose one of the suggestions!";
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // listener to search view on dropdown item clicked
        autoCompleteTextView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    searchView.setQuery(autoSuggestAdapter.getItem(position), false);
                    hasClickedSuggestion = true;
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
                hasClickedSuggestion = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(msg -> {
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
        if (text == null || text.length() <= 2) {
            return;
        }
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
            }

            @Override
            public void onError(String result) {
                Log.e(TAG, "onError: Cannot Get Suggestions due to " + result);
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