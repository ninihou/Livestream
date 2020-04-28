package com.example.livestream.main;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livestream.R;
import com.example.livestream.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;

public class LiveChatActivity extends AppCompatActivity { //together

    private static final String TAG = "LiveChatActivity";
//    private static final String SERVER_URI = "ws://172.20.10.6:8081/DA106_G4/TogetherWS/";//My phone
//    private static final String SERVER_URI = "ws://192.168.196.157:8081/DA106_G4/TogetherWS/";//Tibame
    private static final String SERVER_URI = "wss://172.20.10.10:8443/WebSocketChatWeb/TogetherWS/";
    private MyWebSocketClient myWebSocketClient;
    private WebView webView;
    private TextView tvMessage;
    private EditText etMessage;
    private Button btSend, btConnect, btDisconnect;
    private ScrollView scrollView;
    private String userNameMe;
    private URI uri;
    private TextView tvDonate;
    private Button btnDonate;
    private Animator animator, animatorC;
    private Dialog myDialog;
    private ImageView ivApple, ivCarrot;
    private ImageView dialogA, dialogC;
    private Interpolator interpolator = new LinearInterpolator();
    private CommonTask chatTask, donationTask;

//    @SuppressLint("SetJavaScriptEnabled") //把警告隱藏（20行
    private class MyWebSocketClient extends WebSocketClient { //not UI thread, can't操作元件in these method below

        MyWebSocketClient(URI serverURI) {
            // Draft_17是連接協議，就是標準的RFC 6455（JSR356）
            super(serverURI, new Draft_17());
        }

        //first connection will 執行onOpen
        @Override
        public void onOpen(ServerHandshake handshakeData) {//ServerHandshake連線交握, 檢查連線相關資訊
            runOnUiThread(new Runnable() { //runOnUiThread從activity父類別得到
                @Override
                public void run() {
                    changeConnectStatus(true);
                }
            });//對畫面控制
            String text = String.format(Locale.getDefault(),
                    "onOpen: Http status code = %d; status message = %s",
                    handshakeData.getHttpStatus(),
                    handshakeData.getHttpStatusMessage());

            Log.d(TAG, text);
        }

        @Override
        public void onMessage(final String message) { //網頁端推播訊息時，安著這邊也會執行onMessage
            Log.d(TAG, "onMessage: " + message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String type = jsonObject.get("type").toString();
                        String userName = jsonObject.get("userName").toString();
                        String message = jsonObject.get("message").toString();

                        if("sendText".equals(type)){

                            String text = userName + ": " + message + "\n";
                            tvMessage.append(text);
                        }else if("sendApple".equals(type)) {
                            String text = userName + ": " + message + "\n";
                            tvMessage.append(text);
                            if(!(userName.equals(userNameMe))) {
                                //=======================================================================
                                animator = getAnimSet();
                                animator.addListener(new Animator.AnimatorListener() {
                                    /* 動畫開始時呼叫此方法 */
                                    @Override
                                    public void onAnimationStart(Animator animator) {
//                                        donate();//==============
//                                        donateRecord();
                                        Log.d("Animation", "onAnimationStart");
                                    }

                                    /* 動畫結束時呼叫此方法 */
                                    @Override
                                    public void onAnimationEnd(Animator animator) {
//                ivApple.setVisibility(View.INVISIBLE);
                                        tvDonate.setText("");
                                        Log.d("Animation", "onAnimationEnd");
                                    }

                                    /* 動畫被取消時呼叫此方法 */
                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                        Log.d("Animation", "onAnimationCancel");
                                    }

                                    /* 重複播放動畫時呼叫此方法 */
                                    @Override
                                    public void onAnimationRepeat(Animator animator) {
                                        Log.d("Animation", "onAnimationRepeat");
                                    }
                                });
                                /* ivSoccer元件套用並啟動選取的動畫 */
                                //animator.setTarget(ivApple);
                                animator.start();
                            }
                            //=======================================================
                        }else if("sendCarrot".equals(type)){
                            String text = userName + ": " + message + "\n";
                            tvMessage.append(text);
                            if(!(userName.equals(userNameMe))){
                                animatorC = getAnimSetC();
                                animatorC.addListener(new Animator.AnimatorListener() {
                                    /* 動畫開始時呼叫此方法 */
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                        donate();
                                        donateRecord();
                                        Log.e("e", "e");
                                        //ivCarrot.setVisibility(View.VISIBLE);
                                        Log.d("Animation", "onAnimationStart");
                                    }

                                    /* 動畫結束時呼叫此方法 */
                                    @Override
                                    public void onAnimationEnd(Animator animator) {
//                               ivCarrot.setVisibility(View.INVISIBLE);
                                        tvDonate.setText("");
                                        Log.d("Animation", "onAnimationEnd");
                                    }

                                    /* 動畫被取消時呼叫此方法 */
                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                        Log.d("Animation", "onAnimationCancel");
                                    }

                                    /* 重複播放動畫時呼叫此方法 */
                                    @Override
                                    public void onAnimationRepeat(Animator animator) {
                                        Log.d("Animation", "onAnimationRepeat");
                                    }
                                });
                                // animatorC.setTarget(ivCarrot);
                                animatorC.start();
                            }
                        }



