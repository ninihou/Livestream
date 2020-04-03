package com.example.livestream.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.livestream.R;
//import static com.example.livestream.main.Util.PAGES;

public class ChangeUrlActivity extends AppCompatActivity {

    private EditText etUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_url);
        findViews();
    }

    private void findViews() {
        etUrl = findViewById(R.id.etUrl);
        etUrl.setText(Util.URL);
    }

    public void onUrlClick(View view) {
        Util.URL = etUrl.getText().toString().trim();
        Toast.makeText(this, "new URL = " + Util.URL, Toast.LENGTH_LONG).show();
    }


     //依據PAGES產生對應MenuItem
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        for (Page page : PAGES) {
//            int id = page.getId();
//            String title = page.getTitle();
//            menu.add(0, id, id, title);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        for (Page page : PAGES) {
//            if (item.getItemId() == page.getId()) {
//                Intent intent = new Intent(this, page.getFirstActivity());
//                startActivity(intent);
//                return true;
//            }
//        }
//        return false;
//    }
}
