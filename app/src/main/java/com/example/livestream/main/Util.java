package com.example.livestream.main;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.ArrayList;



public class Util {
    // 模擬器連Tomcat
    public static String URL = "http://10.0.2.2:8081/DA106_G4/";
//    public static String URL = "http://172.20.10.6:8081/BookStoreWeb/";

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


    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