                        scrollView.fullScroll(View.FOCUS_DOWN);//一有新訊息就卷到底
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeConnectStatus(false);
                }
            });
            String text = String.format(Locale.getDefault(),
                    "code = %d, reason = %s, remote = %b",
                    code, reason, remote);
            Log.d(TAG, "onClose: " + text);
        }

        @Override
        public void onError(Exception ex) {
            Log.d(TAG, "onError: exception = " + ex.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);
        findViews();
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        // 要允許不加密連線 (http) 就必須將usesCleartextTraffic設定為true
        webView.loadUrl("https://172.20.10.10:8443/RTCPeerConnection_Ver2/RTCPeerConnection.jsp#06711436105706126");
        // 如果沒有覆寫shouldOverrideUrlLoading()並回傳false，
        // 用戶點擊任何連結都會自動開啟手機內建的瀏覽器來呈現內容
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            // 在7.0時deprecated(還是有支援)，為了向下版本支援仍用此方法
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {

                LiveChatActivity.this.runOnUiThread(new Runnable(){
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                    }
                });

            }

        });

        WebViewClient mWebClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();
            }
        };


        webView.setWebViewClient(mWebClient);

        userNameMe = getIntent().getStringExtra("userName");
        Log.e("userName", userNameMe);//ok

        try {
            uri = new URI(SERVER_URI + "abc"); //connect to server endpoint  //ok
           //Log.e("fail", "fail");
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        myWebSocketClient = new MyWebSocketClient(uri);//參數透過類別傳入
        myWebSocketClient.connect();//需動手連結（web不需要
    }

    private void findViews() {
        webView = findViewById(R.id.webView);
        tvMessage = findViewById(R.id.tvMessage);
        etMessage = findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);
        btConnect = findViewById(R.id.btConnect);
        btDisconnect = findViewById(R.id.btDisconnect);
        scrollView = findViewById(R.id.scrollView);
       // tvDonate = findViewById(R.id.tvDonate);
        ivApple = findViewById(R.id.ivApple);//a
        ivCarrot = findViewById(R.id.ivCarrot);//a
        btnDonate = findViewById(R.id.btnDonate);
        btnDonate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                myDialog = new Dialog(LiveChatActivity.this);
                myDialog.setTitle(getString(R.string.dialog_title));

                // 使用者無法自行取消對話視窗，需要進行操作才行
                myDialog.setCancelable(true);//改成true的話就可以按別的鍵取消

                myDialog.setContentView(R.layout.dialog_donate);

                // 透過myDialog.getWindow()取得這個對話視窗的Window物件
                Window dialogWindow = myDialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);// 當参數值包含Gravity.BOTTOM時，對話視窗出現在下邊
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.height = 500;
                lp.width = 1100;
                lp.alpha = 0.8f;
                dialogWindow.setAttributes(lp);

                // 取得自訂對話視窗上的所有元件都需透過myDialog才能findViewById
                dialogA = myDialog.findViewById(R.id.dialogA);
                dialogA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        donate();//==============
                        donateRecord();
