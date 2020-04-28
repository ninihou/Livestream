package com.example.livestream.main;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.livestream.Chief;
import com.example.livestream.R;
import com.example.livestream.member.LoginActivity;
import com.example.livestream.member.memberActivity;
import com.example.livestream.member.signUpActivity;
import com.example.livestream.task.CommonTask;
import com.example.livestream.task.CommonTaskMain;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView adRecycleView;
    private List<Chief> cardList;
    private TextView tvUserName;
    private String name;
    private CommonTaskMain memberTask;
    private String userName;
    private Button btMember, btnLight;
    private Dialog myDialog;
    private ImageView ivMakeTrailer, ivWatchTailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        pref.edit().putBoolean("login", false).apply();
            //showUserName(userName);//in textView

//        if(isLoggedIn()){
//            userName = getIntent().getStringExtra("userName");
//             Log.e("userName", userName);
//        }


        adRecycleView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.HORIZONTAL));
        cardList = new ArrayList<>();
        cardList.add(new Chief(R.drawable.churro, "西班牙油條"));
        cardList.add(new Chief(R.drawable.heart_pizza, "愛心披薩"));
        cardList.add(new Chief(R.drawable.shrimp, "蝦蝦"));

//        RecycleViewLs.setLayoutManager(
//                new StaggeredGridLayoutManager(
//                        1, StaggeredGridLayoutManager.HORIZONTAL));

        adRecycleView.setAdapter(new ChiefAdapter(cardList));
    }

    public void findViews(){
        adRecycleView = findViewById(R.id.adRecycleView);
        tvUserName = findViewById(R.id.tvUserName);
        btMember = findViewById(R.id.btMember);
        btnLight = findViewById(R.id.btnLight);
        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog = new Dialog(MainActivity.this);
                myDialog.setTitle(getString(R.string.dialog_light));

                myDialog.setCancelable(true);
                myDialog.setContentView(R.layout.dialog_lightbox);
                Window dialogWindow = myDialog.getWindow();
                dialogWindow.setGravity(Gravity.CENTER);
                WindowManager m = getWindowManager();

//                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//                lp.width = 1000;
//                lp.alpha = 1.0f;
//                dialogWindow.setAttributes(lp);

                Display d = m.getDefaultDisplay(); // 取得螢幕寬、高用
                WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 獲取對話視窗當前的参數值
                p.height = (int) (d.getHeight() * 0.6); // 高度設置為螢幕的0.6 (60%)
                p.width = (int) (d.getWidth() * 0.95); // 寬度設置為螢幕的0.95 (95%)
                dialogWindow.setAttributes(p);

                ivMakeTrailer = myDialog.findViewById(R.id.ivMakeTrailer);
                ivMakeTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, LsTrailerActivity.class);
                        startActivity(intent);
                    }
                });

                ivWatchTailer = myDialog.findViewById(R.id.ivWatchTailer);
                ivWatchTailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, SeeTrailerActivity.class);
                        startActivity(intent);
                    }
                });
                myDialog.show();
            }
        });
    }


    public void showUserName(String userName) {
        String url = Util.URL + "MemberServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "findNameByAccount");
        jsonObject.addProperty("account", userName);
        String jsonOut = jsonObject.toString();
        memberTask = new CommonTaskMain(url, jsonOut);
        try {
            String result = memberTask.execute().get();
            JsonObject jsonObject1 = new JsonParser().parse(result).getAsJsonObject();

            tvUserName.setText(jsonObject1.get("account").getAsString());
            // isMember = Boolean.valueOf(result);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        // String userName = getString(R.string.hiUser) + ": " + result;
    }

//    public boolean isLoggedIn() {
//        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,
//                MODE_PRIVATE);
//        return pref.getBoolean ("login", false);
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (memberTask != null) {
            memberTask.cancel(true);
        }
    }

    public void onLoginClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);//intent throw 1 to LoginActivity
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode && requestCode ==1){
            userName = data.getStringExtra("userName");
            showUserName(userName);
            //Log.e("userName", userName);
        }
    }

    public void onLogoutClick(View view){
        tvUserName.setText("未登入");
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        pref.edit().putBoolean("login", false).apply();
//        view.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class ChiefAdapter extends RecyclerView.Adapter<ChiefAdapter.ViewHolder>{
        private List<Chief> cardList;

        private ChiefAdapter(List<Chief> cardList) {
            this.cardList = cardList;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private ImageView cardPic;
            private TextView cardName;

            public ViewHolder(View view){
                super(view);
                cardPic = view.findViewById(R.id.cardPic);
                cardName = view.findViewById(R.id.cardName);
            }

            public void setItem(ImageView item){
                cardPic = item;
                cardName.setText((CharSequence) item);
            }

        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_trailer, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Chief team = cardList.get(position);
            holder.cardPic.setImageResource(team.getCard());
            holder.cardName.setText(team.getFood());
            // itemView為ViewHolder內建屬性(指的就是每一列)
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(MainActivity.this, team.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LiveChatActivity.class);
                    intent.putExtra("userName", tvUserName.getText().toString().trim());
                    startActivity(intent);
                }
            });
        }



    }

    public void onMemberClick(View view){
        Intent intent = new Intent(this, memberActivity.class);
        startActivity(intent);
    }

    public void onTrailerClick(View view){
        Intent intent = new Intent(this, LsTrailerActivity.class);
        startActivity(intent);
    }

//    public void onLiveClick(View view){
//        Intent intent = new Intent(this, LiveChatActivity.class);
//        intent.putExtra("userName", tvUserName.getText().toString().trim());
//        startActivity(intent);
//    }


    private final static int REQ_PERMISSIONS = 0;
    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();
    }

    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int result = ContextCompat.checkSelfPermission(this, permissions[0]);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQ_PERMISSIONS);
        }
    }
}
