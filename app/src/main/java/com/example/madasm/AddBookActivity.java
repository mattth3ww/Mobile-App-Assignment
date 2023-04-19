package com.example.madasm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.madasm.Book;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class AddBookActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText authorEditText;
    private EditText descriptionEditText;
    private ImageView coverImageView;

    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int REQUEST_CODE = 1;
    // Define the extra key constants
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_AUTHOR = "extra_author";
    public static final String EXTRA_COVER_IMAGE = "extra_cover_image";
    public static final String EXTRA_COVER_IMAGE_RESOURCE = "extra_cover_image_resource";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme();
        setContentView(R.layout.activity_add_book);

        titleEditText = findViewById(R.id.titleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        coverImageView = findViewById(R.id.coverImageView);

        coverImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String author = authorEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                int coverImage = R.drawable.book_placeholder;

                if (title.isEmpty()) {
                    titleEditText.setError("Please Enter a book title");
                    titleEditText.requestFocus();
                    return;
                }

                if (author.isEmpty()) {
                    authorEditText.setError("Please enter author information");
                    authorEditText.requestFocus();
                    return;
                }

                if (description.isEmpty()) {
                    descriptionEditText.setError("Please enter book description");
                    descriptionEditText.requestFocus();
                    return;
                }

                if (coverImageView.getDrawable() != null) {
                    coverImage = (int) coverImageView.getTag();
                }

                Book book = new Book(title, author, description, coverImage);
                Intent intent = new Intent();
                intent.putExtra("book", book);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            coverImageView.setImageURI(imageUri);
            coverImageView.setTag(imageUri);
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
