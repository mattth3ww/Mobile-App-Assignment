package com.example.madasm;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private BookAdapter bookAdapter;
    private ArrayList<Book> books;
    private EditText etSearch;
    private ImageButton btnSearch;
    private Button btnAdd;
    private ImageButton btnUser;
    private ImageButton btnSettings;
    private ImageButton btnHome;
    private RecyclerView sliderRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme();
        setContentView(R.layout.main);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnAdd = findViewById(R.id.btnAdd);
        btnUser = findViewById(R.id.btnUser);
        btnSettings = findViewById(R.id.btnSettings);
        btnHome = findViewById(R.id.btnHome);

        // Set up the books ArrayList and add sample data
        books = new ArrayList<>();
        books.add(new Book("The Great Gatsby", "F. Scott Fitzgerald", "Book1", R.drawable.pepecry));
        books.add(new Book("To Kill a Mockingbird", "Harper Lee", "Book2", R.drawable.pepehappy));
        books.add(new Book("1984", "George Orwell", "Book3", R.drawable.pepecry));
        books.add(new Book("Pride and Prejudice", "Jane Austen", "Book4", R.drawable.pepehappy));
        books.add(new Book("The Catcher in the Rye", "J.D. Salinger", "Book5", R.drawable.pepecry));
        books.add(new Book("Animal Farm", "George Orwell", "Book6", R.drawable.pepehappy));

        // Set up the book adapter and recycler view
        bookAdapter = new BookAdapter(this, books, new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Intent intent = new Intent(MainActivity.this, BookDetailsActivity.class);
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK, book);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sliderRecyclerView = findViewById(R.id.sliderRecyclerView);
        sliderRecyclerView.setLayoutManager(layoutManager);
        sliderRecyclerView.setAdapter(bookAdapter);

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

        // Set up the search button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etSearch.getText().toString();
                if (query.isEmpty()) {
                    bookAdapter.updateData(books);
                } else {
                    ArrayList<Book> searchResults = new ArrayList<>();
                    for (Book book : books) {
                        if (book.getTitle().toLowerCase().contains(query.toLowerCase())
                                || book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                            searchResults.add(book);
                        }
                    }
                    bookAdapter.updateData(searchResults);
                }
            }
        });

        // Set up the add button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });
    }

    // onActivityResult method for handling result from AddBookActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AddBookActivity.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Extract book data from intent
            String title = data.getStringExtra(AddBookActivity.EXTRA_TITLE);
            String author = data.getStringExtra(AddBookActivity.EXTRA_AUTHOR);
            String coverImage = data.getStringExtra(AddBookActivity.EXTRA_COVER_IMAGE);
            int coverImageResource = data.getIntExtra(AddBookActivity.EXTRA_COVER_IMAGE_RESOURCE, 0);

            // Create a new book object and add it to the book list
            Book book = new Book(title, author, coverImage, coverImageResource);
            books.add(book);
            bookAdapter.notifyDataSetChanged();
        }
    }

    private void setupTheme() {
        // Get the current theme mode from SharedPreferences, or default to light mode
        SharedPreferences sharedPreferences = getSharedPreferences("theme_pref", Context.MODE_PRIVATE);
        int currentThemeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);

        // Set the theme based on the current theme mode
        setTheme(currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES ? R.style.AppTheme_Dark : R.style.AppTheme);
    }
}


