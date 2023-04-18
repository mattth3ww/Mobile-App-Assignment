package com.example.madasm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class BookDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "extra_book";

    private ImageView mBookCover;
    private TextView mBookTitle;
    private TextView mBookAuthor;
    private TextView mBookDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme();
        setContentView(R.layout.book_item_layout);

        mBookCover = findViewById(R.id.book_cover);
        mBookTitle = findViewById(R.id.book_title);
        mBookAuthor = findViewById(R.id.book_author);
        mBookDescription = findViewById(R.id.book_description);

        Intent intent = getIntent();
        Book book = (Book) intent.getSerializableExtra("book");

        if (book != null){
            setTitle(book.getTitle());
            mBookCover.setImageResource(book.getCoverImage());
            mBookTitle.setText(book.getTitle());
            mBookAuthor.setText(book.getAuthor());
            mBookDescription.setText(book.getDescription());
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
