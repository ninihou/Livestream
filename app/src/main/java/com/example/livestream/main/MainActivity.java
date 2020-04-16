package com.example.livestream.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.livestream.member.signUpActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView adRecycleView;
//    private RecyclerView RecycleViewLs;
    private List<Chief> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adRecycleView = findViewById(R.id.adRecycleView);
        //RecycleViewLs.findViewById(R.id.RecycleViewLs);


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

    public void onLoginClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onLogoutClick(View view){
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

            private ViewHolder(View view){
                super(view);
                cardPic = view.findViewById(R.id.cardPic);
                cardName = view.findViewById(R.id.cardName);
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
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(MainActivity.this, team.getName(), Toast.LENGTH_SHORT).show();
//                }
//            });
        }



    }

    public void onTrailerClick(View view){
        Intent intent = new Intent(this, LsTrailerActivity.class);
        startActivity(intent);
    }


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
