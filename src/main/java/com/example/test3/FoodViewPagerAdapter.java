package com.example.test3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test3.databinding.FoodViewpagerItemBinding;

import java.util.ArrayList;
import java.util.List;

public class FoodViewPagerAdapter extends RecyclerView.Adapter<FoodViewPagerAdapter.FoodViewPagerHolder> {

    private List<Integer> sliderItems;
    private MainFragment parentFragment;

    ArrayList<Integer> tempItems;

    public FoodViewPagerAdapter(MainFragment fragment) {
        parentFragment = fragment;

        ArrayList<Integer> tempItems = new ArrayList<>();
        tempItems.add(R.drawable.board);
        tempItems.add(R.drawable.home);
        tempItems.add(R.drawable.user);
        setValue(tempItems);
    }

    public void setValue(List<Integer> list){
        sliderItems = list;
        notifyDataSetChanged();
    }

    public void imageadd(int resid){
        tempItems = new ArrayList<>();
        tempItems.add(resid);
        tempItems.add(resid);
        tempItems.add(resid);
        setValue(tempItems);
    }

    @NonNull
    @Override
    public FoodViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodViewPagerHolder(FoodViewpagerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewPagerHolder holder, int position) {
        holder.mImage.setImageResource(sliderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }


    class FoodViewPagerHolder extends RecyclerView.ViewHolder {
        private FoodViewpagerItemBinding mBinding;
        public ImageView mImage;

        public FoodViewPagerHolder(@NonNull FoodViewpagerItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mImage = mBinding.foodViewPager;
        }
    }
}