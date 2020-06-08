package com.tw.bonnie.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BingoActivity extends AppCompatActivity {
    public String m_strPlayer;
    public String m_strTableSize;
    public TextView m_tvGreet;
    public GridView m_gvBingo;
    public EditText m_etTableSize;
    public Integer m_iCount =0;
    public Integer m_iRandom;
    public Integer m_iTableSize;
    public Button m_btnCreateBingo;
    public Button m_btnBackPage;
    public RadioButton m_rbtnYellow;
    public RadioButton m_rbtnBlue;
    public RadioButton m_rbtnGreen;
    public RadioButton m_rbtnRed;
    public RadioGroup radioGroup;
    public Switch m_switchType;
    public String[] test = new String[0];
    public boolean[][] status;
    public boolean[] line;
    public MyGridviewAdapter adapter;
    public boolean boo;
    public int iColorChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);
        findViews();

        Bundle bundle = this.getIntent().getExtras(); //把intent的bundle解出來

        m_strPlayer = bundle.getString("EXTRA_NAME");
        m_iCount = bundle.getInt("EXTRA_COUNT");

        //沒輸入名字時給預設值
        if(TextUtils.isEmpty(m_strPlayer)){
            m_strPlayer="Player";
        }

        //Bingo頁面打招呼&計數訊息
        String str = ("嗨 , " + m_strPlayer + "這是你第" + m_iCount + "次玩Bingo");
        m_tvGreet.setText(str);

        adapter = new MyGridviewAdapter();
        m_gvBingo.setAdapter(adapter); //建gridview
        adapter.notifyDataSetChanged();

        createBingo();//建Bingo

        // 控制switch開關
        m_switchType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){ // switch開
                    boo=true;
                    Toast.makeText(BingoActivity.this, "現在是遊戲模式", Toast.LENGTH_SHORT).show();
                    line = new boolean[m_iTableSize*2+2];
                    m_btnCreateBingo.setEnabled(false);

                    //關閉軟鍵盤
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }else { // switch關
                    boo=false;
                    Toast.makeText(BingoActivity.this, "現在是輸入模式", Toast.LENGTH_SHORT).show();
                    m_btnCreateBingo.setEnabled(true);
                    for(int i=0;i<m_iTableSize*m_iTableSize;i++){
                        status[i % m_iTableSize][i / m_iTableSize]=false;
                    }
                }
                adapter.update(boo); // adapter呼叫此方法
            }
        });

        radioGroup.setOnCheckedChangeListener(radioGroupOnCheckedChange);

        m_etTableSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            // 限定輸入bingo盤大小只能是3x3 ~ 8x8
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern p = Pattern.compile("^([3-8])$");

                Matcher matcher =p.matcher(s.toString());
                if(matcher.find() || ("").equals(s.toString())){
                    System.out.print("OK!");
                }else{
                    System.out.print("FALSE!");
                    Toast.makeText(BingoActivity.this, "輸入範圍只能是3~8", Toast.LENGTH_SHORT).show();
                    m_etTableSize.setText("");
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        //回上一頁
        m_btnBackPage.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                BingoActivity.this.finish();
            }
        });

    }


    public RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChange = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch(checkedId){
                case R.id.radiobtn_red:
                    iColorChange = 0; // 變紅色
                    break;
                case R.id.radiobtn_yellow:
                    iColorChange = 1; // 變黃色
                    break;
                case R.id.radiobtn_blue:
                    iColorChange = 2; // 變藍色
                    break;
                case R.id.radiobtn_green:
                    iColorChange = 3; // 變綠色
                    break;
            }
        }
    };


    //動態建立冰果盤
    public void createBingo() {
        m_btnCreateBingo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                m_switchType.setEnabled(true);

                if(TextUtils.isEmpty(m_etTableSize.getText().toString())){
                    Toast.makeText(BingoActivity.this,"請輸入表格大小",Toast.LENGTH_SHORT).show();
                    m_switchType.setEnabled(false);
                }else{
                    m_strTableSize = m_etTableSize.getText().toString();
                    m_iTableSize = Integer.parseInt(m_strTableSize);
                    test = new String[m_iTableSize * m_iTableSize]; //設定冰果盤要有多少格的值
                    status = new boolean[m_iTableSize][m_iTableSize]; // 設定冰果盤每一格都是還沒點過
                    m_gvBingo.setNumColumns(m_iTableSize); //動態改行數

                    for (int i = 0; i < test.length; i++) {
                        status[i % m_iTableSize][i / m_iTableSize]=false;
                        m_iRandom = (int) (Math.random() * ((m_iTableSize * m_iTableSize) + 20) + 1);
                        test[i] = String.valueOf(m_iRandom); //亂數填值
                        for (int j = 0; j < i; j++) {
                            if(test[j].equals(test[i])) {
                                j=0; // 重頭開始檢查
                                i--; // i重生 重複的這圈重跑
                            }
                        }
                    }
                    adapter.update(boo); // adapter呼叫此方法
                }

                //關閉軟鍵盤
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });
    }

    public void findViews() {
        m_tvGreet = findViewById(R.id.tvGreet);
        m_gvBingo = findViewById(R.id.gridView);
        m_etTableSize = findViewById(R.id.edSize);
        m_btnCreateBingo = findViewById(R.id.btnCreate);
        m_btnBackPage = findViewById(R.id.btnBack);
        m_switchType = findViewById(R.id.switchType);
        m_switchType.setEnabled(false);
        m_rbtnRed = findViewById(R.id.radiobtn_red);
        m_rbtnYellow = findViewById(R.id.radiobtn_yellow);
        m_rbtnGreen = findViewById(R.id.radiobtn_green);
        m_rbtnBlue = findViewById(R.id.radiobtn_blue);
        radioGroup = findViewById(R.id.radioGroup);
    }

    class MyGridviewAdapter extends BaseAdapter {

        @Override
        public int getCount() { return test.length; }

        @Override
        public Object getItem(int position) { return test[position]; }

        @Override
        public long getItemId(int position) { return position; }

        public void update(boolean boo){ notifyDataSetChanged(); }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            final ViewHolder holder;

            if (view== null){
                LayoutInflater inflater = LayoutInflater.from(BingoActivity.this);
                view = inflater.inflate(R.layout.view_gridview_item, null);
                holder = new ViewHolder();
                holder.etContent = view.findViewById(R.id.editText_square);
                holder.etContent.setTag(position); // 將每一格etcontent設tag
                holder.myTextWatcher = new MyTextWatcher(holder.etContent); // 宣告將設置tag的etcontent傳入textWatcher

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.etContent.addTextChangedListener(holder.myTextWatcher); // 給eycontent設置textwatcher

            if(boo){
                holder.etContent.setFocusable(false);//switch自動時不能編輯
                holder.etContent.setSelected(true);// 先設true他一開始就會進到isSelected
            }else {
                holder.etContent.setFocusable(true);//手動時可以編輯
                holder.etContent.setFocusableInTouchMode(true);
                holder.etContent.setBackgroundColor(0x80a9a9a9);
            }

            //Bingo格子依序填值
            holder.etContent.setText(test[position]);

            holder.etContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!holder.etContent.isFocusable() && m_switchType.isChecked()){//當不是可編輯時才變色
                        if (holder.etContent.isSelected()) { // 被選到時

                            switch (iColorChange){
                                case 0:
                                    holder.etContent.setBackgroundColor(0x80ff0000); // 變紅色
                                    break;
                                case 1:
                                    holder.etContent.setBackgroundColor(0x80ffff00); // 變黃色
                                    break;
                                case 2:
                                    holder.etContent.setBackgroundColor(0x8000bfff); // 變藍色
                                    break;
                                case 3:
                                    holder.etContent.setBackgroundColor(0x80003300); // 變綠色
                                    break;
                                default:
                                    holder.etContent.setBackgroundColor(0x80ff0000); // 沒有選就預設點到時變紅色
                                    break;
                            }

                            holder.etContent.setSelected(false);
                            status[position % m_iTableSize][position / m_iTableSize]=true;
                        }else{
                            holder.etContent.setSelected(true);
                            holder.etContent.setBackgroundColor(0x80a9a9a9);// 變灰色
                            status[position % m_iTableSize][position / m_iTableSize]=false;
                        }

                        for(int i=0;i<m_iTableSize*2+2;i++){
                            line[i] = true;
                        }

                        // Bingo連線測試
                        for(int i = 0; i < m_iTableSize; i++){
                            for (int j = 0; j < m_iTableSize; j++){
                                line[i] = line[i] & status[i][j]; // 測 0 1 2 橫條
                                line[i + m_iTableSize] = line[i + m_iTableSize] & status[j][i]; // 測 3 4 5 直條
                            }
                            line[m_iTableSize*2] = line[m_iTableSize*2] & status[i][i]; // 測 6 斜條
                            line[m_iTableSize*2+1] = line[m_iTableSize*2+1] & status[i][m_iTableSize-i-1]; // 測 7 斜條
                        }

                        for(int i = 0;i<line.length;i++){
                            if(line[i]){
                                new AlertDialog.Builder(BingoActivity.this,R.style.buttonDialog)
                                        .setTitle("Bingo !! ")
                                        .setCancelable(false)//點選對話方塊以外的區域是否讓對話方塊消失
                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                m_switchType.setChecked(false);
                                            }
                                        }).setNegativeButton("重新開始", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BingoActivity.this.recreate();
                                        m_etTableSize.setText("");
                                        m_switchType.setChecked(false);
                                    }
                                }).create()
                                        .show();
                                break;
                            }
                        }
                    }
                }
            });

            return view;
        }

        class ViewHolder {
            EditText etContent;
            MyTextWatcher myTextWatcher;
        }


        private class MyTextWatcher implements TextWatcher{
            private EditText etHolderContainer ;

            MyTextWatcher (EditText etTextWatcher){ // 宣告時傳入etContent
                this.etHolderContainer = etTextWatcher; //  用etHolderContainer去存etContent
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                test[(int)etHolderContainer.getTag()] = s.toString(); // 在輸入模式更改值將值更新

                boolean booEmpty = true;
                if(TextUtils.isEmpty(s.toString())){
                    booEmpty = false;
                }

                if(!booEmpty){ // 有空值時
                    Toast.makeText(BingoActivity.this,"尚有空格未填入數值",Toast.LENGTH_SHORT).show();
                    m_switchType.setClickable(false); // 當輸入空值時不能切換到遊戲模式
                }else {
                    m_switchType.setClickable(true);
                }

                boolean booRepeat = true;
                for(int i=0;i<test.length;i++){
                    if(i != (int)etHolderContainer.getTag()){ // 不等於自己那格時
                        if(test[i].equals(s.toString())){ // 去測他們有無重複
                            booRepeat = false;
                            break;
                        }
                    }
                }

                if (!booRepeat) { // 有重複時
                    Toast.makeText(BingoActivity.this, "有重複的數字請重新輸入", Toast.LENGTH_SHORT).show();
                    m_switchType.setEnabled(false);
                }else {
                    m_switchType.setEnabled(true);
                }
            }
        }
    }
}
