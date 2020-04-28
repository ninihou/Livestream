package com.example.livestream.main;


import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.livestream.ws.ChatWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class Util {
    // 模擬器連Tomcat
    public static String URL = "http://10.0.2.2:8081/DA106_G4/";
//    public static String URL = "http://172.20.10.6:8081/DA106_G4/";//My phone
//    public static String URL = "http://172.20.10.2:8081/DA106_G4/";//Tibame

    public static ChatWebSocketClient chatWebSocketClient;

    // 偏好設定檔案名稱
    public final static String PREF_FILE = "preference";

    // 功能分類
//    public final static Page[] PAGES = {
//            new Page(0, "Book", R.drawable.books, BookActivity.class),
//            new Page(1, "Order", R.drawable.cart_empty, CartActivity.class),
//            new Page(2, "Member", R.drawable.user, MemberShipActivity.class),
//            new Page(3, "Setting", R.drawable.setting, ChangeUrlActivity.class)
//    };

    // 要讓商品在購物車內順序能夠一定，且使用RecyclerView顯示時需要一定順序，List較佳
//    public static ArrayList<OrderBook> CART = new ArrayList<>();//static＋ArrayList：跨頁面的資料保存

    // check if the device connect to the network
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int stringId) {
        Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
    }

    // 建立WebSocket連線
//    public static void connectServer(Context context, String userName) {
//        URI uri = null;
//        try {
//            uri = new URI(SERVER_URI + userName);
//        } catch (URISyntaxException e) {
//            Log.e(TAG, e.toString());
//        }
//        if (chatWebSocketClient == null) {
//            chatWebSocketClient = new ChatWebSocketClient(uri, context);
//            chatWebSocketClient.connect();
//        }
//    }

    // 中斷WebSocket連線
//    public static void disconnectServer() {
//        if (chatWebSocketClient != null) {
//            chatWebSocketClient.close();
//            chatWebSocketClient = null;
//        }
//    }

    //縮圖方法
    /*
     * options.inJustDecodeBounds取得原始圖片寬度與高度資訊 (但不會在記憶體裡建立實體)
     * 當輸出寬與高超過自訂邊長邊寬最大值，scale設為2 (寬變1/2，高變1/2)
     */
    public static int getImageScale(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int scale = 1;
        while (options.outWidth / scale >= width ||
                options.outHeight / scale >= height) {
            scale *= 2;
        }
        return scale;
    }
}
