package com.example.madasm;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Creating variables for our request queue, array list, progress bar, edit text, image button and recycler view.
    private RequestQueue mRequestQueue;
    private ArrayList<BookInfo> bookInfoArrayList;
    private ProgressBar progressBar;
    private EditText searchEdt;
    private ImageButton searchBtn;
    private ImageButton btnHome;
    private ImageButton btnUser;
    private ImageButton btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme();
        setContentView(R.layout.main);

        // Initializing views.
        progressBar = findViewById(R.id.idLoadingPB);
        searchEdt = findViewById(R.id.etSearch);
        searchBtn = findViewById(R.id.btnSearch);
        btnHome = findViewById(R.id.btnHome);
        btnUser = findViewById(R.id.btnUser);
        btnSettings = findViewById(R.id.btnSettings);

        // On Click Listener for every buttons
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                // Validation to check for empty search bar
                if (searchEdt.getText().toString().isEmpty()) {
                    searchEdt.setError("Please enter search query");
                    return;
                }
                // If the search query is not empty then call the get book info method to load the books from the API
                getBooksInfo(searchEdt.getText().toString());
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to home page
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to settings page
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to user page
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getBooksInfo(String query) {

        // Creating a new array list.
        bookInfoArrayList = new ArrayList<>();

        // Below line is use to initialize the variable for our request queue
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);

        // Below line is use to clear cache this will be use when our data is being updated
        mRequestQueue.getCache().clear();

        // URL for getting data from API in JSON format
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;

        // Below line we are creating a new request queue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // Below line is use to make json object request inside that we are passing url, get method and getting JSON object
        JsonObjectRequest booksObjrequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                // Inside on response method we are extracting all the JSON data.
                try {
                    JSONArray itemsArray = response.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemsObj = itemsArray.getJSONObject(i);
                        JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
                        String title = volumeObj.optString("title");
                        String subtitle = volumeObj.optString("subtitle");
                        JSONArray authorsArray = volumeObj.getJSONArray("authors");
                        String publisher = volumeObj.optString("publisher");
                        String publishedDate = volumeObj.optString("publishedDate");
                        String description = volumeObj.optString("description");
                        int pageCount = volumeObj.optInt("pageCount");
                        JSONObject imageLinks = volumeObj.optJSONObject("imageLinks");
                        String thumbnail = imageLinks.optString("thumbnail");
                        String previewLink = volumeObj.optString("previewLink");
                        String infoLink = volumeObj.optString("infoLink");
                        JSONObject saleInfoObj = itemsObj.optJSONObject("saleInfo");
                        String buyLink = saleInfoObj.optString("buyLink");
                        ArrayList<String> authorsArrayList = new ArrayList<>();
                        if (authorsArray.length() != 0) {
                            for (int j = 0; j < authorsArray.length(); j++) {
                                authorsArrayList.add(authorsArray.optString(i));
                            }
                        }
                        // After extracting all the data we are saving this data in our BookInfo modal class
                        BookInfo bookInfo = new BookInfo(title, subtitle, authorsArrayList, publisher, publishedDate, description, pageCount, thumbnail, previewLink, infoLink, buyLink);

                        // Below line is use to pass our modal class in our array list.
                        bookInfoArrayList.add(bookInfo);

                        // Below line is use to pass our array list in adapter class.
                        BookAdapter adapter = new BookAdapter(bookInfoArrayList, MainActivity.this);

                        // Below line is use to add linear layout manager for our recycler view.
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.sliderRecyclerView);

                        // In below line we are setting layout manager and adapter to our recycler view.
                        mRecyclerView.setLayoutManager(linearLayoutManager);
                        mRecyclerView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Display a toast message when we get any error from API
                    Toast.makeText(MainActivity.this, "No Data Found" + e, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Displaying error message in toast
                Toast.makeText(MainActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();
            }
        });
        // Finally, we are adding our JSON object request in our request queue
        queue.add(booksObjrequest);
    }

    private void setupTheme() {
        // Get the current theme mode from SharedPreferences, or default to light mode
        SharedPreferences sharedPreferences = getSharedPreferences("theme_pref", Context.MODE_PRIVATE);
        int currentThemeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);

        // Set the theme based on the current theme mode
        setTheme(currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES ? R.style.AppTheme_Dark : R.style.AppTheme);
    }
}


