package com.example.test3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class DogPeopleFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dogpeopleinformation, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.dogpeopleRecyclerview);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        DogPeopleRecyclerAdapter adapter = new DogPeopleRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new RecyclerViewDecoration(0, 30));

        List<String> listTitle = Arrays.asList("내가 쓴 글", "좋아요 누른 글", "게시글a", "게시글b", "게시글a", "게시글b", "게시글a", "게시글b",
                "게시글a", "게시글b", "게시글a", "게시글b", "게시글a", "게시글b", "게시글a", "게시글b");

        List<Integer> listResId = Arrays.asList(
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18,
                R.drawable.baseline_minimize_black_18
        );
        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            Data data = new Data();
            data.setTitle(listTitle.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
        return view;
    }
}