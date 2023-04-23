package com.example.GENB;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputWords;
    private TextInputEditText inputWordCount;
    private Button generatePhraseButton;
    private Button selectFileButton;
    private TextView phraseTextView;
    private ClipboardManager clipboardManager;
    private Random random;

    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputWords = findViewById(R.id.inputWords);
        inputWordCount = findViewById(R.id.numWords);
        generatePhraseButton = findViewById(R.id.generatePhraseButton);
        selectFileButton = findViewById(R.id.selectFileButton);
        phraseTextView = findViewById(R.id.phraseTextView);

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        random = new Random();

        generatePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePhrase();
            }
        });

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }

    private void generatePhrase() {
        String input = inputWords.getText().toString();
        String[] wordsArray = input.split(" ");
        int wordCount;
        try {
            wordCount = Integer.parseInt(inputWordCount.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid word count", Toast.LENGTH_SHORT).show();
            return;
        }

        if (wordCount > wordsArray.length) {
            Toast.makeText(this, "Word count exceeds number of words in the list", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> wordsList = new ArrayList<>(Arrays.asList(wordsArray));
        String phrase = "";
        for (int i = 0; i < wordCount; i++) {
            int index = random.nextInt(wordsList.size());
            phrase += wordsList.get(index).trim() + " ";
            wordsList.remove(index);
        }

        phraseTextView.setText(phrase.trim());
        ClipData clipData = ClipData.newPlainText("Generated Phrase", phrase.trim());
        clipboardManager.setPrimaryClip(clipData);
    }

    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                readTextFromUri(uri);
            }
        }
    }

    private void readTextFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(" ");
            }
            inputStream.close();
            reader.close();
            inputWords.setText(stringBuilder.toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
        }
    }
}