Log.e("check", "check");
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("type", "sendApple");
                        jsonObject.addProperty("userName", userNameMe);//change into ---
                        jsonObject.addProperty("message", "對廚師斗內了蘋果");//聊天訊息
                        myWebSocketClient.send(jsonObject.toString());
                        Log.d(TAG, "output: " + jsonObject.toString());


                        myDialog.cancel();//按下蘋果後關閉dialoge
                        if (animator != null) {
                            animator.cancel();
                            animator.removeAllListeners();
                            animator = null;
                        }
                       // tvDonate.setText(R.string.donate_apple);
                        animator = getAnimSet();
                        animator.addListener(new Animator.AnimatorListener() {
                            /* 動畫開始時呼叫此方法 */
                            @Override
                            public void onAnimationStart(Animator animator) {
//                                donate();//==============
//                                donateRecord();
                                Log.d("Animation", "onAnimationStart");
                            }

                            /* 動畫結束時呼叫此方法 */
                            @Override
                            public void onAnimationEnd(Animator animator) {
//                ivApple.setVisibility(View.INVISIBLE);
                                Log.d("Animation", "onAnimationEnd");
                            }

                            /* 動畫被取消時呼叫此方法 */
                            @Override
                            public void onAnimationCancel(Animator animator) {
                                Log.d("Animation", "onAnimationCancel");
                            }

                            /* 重複播放動畫時呼叫此方法 */
                            @Override
                            public void onAnimationRepeat(Animator animator) {
                                Log.d("Animation", "onAnimationRepeat");
                            }
                        });
                        /* ivSoccer元件套用並啟動選取的動畫 */
                         //animator.setTarget(ivApple);
                        animator.start();

                    }
                });

                dialogC = myDialog.findViewById(R.id.dialogC);
                dialogC.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        donate();//==============
                        donateRecord();
                        Log.e("checkCarrot", "checkCarrot");
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("type", "sendCarrot");
                        jsonObject.addProperty("userName", userNameMe);//change into ---
                        jsonObject.addProperty("message", "對廚師斗內了蘿蔔");//聊天訊息
                        myWebSocketClient.send(jsonObject.toString());
                        Log.d(TAG, "output: " + jsonObject.toString());

                        myDialog.cancel();//按下蘋果後關閉dialoge
                        if (animatorC != null) {
                            animatorC.cancel();
                            animatorC.removeAllListeners();
                            animatorC = null;
                        }
                        //tvDonate.setText(R.string.donate_carrot);
                        animatorC = getAnimSetC();
                        animatorC.addListener(new Animator.AnimatorListener() {
                            /* 動畫開始時呼叫此方法 */
                            @Override
                            public void onAnimationStart(Animator animator) {
                                donate();
                                donateRecord();
                                Log.e("e", "e");
                                //ivCarrot.setVisibility(View.VISIBLE);
                                Log.d("Animation", "onAnimationStart");
                            }

                            /* 動畫結束時呼叫此方法 */
                            @Override
                            public void onAnimationEnd(Animator animator) {
//                               ivCarrot.setVisibility(View.INVISIBLE);
                                Log.d("Animation", "onAnimationEnd");
                            }

                            /* 動畫被取消時呼叫此方法 */
                            @Override
                            public void onAnimationCancel(Animator animator) {
                                Log.d("Animation", "onAnimationCancel");
                            }

                            /* 重複播放動畫時呼叫此方法 */
                            @Override
                            public void onAnimationRepeat(Animator animator) {
                                Log.d("Animation", "onAnimationRepeat");
                            }
                        });
                       // animatorC.setTarget(ivCarrot);
                        animatorC.start();
                    }
                });
                myDialog.show();
            }
        });
    }
    //==========Donate扣錢===========

    public void donate(){
        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        String account = preferences.getString("account","");
        String url = Util.URL + "MemberServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "donate");
        jsonObject.addProperty("account", account);
        String jsonOut = jsonObject.toString();
        chatTask = new CommonTask(url, jsonOut);
        try {
            boolean isDonateSuccess = Boolean.valueOf(chatTask.execute().get());
            Log.e("isSuccess", String.valueOf(isDonateSuccess));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    //==========Donate紀錄===========
    public void donateRecord(){
        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        String member_id = preferences.getString("member_id","");
        Log.e("memidD", member_id);
        Integer donation_cost = 100;
       // Integer.toString(donation_cost);
       // String dc = donation_cost.toString
        String url = Util.URL + "DonationRServlet";
        Livestream livestream = new Livestream(member_id, donation_cost);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "donateR");
        jsonObject.addProperty("donation", new Gson().toJson(livestream));
        String jsonOut = jsonObject.toString();
        donationTask = new CommonTask(url, jsonOut);

        try {
            boolean result = Boolean.valueOf(donationTask.execute().get());
            Log.e("isDonationRSuccess", String.valueOf(result));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


//===============================================================================================================================
    /**
     * 建立位移動畫並套用當前特效====for apple
     */
    private ObjectAnimator getTranslateAnim() {
        /* 第1個參數為套用動畫的對象 - ivSoccer物件；
           第2個參數為要套用的屬性 - TRANSLATION_X代表水平位移；
           第3個參數為屬性設定值 - 從原點向右移動800像素 */
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofFloat(ivApple, View.TRANSLATION_Y, -100, -1000);
        /* 設定播放時間(預設300毫秒)為500毫秒，就是0.5秒 */
        objectAnimator.setDuration(500);
        /* 1代表重複播放1次；預設為0代表不重播；INFINITE代表無限重播 */
       // objectAnimator.setRepeatCount(1);
        /* REVERSE代表播放完畢後會反向播放；重複播放至少要設定1次才會反向播放 */
        //objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        /* 套用特效 */
        objectAnimator.setInterpolator(interpolator);
        return objectAnimator;
    }
    private ObjectAnimator getTranslateAnimO() {
        /* 第1個參數為套用動畫的對象 - ivSoccer物件；
           第2個參數為要套用的屬性 - TRANSLATION_X代表水平位移；
           第3個參數為屬性設定值 - 從原點向右移動800像素 */
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofFloat(ivApple, View.TRANSLATION_Y, -1000, -100);
        /* 設定播放時間(預設300毫秒)為500毫秒，就是0.5秒 */
        objectAnimator.setDuration(500);
        /* 1代表重複播放1次；預設為0代表不重播；INFINITE代表無限重播 */
        // objectAnimator.setRepeatCount(1);
        /* REVERSE代表播放完畢後會反向播放；重複播放至少要設定1次才會反向播放 */
       // objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        /* 套用特效 */
        objectAnimator.setInterpolator(interpolator);
        return objectAnimator;
    }

    /**
     * 建立位移動畫並套用當前特效====for carrot start
     */
    private ObjectAnimator getTranslateAnimC() {
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofFloat(ivCarrot, View.TRANSLATION_X, 0, 800);
        /* 設定播放時間(預設300毫秒)為500毫秒，就是0.5秒 */
        objectAnimator.setDuration(500);
        /* 1代表重複播放1次；預設為0代表不重播；INFINITE代表無限重播 */
         //objectAnimator.setRepeatCount(1);
        /* REVERSE代表播放完畢後會反向播放；重複播放至少要設定1次才會反向播放 */
       // objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        /* 套用特效 */
        objectAnimator.setInterpolator(interpolator);
        return objectAnimator;
    }
    //carrot over
    private ObjectAnimator getTranslateAnimCO() {
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofFloat(ivCarrot, View.TRANSLATION_X, 800, 0);
        /* 設定播放時間(預設300毫秒)為500毫秒，就是0.5秒 */
        objectAnimator.setDuration(500);
        /* 1代表重複播放1次；預設為0代表不重播；INFINITE代表無限重播 */
        //objectAnimator.setRepeatCount(1);
        /* REVERSE代表播放完畢後會反向播放；重複播放至少要設定1次才會反向播放 */
        //objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        /* 套用特效 */
        objectAnimator.setInterpolator(interpolator);
        return objectAnimator;
    }

    /**
     * 建立旋轉動畫並套用當前特效
     */
    private ObjectAnimator getRotateAnim() {
        /* 設定ivSoccer物件從0旋轉至360角度 */
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofFloat(ivApple, View.ROTATION, 0, 60);
        objectAnimator.setDuration(300);
        /* INFINITE代表無限重播 */
       // objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatCount(5);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setInterpolator(interpolator);
        return objectAnimator;
    }



    /**
     * 建立縮放動畫並套用當前特效 for carrot
     */
    private ObjectAnimator getScaleAnim() {
        /* 設定水平縮放：從原大小(1.0f)縮小至10%(0.1f)後再放大至2倍(2.0f)後再縮小至原大小 */
        PropertyValuesHolder holderX =
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.1f, 2.0f, 1.0f);
        /* 設定垂直縮放跟水平縮放效果一樣 */
        PropertyValuesHolder holderY =
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.1f, 2.0f, 1.0f);
        /* 將水平與垂直縮放等2個屬性設定套用在同一個ivSoccer物件上 */
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(ivCarrot, holderX, holderY);//contains x an y的縮放
        objectAnimator.setDuration(2000);
        objectAnimator.setInterpolator(interpolator);
        return objectAnimator;
    }

    /**
     * 建立集合動畫for apple
     */
    private AnimatorSet getAnimSet() {
        /* 建立集合動畫物件以加入多種動畫 */
        AnimatorSet animatorSet = new AnimatorSet();

        /* 播放完play()的動畫後再播放before()的動畫 */
        animatorSet.play(getTranslateAnim()).before(getRotateAnim());

        /* 播放完after()的動畫後再播放play()的動畫 */
        animatorSet.play(getTranslateAnimO()).after(getRotateAnim());
        return animatorSet;
    }

    /**
     * 建立集合動畫for carrot
     */
    private AnimatorSet getAnimSetC() {
        /* 建立集合動畫物件以加入多種動畫 */
        AnimatorSet animatorSet = new AnimatorSet();
        /* 播放完play()的動畫後再播放before()的動畫 */
        animatorSet.play(getTranslateAnimC()).before(getScaleAnim());//12
        /* 播放完after()的動畫後再播放play()的動畫 */
        animatorSet.play(getTranslateAnimCO()).after(getScaleAnim());//32

        return animatorSet;
    }

    /**
     * 建立透明度動畫並套用當前特效 for apple
     */
    private ObjectAnimator getAlphaAnim() {
        /* 設定ivSoccer物件從完全不透明(1)至完全透明(0) */
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofFloat(ivApple, View.ALPHA, 1, 0);
        objectAnimator.setDuration(1000);
        objectAnimator.setRepeatCount(1);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setInterpolator(interpolator);
        return objectAnimator;
    }



    /**
     * 建立顏色動畫
     */
//    private ObjectAnimator getColorAnim() {
//        ObjectAnimator objectAnimator;
//        /* 設定背景色從白色變成紅色再還原，API 21開始可呼叫ofArgb()設定 */
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            objectAnimator = ObjectAnimator.ofArgb(tvTitle, "backgroundColor",
//                    Color.WHITE, Color.RED);
//        } else {//old version way
//            objectAnimator =
//                    ObjectAnimator.ofObject(tvTitle, "backgroundColor",
//                            new ArgbEvaluator(), Color.WHITE, Color.RED);
//        }
//        objectAnimator.setDuration(1000);
//        objectAnimator.setRepeatCount(1);
//        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        objectAnimator.setInterpolator(interpolator);
//        return objectAnimator;
//    }

    /**
     * 建立搖晃動畫，將位移動畫重複且快速播放數次即可達到搖晃效果
     */
//    private ObjectAnimator getShakeAnim() {
//        ObjectAnimator objectAnimator =
//                ObjectAnimator.ofFloat(tvTitle, View.TRANSLATION_Y, 0, 10);//TRANSLATION_X是左右甩
//        objectAnimator.setDuration(1000);
//        /* 設定CycleInterpolator特效以重複播放7次 */
//        CycleInterpolator cycleInterpolator = new CycleInterpolator(7);//設定位移次數
//        objectAnimator.setInterpolator(cycleInterpolator);
//        return objectAnimator;
//    }
//===============================================================================================================================
    @Override
    protected void onStop() {  //畫面不在眼前，把監聽器拿掉，以免佔用資源
        super.onStop();
        if (animator != null) {
            animator.removeAllListeners();
        }
        if (animatorC != null) {
            animator.removeAllListeners();
        }

    }

    /* 依照連線狀況改變按鈕enable狀態 */
    private void changeConnectStatus(boolean isConnected) {
        if (isConnected) {//已連線
            btSend.setEnabled(true);
            btConnect.setEnabled(false);
            btDisconnect.setEnabled(true);
            showToast(R.string.text_Connect);
        } else {
            btSend.setEnabled(false);
            btConnect.setEnabled(true);
            btDisconnect.setEnabled(false);
            showToast(R.string.text_Disconnect);
        }

    }

//================change into member name===============
    public void clickSend(View view) {//android對話
        String message = etMessage.getText().toString();
        if (message.trim().isEmpty()) {
            showToast(R.string.text_MessageEmpty);
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "sendText");
        jsonObject.addProperty("userName", userNameMe);//change into ---
        jsonObject.addProperty("message", message);//聊天訊息
        myWebSocketClient.send(jsonObject.toString());
        Log.d(TAG, "output: " + jsonObject.toString());
        etMessage.setText("");

    }

    public void clickConnect(View view) {
        myWebSocketClient = new MyWebSocketClient(uri);
//==============================
        if (SERVER_URI.indexOf("wss") == 0)
        {
            try {
                SSLContext sslContext = SSLContext.getDefault();
                myWebSocketClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
//===============================
        myWebSocketClient.connect();
        Log.e("b", "b");
    }

    public void clickDisconnect(View view) {//斷線
        if (myWebSocketClient != null) {
            myWebSocketClient.close();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//返回鍵斷線
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (myWebSocketClient != null) {
                myWebSocketClient.close();
                showToast(R.string.text_LeftChatRoom);
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showToast(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }
}
