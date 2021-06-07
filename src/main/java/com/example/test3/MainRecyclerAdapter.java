package com.example.test3;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test3.databinding.FragmentMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    private List<String> resetList;
    private List<String> historyList;
    static public List<String> mValue = new ArrayList<>();
    private MainFragment parentFragment;

    MainRecyclerAdapter(MainFragment fragment){
        parentFragment = fragment;
        resetList = new ArrayList<>();
        resetList.add("BLT샌드위치");resetList.add("간짜장");resetList.add("김밥");
        System.out.println("리사이클러어댑터에 add되는 순간!");
        setValue(resetList);
        //todo 히스토리 리스트 초기화
        historyList = resetList;
    }

    public boolean isSameList(List<String> list){
        int temp = 0;
        if(mValue.size()<3){return false;}
        for(int i = 0; i < 3; i++) {
            Log.d("MainRecyclerAdpter",mValue.get(i)+":"+list.get(i));
            if (list.get(i).equals(mValue.get(i))) {
                temp++;
            }
        }
        if (temp == 3) return true;
        temp=0;
        for(int i = 0; i < 3; i++) {
            Log.d("MainRecyclerAdpter",historyList.get(i)+":"+list.get(i));
            if (list.get(i).equals(historyList.get(i))) {
                temp++;
            }
        }
        if (temp == 3) return true;
        return false;
    }

    public void setValue(List<String> list){
        historyList=mValue;
        mValue = list;
        notifyDataSetChanged();
    }

    public void setValueReset(){
        Log.d("TempRecyclerViewAdapter","reset");
        historyList = mValue;
        mValue = resetList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temp_recyclerview_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mNumberView.setText(mValue.get(position));
        holder.mRankView.setText(Integer.toString(position + 1));
        //System.out.println(parentFragment.ggum(mValue.get(position)));
        holder.mImageView.setImageResource(parentFragment.ggum(mValue.get(position)));

    }


    @Override
    public int getItemCount() {
        return mValue.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNumberView;
        public TextView mRankView;
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mNumberView = (TextView) view.findViewById(R.id.recycler_item);
            mRankView = (TextView) view.findViewById(R.id.recycler_rank);
            mImageView = (ImageView) view.findViewById(R.id.recycler_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parentFragment.setRecent(mNumberView.getText().toString());
                    //parentFragment.PersonalizePOST(Integer.parseInt(mNumberView.getText().toString()));
                    //setValueReset();
                }
            });
        }
    }
}
