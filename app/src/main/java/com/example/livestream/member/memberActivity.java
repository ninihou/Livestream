package com.example.livestream.member;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livestream.Chief;
import com.example.livestream.R;
import com.example.livestream.fragment_member.ThreeFragment;
import com.example.livestream.main.LiveChatActivity;
import com.example.livestream.main.MainActivity;
import com.example.livestream.main.Util;
import com.example.livestream.task.CommonTask;
import com.example.livestream.task.ImageTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import goldzweigapps.tabs.Builder.EasyTabsBuilder;
import goldzweigapps.tabs.Colors.EasyTabsColors;
import goldzweigapps.tabs.Interface.TabsListener;
import goldzweigapps.tabs.Items.TabItem;
import goldzweigapps.tabs.View.EasyTabs;
import goldzweigapps.tabs.transforms.EasyRotateUpTransformer;

public class memberActivity extends AppCompatActivity {

    private ImageView picture;
    private TextView tvBalance;
    private ImageTask photoTask;
    private CommonTask commonTask, lsCommonTask;
    private String account;
    private RecyclerView myRecyclerView;
    private LinearLayoutManager linearLayoutManager;
//    private TabLayout myTabLayout;
    private List<Chief> cardList;
    ArrayList<String> arrayList = new ArrayList<>();

    private final static String TAG = "memberActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        findViews();
        EasyTabs tabs = (EasyTabs) findViewById(R.id.easyTabs);
        EasyTabsBuilder.with(tabs)
                 .addTabs(
                new TabItem(new ThreeFragment(),"收藏直播"),
                new TabItem(new ThreeFragment(), "收藏廚師"),
                new TabItem(new ThreeFragment(),"收藏食譜"))
                .setTabsBackgroundColor(EasyTabsColors.Cornsilk)
                .setIndicatorColor(EasyTabsColors.Aqua)
                .setTextColors(EasyTabsColors.Moccasin,EasyTabsColors.Gray)
                .hideAllTitles(false)
                .addIcons(
                        R.mipmap.ic_launcher,
                        R.drawable.chocolate_donut,
                        R.mipmap.ic_launcher_round)
                .setIconFading(false)
                .withListener(new TabsListener() {
                    @Override
                    public void onScreenPosition(int position) {
                        Toast.makeText(getApplicationContext(),String.valueOf(position),Toast.LENGTH_SHORT).show();
                    }
                })
                .addTransformation(true,new EasyRotateUpTransformer())
                .Build();

        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        preferences.edit().putBoolean("login", true).apply();
        String account = preferences.getString("account","");
        Member member = (Member) this.getIntent().getSerializableExtra("member");
        showMemberPic(member);
        showBalance(account);
    }

    private void findViews() {
        picture = findViewById(R.id.picture);
        tvBalance = findViewById(R.id.tvBalance);
//        myRecyclerView = findViewById(R.id.myRecyclerView);
    }

    protected void onStart(){
        super.onStart();
        if (Util.networkConnected(this)){
            //String jsonOut = jsonObject.toString();
           // livestream();
        }else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
    }

//    private void livestream() {
//        if (Util.networkConnected(this)) {
//            lsCommonTask = new CommonTask(Util.URL + "CollectionServlet", jsonOut);
//            String url = Util.URL + "CollectionServlet";
//            List<LsCollection> lsCollection = null;
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAll");
//        }
//    }

    public void onEditClick(View view){
        Intent intent = new Intent(this, MemberEditActivity.class);
        startActivity(intent);
    }
    private void showMemberPic(Member member){
        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        preferences.edit().putBoolean("login", true).apply();
        String account = preferences.getString("account","");
        Log.e("test", account);

        String url = Util.URL + "MemberServlet";
       // String account2 = member.getAccount();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            photoTask = new ImageTask(url, account, imageSize);
            bitmap = photoTask.execute().get();
            Log.e("test1", "test1");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (bitmap != null) {
            picture.setImageBitmap(bitmap);
        } else {
            picture.setImageResource(R.drawable.camera);
        }

    }

    public void showBalance(String account) {
        String url = Util.URL + "MemberServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "findBalanceByAccount");
        jsonObject.addProperty("account", account);
        String jsonOut = jsonObject.toString();
        commonTask = new CommonTask(url, jsonOut);
        try {
            String result = commonTask.execute().get();
            JsonObject jsonObject1 = new JsonParser().parse(result).getAsJsonObject();

            tvBalance.setText(jsonObject1.get("account").getAsString());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
//=================================================
//    private class ChiefAdapter extends RecyclerView.Adapter<memberActivity.ChiefAdapter.ViewHolder>{
//        private List<Chief> cardList;
//
//        private ChiefAdapter(List<Chief> cardList) {
//            this.cardList = cardList;
//        }
//
//        class ViewHolder extends RecyclerView.ViewHolder{
//
//            private ImageView ivLsPicture;
//            private TextView tvTitle;
//
//            public ViewHolder(View view){
//                super(view);
//                ivLsPicture = view.findViewById(R.id.ivLsPicture);
//                tvTitle = view.findViewById(R.id.tvTitle);
//            }
//
//            public void setItem(ImageView item){
//                ivLsPicture = item;
//                tvTitle.setText((CharSequence) item);
//            }
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return cardList.size();
//        }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_trailer, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final Chief team = cardList.get(position);
//        holder.cardPic.setImageResource(team.getCard());
//        holder.cardName.setText(team.getFood());
//        // itemView為ViewHolder內建屬性(指的就是每一列)
////        holder.itemView.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                //Toast.makeText(MainActivity.this, team.getName(), Toast.LENGTH_SHORT).show();
////                Intent intent = new Intent(memberActivity.this, LiveChatActivity.class);
////                intent.putExtra("userName", tvUserName.getText().toString().trim());
////                startActivity(intent);
////            }
////        });
//    }
//    }
//===============================================================================================

    @Override
    protected void onStop() {
        super.onStop();
        if (commonTask != null) {
            commonTask.cancel(true);
        }

        if (photoTask != null) {
            photoTask.cancel(true);
        }
//        if (categoryTask != null) {
//            categoryTask.cancel(true);
//        }
    }

}
