package com.example.myapp07;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ScreenActivity extends MainActivity {
    // the key constant
    public static final String EXTRA_MESSAGE
//            = "com.example.testactivitytrasdata.MESSAGE";
            = "YourPackageName.MESSAGE";

    private TextView scView;
    static final int RESULT_SUBACTIVITY = 1000;

    ScreenActivity(){

    }
    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
    public void screenActivityMain(){
//        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scView = findViewById(R.id.recognaizeTextView);
        scView.setText("MainActivity!!");

        final TextView editText= findViewById(R.id.textView2);
        Button button = findViewById(R.id.activityButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SubScreenActivity.class);
                if(editText.getText() != null){
                    String str = editText.getText().toString();
                    intent.putExtra(EXTRA_MESSAGE, str);
                }
                startActivityForResult( intent, RESULT_SUBACTIVITY );

                // in order to clear the edittext
                editText.setText("lalaland");
            }
        });
    }

    // SubActivity からの返しの結果を受け取る
    protected void onActivityResult( int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK && requestCode == RESULT_SUBACTIVITY &&
                null != intent) {
            String res = intent.getStringExtra(ScreenActivity.EXTRA_MESSAGE);
            scView.setText(res);
        }
    }
}
