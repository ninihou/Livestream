package com.example.livestream.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.livestream.R;
import com.example.livestream.task.CommonTask;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

public class SeeTrailerActivity extends AppCompatActivity {
    private static final String TAG = "SeeTrailerActivity";
    private TextView tvMember_id;
    private CommonTask seeTrailerTask;

    SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
            MODE_PRIVATE);
    //        preferences.edit().putBoolean("login", true).apply();
    String member_id = preferences.getString("member_id","");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_trailer);

        findViews();
        setMemberId();
        setTrailer();
    }

    private void findViews(){
        tvMember_id = findViewById(R.id.tvMember_id);
    }

    private void setMemberId(){

        tvMember_id.setText("廚師ID : "+ member_id);
    }

    private void setTrailer(){
        String url = Util.URL + "AnLivestreamServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "findTrByStatus");
        jsonObject.addProperty("member_id", member_id);
        String jsonOut = jsonObject.toString();
        seeTrailerTask = new CommonTask(url, jsonOut);
        try {
            String result = seeTrailerTask.execute().get();
            JSONArray lsArray = new JSONArray(result);
            Log.e("lsArray", String.valueOf(lsArray));
            JSONObject jobj = lsArray.getJSONObject(0);
           // isSuccess = Boolean.valueOf(result);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }



}
