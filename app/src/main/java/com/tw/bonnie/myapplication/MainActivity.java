package com.tw.bonnie.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public Button m_btnStart;
    public Button m_btnExit;
    public EditText m_etName;
    public String m_strName;
    public int m_iCount =1;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        //開始遊戲
        m_btnStart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //將資料存到bundle再傳到另一個Activity
                Bundle bundle = new Bundle();
                m_strName = m_etName.getText().toString();
                SharedPreferences pref = getSharedPreferences("USER",MODE_PRIVATE);

                int iUser = getSharedPreferences("USER",MODE_PRIVATE)
                        .getInt(m_strName,-1);
                m_iCount = 1;
                if(iUser == -1){
                    pref = getSharedPreferences("USER", MODE_PRIVATE);
                    pref.edit()
                            .putInt(m_strName,m_iCount)
                            .commit();
                }else {
                    m_iCount = getSharedPreferences("USER",MODE_PRIVATE)
                            .getInt(m_strName,-1);
                    m_iCount+=1;
                    pref.edit().putInt(m_strName,m_iCount)
                            .commit();
                }

                bundle.putString("EXTRA_NAME",m_strName); //傳遞string
                bundle.putInt("EXTRA_COUNT",m_iCount); //傳遞intt

                Intent intent = new Intent(MainActivity.this,BingoActivity.class);
                intent.putExtras(bundle); //將bundle物件傳給intent
                startActivity(intent);
            }
        });

        //結束遊戲
        m_btnExit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
    }

    public void findViews() {
        m_btnStart = findViewById(R.id.button_start);
        m_btnExit = findViewById(R.id.button_exit);
        m_etName = findViewById(R.id.editText_name);
        textView = findViewById(R.id.textView_title);
    }
}
