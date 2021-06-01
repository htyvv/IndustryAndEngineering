package com.example.test3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test3.databinding.FragmentReadBinding;

public class ReadFragment extends Fragment { // 5
    
    private View view;
    private FragmentReadBinding binding;

    ImageButton commentButton;
    EditText commentInputEditText;
    ImageButton readBackButton;
    
    ConstraintLayout readModifyLayout;
    ImageButton readModifyButton;
    ImageButton readDeleteButton;

    TextView readWriter;
    TextView readWriteTime;
    TextView readLikeAmount;
    TextView readCommentAmount;
    TextView readTitleText;
    HTMLTextView readMainText;

    CommentRecyclerAdapter adapter;
    Data data = new Data();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentReadBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        ReadFragment fragment = this;
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();



        // Amplify로부터 게시글 받아옴
        AmplifyApi.Get(this, getActivity(), MainActivity.boardId);



        //commentRecyclerView
        RecyclerView readCommentRecyclerView = binding.readCommentRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) getActivity());
        readCommentRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CommentRecyclerAdapter(this);
        readCommentRecyclerView.setAdapter(adapter);

        readCommentRecyclerView.addItemDecoration(new RecyclerViewDecoration(0, 30));



        // readWrite
        readWriter = binding.readWriter;
        readWriter.setText("");


        // readWriteTime
        readWriteTime = binding.readWriteTime;
        readWriteTime.setText("");


        // readLikeAmount
        readLikeAmount = binding.readLikeAmount;
        readLikeAmount.setText("");
        

        // readCommentAmount
        readCommentAmount = binding.readCommentAmount;
        readCommentAmount.setText("");


        // readTitleText
        readTitleText = binding.readTitleText;
        readTitleText.setText("");


        // readMainText
        readMainText = binding.readMainText;
        readMainText.setHtmlText("");


        // readModifyLayout
        readModifyLayout = binding.readModifyLayout;

        // readModifyButton
        readModifyButton = binding.readModifyButton;
        readModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setFrag(6);
            }
        });



        // readDeleteButton
        readDeleteButton = binding.readDeleteButton;
        readDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oDialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);

                oDialog.setMessage("글을 삭제하시겠습니까?")
                        .setTitle("일반 Dialog")
                        .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.i("Dialog", "취소");
                                Toast.makeText(getContext(), "글 삭제가 취소되었습니다.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNeutralButton("예", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.d("ReadFragment", "boardId = " + Integer.toString(MainActivity.boardId) + ", userId = " + MainActivity.userId);
                                AmplifyApi.Delete(Integer.toString(MainActivity.boardId), MainActivity.userId);
                                Toast.makeText(getContext(), "글이 삭제되었습니다.", Toast.LENGTH_LONG).show();

                                while (!MainActivity.modifyComplete) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                MainActivity.modifyComplete = false;

                                ((MainActivity) getActivity()).setFrag(1);
                            }
                        })
                        .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.


                        .show();
            }
        });



        // commentInputEditText
        commentInputEditText = binding.commentInputEditText;

        // commentButton
        commentButton = binding.commentButton;
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO userAttr에서 name과 sub를 받아와서 name, password로 줘야 함
                AmplifyApi.CommentPost(MainActivity.boardId, MainActivity.userId, commentInputEditText.getText().toString(), MainActivity.userName);
                commentInputEditText.setText(null);

                while (!MainActivity.modifyComplete) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.modifyComplete = false;

                ft.detach(fragment).attach(fragment).commit();
            }
        });


        // readBackButton
        readBackButton = binding.readBackButton;
        readBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setFrag(1);
            }
        });

        return view;
    }

    public void setValue(Data newData){
        
        data = newData;

        String year = data.getDate().substring(0, 4);
        String month = data.getDate().substring(5, 7);
        if (month.charAt(0) == '0') month = Character.toString(month.charAt(1));
        String date = data.getDate().substring(8, 10);
        if (date.charAt(0) == '0') date = Character.toString(date.charAt(1));

        String hour = data.getDate().substring(11, 13);
        if (hour.charAt(0) == '0') hour = Character.toString(hour.charAt(1));
        String minute = data.getDate().substring(14, 16);
        if (minute.charAt(0) == '0') minute = Character.toString(minute.charAt(1));
        String second = data.getDate().substring(17, 19);
        if (second.charAt(0) == '0') second = Character.toString(second.charAt(1));

        readWriter.setText("작성자: " + data.getName());
        readWriteTime.setText("작성 일자: " + year + "년 " + month + "월 " + date + "일  " + hour + "시 " + minute + "분 " + second + "초");
        readLikeAmount.setText("50");
        readCommentAmount.setText(Integer.toString(data.getCommentAmount()));
        readTitleText.setText(data.getTitle());
        readMainText.setHtmlText(data.getContent());

        adapter.setValue(data.getComment());
        adapter.notifyDataSetChanged();

        
        // 글 수정을 위해 저장
        MainActivity.modifyTitle = data.getTitle();
        MainActivity.modifyContent = data.getContent();

        
        // 본인이면 수정, 삭제 레이아웃 보임
        if (data.getPassword().equals(MainActivity.userId)) {
            readModifyLayout.setVisibility(View.VISIBLE);
        } else {
            readModifyLayout.setVisibility(View.GONE);
        }
        
    }

}