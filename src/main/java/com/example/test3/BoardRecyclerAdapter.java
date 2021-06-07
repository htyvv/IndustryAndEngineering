package com.example.test3;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class BoardRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    // adapter에 들어갈 list 입니다.
    private ArrayList<Data> listData = new ArrayList<>();
    private BoardFragment parentFragment;

    BoardRecyclerAdapter(BoardFragment fragment){
        parentFragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        /*
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_recyclerview_item, parent, false);
        return new ItemViewHolder(view);
         */

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_recyclerview_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        /*
        holder.onBind(listData.get(position));
        */

        if (holder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return listData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        /*
        return listData.size();
         */
        return listData == null ? 0 : listData.size();
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {

    }

    private void populateItemRows(ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    Data getItem(int position) {
        return listData.get(position);
    }

    void addItem(Data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
        notifyDataSetChanged();
    }

    void removeItem(int position) {
        if (listData.get(position) == null) Log.d("Removed: ", "null");
        else Log.d("Removed: ", listData.get(position).getTitle());
        listData.remove(position);
        notifyDataSetChanged();
    }

    void addPage(ArrayList<Data> newValue) {

        for (Data data : newValue) {
            listData.add(data);
        }

        notifyDataSetChanged();
    }


    public void setValue(ArrayList<Data> newValue){
        listData = newValue;
        notifyDataSetChanged();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView boardTitle;
        private TextView boardUserName;
        private TextView boardTime;
        private TextView boardLikeAmount;
        private TextView boardCommentAmount;
        private int boardId;

        ItemViewHolder(View itemView) {
            super(itemView);

            boardTitle = itemView.findViewById(R.id.boardTitle);
            boardUserName = itemView.findViewById(R.id.boardUserName);
            boardTime = itemView.findViewById(R.id.boardTime);
            boardLikeAmount = itemView.findViewById(R.id.boardLikeAmount);
            boardCommentAmount = itemView.findViewById(R.id.boardCommentAmount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: 누르면 그 글의 데이터를 받아와서 setFrag(5); -> Fragment생성때 받아갈 데이터를 여기서 전송해줘야 함
                    MainActivity.boardId = boardId;
                    ((MainActivity) parentFragment.getActivity()).setFrag(5);
                }
            });
        }

        void onBind(Data data) {
            boardTitle.setText(data.getTitle());
            boardUserName.setText(data.getName());

            String year = data.getDate().substring(2, 4);
            String month = data.getDate().substring(5, 7);
            String date = data.getDate().substring(8, 10);

            String hour = data.getDate().substring(11, 13);
            String minute = data.getDate().substring(14, 16);

            boardTime.setText(year + "/" + month + "/" + date + "  " + hour + ":" + minute);
            boardLikeAmount.setText(Integer.toString(data.getLikeAmount()));
            boardCommentAmount.setText(Integer.toString(data.getCommentAmount()));

            boardId = data.getId();
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}