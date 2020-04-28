package com.example.livestream.fragment_member;

import android.content.SharedPreferences;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
//import android.support.v7.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.livestream.Data;
import com.example.livestream.Item;
import com.example.livestream.R;
import com.example.livestream.main.Util;
import com.example.livestream.task.CommonTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThreeFragment extends Fragment {
    private View view;
    private CommonTask lsCollectionTask;
    private static final String TAG = "ThreeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_three, container, false);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();


        //Data
        List<Item> itemList = Data.getItemList();

        //RecyclerCardView
        RecyclerView recyclerCardView = (RecyclerView) view.findViewById(R.id.recyclerCardView_f);
        recyclerCardView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        recyclerCardView.setAdapter(new RecyclerCardViewItemAdapter(getContext(),itemList));
    }

    private void showLs(){
        if (Util.networkConnected(getActivity())){
            SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            String member_id = preferences.getString("member_id", "");
            String url = Util.URL + "CollectionServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findlsBymemberid");
            jsonObject.addProperty("member_id", member_id);
            String jsonOut = jsonObject.toString();
            lsCollectionTask = new CommonTask(url, jsonOut);
            try {
                String lsId = lsCollectionTask.execute().get();
                JsonObject jsonObject1 = new JsonParser().parse(lsId).getAsJsonObject();
                //jsonObject1 is LsID, need id to get ls pic and title to show

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Util.showToast(getContext(), R.string.msg_NoNetwork);
        }
    }


    //CardView
    private class RecyclerCardViewItemAdapter extends RecyclerView.Adapter<RecyclerCardViewItemAdapter.RecyclerCardViewHolder>{

        private Context context;
        private List<Item> itemList;

        public RecyclerCardViewItemAdapter(Context context, List<Item> itemList) {
            this.context = context;
            this.itemList = itemList;
        }

        public class RecyclerCardViewHolder extends RecyclerView.ViewHolder{

            private final ImageView ivLsPicture;
            private final TextView tvTitle;
           // private final TextView textView5;

            public RecyclerCardViewHolder(View itemView) {
                super(itemView);
                ivLsPicture = (ImageView) itemView.findViewById(R.id.ivLsPicture);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
               // textView5 = (TextView) itemView.findViewById(R.id.textView5);
            }
        }

        @Override
        public RecyclerCardViewItemAdapter.RecyclerCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.cardview_livestream,parent,false);
            return new RecyclerCardViewItemAdapter.RecyclerCardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerCardViewItemAdapter.RecyclerCardViewHolder holder, int position) {
            final Item item = itemList.get(position);
            holder.ivLsPicture.setImageResource(item.getImage());
            holder.tvTitle.setText(item.getTitle());
          //  holder.textView5.setText(item.getSubtitle());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(item.getImage());
                    Toast toast = new Toast(context);
                    toast.setView(imageView);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

}
