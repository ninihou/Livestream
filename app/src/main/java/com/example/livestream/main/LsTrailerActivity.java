package com.example.livestream.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;

import com.example.livestream.R;
import com.example.livestream.task.CommonTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class LsTrailerActivity extends AppCompatActivity {
    private static final int REQ_TAKE_PICTURE = 0;

    private static final String TAG = "LsTrailerActivity";

   // private TextView tvDateTime;
    private EditText etTitle;
    private EditText etIntro;
    private ImageView ivTakePic;
    private CommonTask lsTrailerTask;
    private Button btUpload;


    private static TextView tvDate, tvTime;
    private static int year, month, day, hour, minute;
    private File file;
    private Bitmap picture;
    private AsyncTask dataUploadTask;
    private int resultCode;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ls_trailer);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        showDateNow();//呼叫顯示現在日期
        showTimeNow();//呼叫顯示現在時間
        tvMessage = findViewById(R.id.tvMessage);
        ivTakePic = findViewById(R.id.ivTakePic);
        btUpload = findViewById(R.id.btUpload);
        etTitle = findViewById(R.id.etTitle);
        etIntro = findViewById(R.id.etIntro);



        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isInputValid = true;
                if (picture == null) {
                    showToast(LsTrailerActivity.this, R.string.msg_NotUploadWithoutPicture);
                    isInputValid = false;
                }

                String title = etTitle.getText().toString().trim();
                String intro = etIntro.getText().toString().trim();
                String date = tvDate.getText().toString();

                byte[] pic = Bitmap2Bytes(picture);
                String message = "";
                SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
                        MODE_PRIVATE);
                String member_id = preferences.getString("member_id","");
                Log.e("mjm", member_id);

                if (title.isEmpty()) {
                    message += getString(R.string.text_Title) + " "
                            + getString(R.string.msg_InputEmpty) + "\n";
                    isInputValid = false;
                }
                if (intro.isEmpty()) {
                    message += getString(R.string.text_Intro) + " "
                            + getString(R.string.msg_InputEmpty) + "\n";
                    isInputValid = false;
                }
                if (date.isEmpty()){
                    message += getString(R.string.dtime) + " "
                            + getString(R.string.msg_InputEmpty) + "\n";
                    isInputValid = false;
                }
                tvMessage.setText(message);
                if (isInputValid){
                    if (Util.networkConnected(LsTrailerActivity.this)) {

                        String url = Util.URL + "AnLivestreamServlet";
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "insert");
                        jsonObject.addProperty("member_id", member_id);
                        jsonObject.addProperty("date", date);
                        jsonObject.addProperty("title", title);
                        jsonObject.addProperty("intro", intro);
                        jsonObject.addProperty("pic", Base64.encodeToString(pic,Base64.DEFAULT));
//                jsonObject.addProperty("member", new Gson().toJson(member));
                        String jsonOut = jsonObject.toString();
                        lsTrailerTask = new CommonTask(url, jsonOut);


                boolean isSuccess = false;
                try {
                    String result = lsTrailerTask.execute().get();
                    isSuccess = Boolean.valueOf(result);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                    }
                }
            }
        });

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private static void showDateNow() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        updateDateInfo();
    }

    private static void showTimeNow() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        updateTimeInfo();
    }

    // 將指定的日期顯示在TextView上
    private static void updateDateInfo() {
        tvDate.setText(new StringBuilder().append(year).append("-")
                //「month + 1」是因為一月的值是0而非1
                .append(parseNum(month + 1)).append("-").append(parseNum(day)));
    }

    // 將指定的日期顯示在TextView上
    private static void updateTimeInfo() {
        tvTime.setText(new StringBuilder().append(parseNum(hour)).append(":").append(parseNum(minute)));
    }


    // 若數字有十位數，直接顯示；若只有個位數則補0後再顯示。例如7會改成07後再顯示
    private static String parseNum(int day) {
        if (day >= 10)
            return String.valueOf(day);
        else
            return "0" + String.valueOf(day);
    }

    // 按下「日期」按鈕
    public void onDateClick(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        FragmentManager fm = getSupportFragmentManager();
        datePickerFragment.show(fm, "datePicker");
    }

    // 按下「時間」按鈕
    public void onTimeClick(View view) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        FragmentManager fm = getSupportFragmentManager();
        timePickerFragment.show(fm, "timePicker");
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        // 改寫此方法以提供Dialog內容
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // 建立DatePickerDialog物件
            // this為OnDateSetListener物件
            // year、month、day會成為日期挑選器預選的年月日
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(), this, year, month, day);

            //限制可選日期
            DatePicker picker = datePickerDialog.getDatePicker();
            picker.setMinDate(System.currentTimeMillis());//can't pick date before today

            return datePickerDialog;
        }

        @Override
        // 日期挑選完成會呼叫此方法，並傳入選取的年月日
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            year = y;
            month = m;
            day = d;
            updateDateInfo();
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        // 改寫此方法以提供Dialog內容
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // 建立TimePickerDialog物件
            // this為OnTimeSetListener物件
            // hour、minute會成為時間挑選器預選的時與分
            // false 設定是否為24小時制顯示
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getActivity(), this, hour, minute, false);//true的話會變成兩圈（24小時制
            return timePickerDialog;
        }

        @Override
        // 時間挑選完成會呼叫此方法，並傳入選取的時與分
        public void onTimeSet(TimePicker timePicker, int h, int m) {
            hour = h;
            minute = m;
            updateTimeInfo();
        }
    }

