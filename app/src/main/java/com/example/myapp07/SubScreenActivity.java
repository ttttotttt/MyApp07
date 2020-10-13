package com.example.myapp07;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SubScreenActivity extends AppCompatActivity {
    private TextView editText;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        // to get message from MainActivity
        Intent intent = getIntent();
        message = intent.getStringExtra(ScreenActivity.EXTRA_MESSAGE);
        editText = findViewById(R.id.textView2);
        editText.setText(message);
        TextView textView = findViewById(R.id.recognaizeTextView);
        textView.setText("SUBView!!");

        // back to MainActivity
        Button button = findViewById(R.id.activityButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                if (editText.getText() != null) {
                    String str = message + editText.getText().toString();
                    intent.putExtra(ScreenActivity.EXTRA_MESSAGE, str);
                }
                editText.setText("Slsnlns!!");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
