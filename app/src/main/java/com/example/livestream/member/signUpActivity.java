package com.example.livestream.member;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.livestream.main.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import com.example.livestream.R;
import com.example.livestream.main.Util;
import com.example.livestream.task.CommonTask;

import org.json.JSONObject;

public class signUpActivity extends AppCompatActivity {

    private static final String TAG = "signUpActivity";
    private EditText etUserId;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etName;
    private EditText etEmail;
    private TextView tvMessage;
    private boolean memberExist = false;
    private CommonTask memberExistTask, memberRegisterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
    }

    private void findViews() {
        etUserId = findViewById(R.id.tvUserId);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        tvMessage = findViewById(R.id.tvMessage);

        // check if the id is been used
        etUserId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Util.networkConnected(signUpActivity.this)) {
                        String url = Util.URL + "MemberServlet";
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "isUserIdExist");
                        jsonObject.addProperty("account", etUserId.getText().toString());
                        String jsonOut = jsonObject.toString();
                        memberExistTask = new CommonTask(url, jsonOut);
                        try {
                            String result = memberExistTask.execute().get();
                            memberExist = Boolean.valueOf(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        // show an error message if the id exists;
                        // otherwise, the error message should be clear
                        if (memberExist) {
                            tvMessage.setText(R.string.msg_UserIdExist);
                        } else {
                            tvMessage.setText(null);
                        }
                    }
                }
            }
        });
    }

    public void onSubmitClick(View view){
        String account = etUserId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        String message = "";
        boolean isInputValid = true;
        if (memberExist){
            message += getString(R.string.msg_UserIdExist) + "\n";
            isInputValid = false;
        }
        if (account.isEmpty()){
            message += "Account" + "" + "Cannot be empty" + "\n";
            isInputValid = false;
        }
        if (password.isEmpty()) {
            message += getString(R.string.hint_etPassword) + " "
                    + getString(R.string.msg_InputEmpty) + "\n";
            isInputValid = false;
        }
        if (!confirmPassword.equals(password)) {
            message += getString(R.string.msg_ConfirmPasswordNotSameAsPassword);
            isInputValid = false;
        }
        if (name.isEmpty()) {
            message += getString(R.string.text_etName) + " "
                    + getString(R.string.msg_InputEmpty) + "\n";
            isInputValid = false;
        }
        if (email.isEmpty()) {
            message += getString(R.string.text_etEmail) + " "
                    + getString(R.string.msg_InputEmpty) + "\n";
            isInputValid = false;
        }
        tvMessage.setText(message);
        Member member = new Member(account, password, name, email);
        if (isInputValid) {
            if (Util.networkConnected(this)) {
                String url = Util.URL + "MemberServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "add");
                jsonObject.addProperty("member", new Gson().toJson(member));
                String jsonOut = jsonObject.toString();
                memberRegisterTask = new CommonTask(url, jsonOut);
                boolean isSuccess = false;
                try {
                    String result = memberRegisterTask.execute().get();
                    isSuccess = Boolean.valueOf(result);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (isSuccess) {
                    // user ID and password will be saved in the preferences file
                    // and starts UserActivity
                    // while the user account is created successfully
                    SharedPreferences preferences = getSharedPreferences(
                            Util.PREF_FILE, MODE_PRIVATE);
                    preferences.edit().putBoolean("login", true)
                            .putString("account", account)
                            .putString("password", password).apply();
                    Util.showToast(this, R.string.msg_SuccessfullyCreateAccount);
                    Intent intent = new Intent(this, MainActivity.class);//where to turn~~~
                    startActivity(intent);
                } else {
                    tvMessage.setText(R.string.msg_FailCreateAccount);
                }
            }
        }
    }

}