//taking pictures--------
    public void onTakePictureClick(View view) {
        Log.e("s","s");
        takePicture();

    }

    private void takePicture(){
        Log.e("a","a");
        long time= System.currentTimeMillis();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.e("b","b");
        //asign file path
        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file,  "picture.jpg");
        Log.e("c","c");
        Uri contentUri= FileProvider.getUriForFile(this, getPackageName()+".provider",file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri); //contentUri is for camera app
        Log.e("d","d");
        if (isIntentAvailable(this, intent)) {
            startActivityForResult(intent, REQ_TAKE_PICTURE);
        } else {
            showToast(this, R.string.msg_NoCameraApp);
        }
    }

    private void showToast(Context context, int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.resultCode = resultCode;
        super.onActivityResult(requestCode, resultCode, data);
//        showToast(this, R.string.msg_NoCameraApp);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 手機拍照App拍照完成後可以取得照片圖檔
                case REQ_TAKE_PICTURE:
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    // inSampleSize值即為縮放的倍數 (數字越大縮越多)
                    opt.inSampleSize = Util.getImageScale(file.getPath(), 640, 1280);
                    picture = BitmapFactory.decodeFile(file.getPath(), opt);
                    ivTakePic.setImageBitmap(picture);
                    break;
            }
        }
    }

//    private String getRemoteData(String url, String jsonOut) throws IOException {
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setDoInput(true); // allow inputs
//        connection.setDoOutput(true); // allow outputs
//        connection.setUseCaches(false); // do not use a cached copy
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("charset", "UTF-8");
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
//        bw.write(jsonOut);
//        Log.d(TAG, "jsonOut: " + jsonOut);
//        bw.close();
//
//        int responseCode = connection.getResponseCode();
//        StringBuilder jsonIn = new StringBuilder();
//        if (responseCode == 200) {
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line;
//            while ((line = br.readLine()) != null) {
//                jsonIn.append(line);
//            }
//        } else {
//            Log.d(TAG, "response code: " + responseCode);
//        }
//        connection.disconnect();
//        Log.d(TAG, "jsonIn: " + jsonIn);
//        return jsonIn.toString();
//    }

    @Override
    protected void onPause() {
        if (dataUploadTask != null) {
            dataUploadTask.cancel(true);
        }
        super.onPause();
    }

    private final static int REQ_PERMISSIONS = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSIONS:
                String text = "";
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        text += permissions[i] + "\n";
                    }
                }
                if (!text.isEmpty()) {
                    text += getString(R.string.text_NotGranted);
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

        //把拍到的照片轉成String
    public byte[] Bitmap2Bytes(Bitmap bm) {
        if (bm == null) {
            showToast(LsTrailerActivity.this, R.string.msg_NotUploadWithoutPicture);
            //isInputValid = false;
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
