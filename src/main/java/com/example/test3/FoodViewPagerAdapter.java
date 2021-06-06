package com.example.test3;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test3.databinding.FoodViewpagerItemBinding;

import java.util.ArrayList;
import java.util.List;

public class FoodViewPagerAdapter extends RecyclerView.Adapter<FoodViewPagerAdapter.FoodViewPagerHolder> {

    private ArrayList<Data> sliderItems;
    private MainFragment parentFragment;


    public FoodViewPagerAdapter(MainFragment fragment) {
        parentFragment = fragment;

        ArrayList<Data> tempItems = new ArrayList<>();
        tempItems.add(new Data());
        setValue(tempItems);
    }


    public void setValue(ArrayList<Data> dataArrayList){
        if (dataArrayList != null) {
            sliderItems = dataArrayList;
        } else {
            sliderItems = new ArrayList<>();
            Log.d("FoodViewPagerAdapter", "[setValue] dataArrayList is null");
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public FoodViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodViewPagerHolder(FoodViewpagerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull FoodViewPagerHolder holder, int position) {

        Glide.with(parentFragment.getActivity()).load(sliderItems.get(position).getImage())
                .placeholder(R.drawable.placeholder)
                .error(R.mipmap.ic_main)
                .into(holder.mImage);

        holder.boardId = sliderItems.get(position).getId();

    }


    @Override
    public int getItemCount() {
        return sliderItems.size();
    }


    class FoodViewPagerHolder extends RecyclerView.ViewHolder {
        private FoodViewpagerItemBinding mBinding;
        public ImageView mImage;
        public int boardId;

        public FoodViewPagerHolder(@NonNull FoodViewpagerItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mImage = mBinding.foodViewPager;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("FoodViewPagerAdapter", "Clicked");
                    MainActivity.recoboardId = boardId;
                    MainActivity.previousPage = 0;
                    ((MainActivity) parentFragment.getActivity()).setFrag(11);
                }
            });
        }
    }
}