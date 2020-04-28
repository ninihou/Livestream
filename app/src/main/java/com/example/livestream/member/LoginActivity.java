package com.example.livestream.member;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.livestream.R;
import com.example.livestream.main.MainActivity;
import com.example.livestream.main.Util;
import com.example.livestream.task.CommonTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.AbstractList;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextView tvMessage;
    private CommonTask isMemberTask, MemberIdTask;
    private EditText etUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvMessage = findViewById(R.id.tvMessage);
        setResult(RESULT_CANCELED);
    }

    protected void onStart(){ //check
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        boolean login = preferences.getBoolean("login", false);
        if (login) {
            String account = preferences.getString("account", "");
            String password = preferences.getString("password", "");

            if (isMember(account, password)) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private void showMessage(int msgResId) {
        tvMessage.setText(msgResId);
    }

    public void onLoginClick(View view) {
        EditText etUser = findViewById(R.id.etUser);
        this.etUser = etUser;
        //========抓account並放到main
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("userName", this.etUser.getText().toString().trim());
//        startActivity(intent);
        //========
        EditText etPassword = findViewById(R.id.etPassword);
        String account = etUser.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (account.length() <= 0 || password.length() <= 0) {
            showMessage(R.string.msg_InvalidUserOrPassword);
            return;
        }

        if (isMember(account, password)) {
            SharedPreferences preferences = getSharedPreferences(
                    Util.PREF_FILE, MODE_PRIVATE);
            preferences.edit().putBoolean("login", true)
                    .putString("account", account)
                    .putString("password", password).apply();

            String account1 = preferences.getString("account", "");
            String url = Util.URL + "MemberServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findIdByAccount");
            jsonObject.addProperty("account", account1);
            String jsonOut = jsonObject.toString();
            MemberIdTask = new CommonTask(url, jsonOut);
            try {
                String member_id = MemberIdTask.execute().get();
                JsonObject jsonObject1 = new JsonParser().parse(member_id).getAsJsonObject();
                String a= jsonObject1.get("account").getAsString();
                Log.e("jsonObject1", a);
                preferences.edit().putString("member_id", a).apply();
//                Log.e("member_id", member_id);
//                String yo = member_id.substring(str.indexOf(":")+2, str.indexOf("}")-2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            finish();
        } else {
            showMessage(R.string.msg_InvalidUserOrPassword);
        }
    }

    @Override
    public void finish(){
        Intent data = new Intent();
        data.putExtra("userName", this.etUser.getText().toString().trim());
        setResult(RESULT_OK, data);
        Log.e("why", "www");
        super.finish();
    }

//    public void clickAllChat(View view) {
//        Intent intent = new Intent(this, TogetherChatActivity.class);
//        intent.putExtra("userName", etUserName.getText().toString().trim());
//        startActivity(intent);
//    }

    public void onNewAccountClick(View view) {
        Intent intent = new Intent(this, signUpActivity.class);
        startActivity(intent);
    }

    private boolean isMember(final String account, final String password) {
        boolean isMember = false;
        if (Util.networkConnected(this)) {
            String url = Util.URL + "MemberServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "isMember");
            jsonObject.addProperty("account", account);
            jsonObject.addProperty("password", password);
            String jsonOut = jsonObject.toString();
            isMemberTask = new CommonTask(url, jsonOut);
            try {
                String result = isMemberTask.execute().get();
                isMember = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                isMember = false;
            }
        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return isMember;
    }

    protected void onStop() {
        super.onStop();
        if (isMemberTask != null) {
            isMemberTask.cancel(true);
        }
    }
}
