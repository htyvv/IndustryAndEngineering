package com.example.test3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecoCommentRecyclerAdapter extends RecyclerView.Adapter<RecoCommentRecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<DataComment> listData = new ArrayList<>();
    private RecoReadFragment parentFragment;

    private DataComment currentData;

    RecoCommentRecyclerAdapter(RecoReadFragment fragment){
        parentFragment = fragment;
    }

    @NonNull
    @Override
    public RecoCommentRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_recyclerview_item, parent, false);
        return new RecoCommentRecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecoCommentRecyclerAdapter.ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        currentData = listData.get(position);
        holder.onBind(currentData);
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(DataComment data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public void setValue(ArrayList<DataComment> newValue){
        listData = newValue;
        notifyDataSetChanged();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView commentUser;
        private TextView commentContent;
        private TextView commentDate;
        private ImageButton commentDeleteButton;

        ItemViewHolder(View itemView) {

            super(itemView);

            commentUser = itemView.findViewById(R.id.commentUser);
            commentContent = itemView.findViewById(R.id.commentContent);
            commentDate = itemView.findViewById(R.id.commentDate);

            commentDeleteButton = itemView.findViewById(R.id.commentDeleteButton);
            commentDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder oDialog = new AlertDialog.Builder(parentFragment.getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);

                    oDialog.setMessage("댓글을 삭제하시겠습니까?")
                            .setTitle("일반 Dialog")
                            .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Log.i("Dialog", "취소");
                                    Toast.makeText(parentFragment.getContext(), "댓글 삭제가 취소되었습니다.", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNeutralButton("예", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Log.d("CommentRecyclerAdapter", "recoboardId = " + Integer.toString(MainActivity.recoboardId) + ", commentId = " + currentData.getId() + ", password = " + MainActivity.userId);
                                    AmplifyApi.RecommendBoardCommentDelete(Integer.toString(MainActivity.recoboardId), currentData.getId(), MainActivity.userId);
                                    Toast.makeText(parentFragment.getContext(), "댓글이 삭제되었습니다.", Toast.LENGTH_LONG).show();

                                    while (!MainActivity.modifyComplete) {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    MainActivity.modifyComplete = false;

                                    FragmentTransaction ft = parentFragment.getParentFragmentManager().beginTransaction();
                                    ft.detach(parentFragment).attach(parentFragment).commit();
                                }
                            })
                            .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.


                            .show();

                }
            });

        }

        void onBind(DataComment data) {
            commentUser.setText(data.getName());
            commentContent.setText(data.getContent());
            commentDate.setText(data.getDate());

            if(data.getDate() != null) {
                String year = data.getDate().substring(2, 4);
                String month = data.getDate().substring(5, 7);
                String date = data.getDate().substring(8, 10);

                String hour = data.getDate().substring(11, 13);
                String minute = data.getDate().substring(14, 16);
                commentDate.setText(year + "/" + month + "/" + date + "  " + hour + ":" + minute);
            }

            if (data.getPassword().equals(MainActivity.userId)) {
                commentDeleteButton.setVisibility(View.VISIBLE);
            } else {
                commentDeleteButton.setVisibility(View.GONE);
            }
        }
    }

}
