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

import com.example.test3.databinding.FragmentRecoReadBinding;

import java.util.ArrayList;


public class RecoReadFragment extends Fragment { // 11

    private View view;
    private FragmentRecoReadBinding binding;

    ImageButton recoCommentButton;
    EditText recoCommentInputEditText;
    ImageButton recoReadBackButton;

    ConstraintLayout recoReadModifyLayout;
    ImageButton recoReadLikeButton;
    ImageButton recoReadModifyButton;
    ImageButton recoReadDeleteButton;
    Boolean recoCurrentLike = false;
    int recoCurrentLikeAmount;
    String recoCurrentLikeId;

    TextView recoReadWriter;
    TextView recoReadWriteTime;
    TextView recoReadLikeAmount;
    TextView recoReadCommentAmount;
    TextView recoReadTitleText;
    TextView recoReadTag;
    HTMLTextView recoReadMainText;

    RecoCommentRecyclerAdapter adapter;
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

        binding = com.example.test3.databinding.FragmentRecoReadBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        RecoReadFragment fragment = this;
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();



        // Amplify????????? ????????? ?????????
        AmplifyApi.RecommendBoardGet(this, getActivity(), MainActivity.recoboardId);



        //commentRecyclerView
        RecyclerView recoReadCommentRecyclerView = binding.recoReadCommentRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) getActivity());
        recoReadCommentRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecoCommentRecyclerAdapter(this);
        recoReadCommentRecyclerView.setAdapter(adapter);

        recoReadCommentRecyclerView.addItemDecoration(new RecyclerViewDecoration(0, 30));



        // recoReadWrite
        recoReadWriter = binding.recoReadWriter;
        recoReadWriter.setText("");


        // recoReadWriteTime
        recoReadWriteTime = binding.recoReadWriteTime;
        recoReadWriteTime.setText("");


        // recoReadLikeAmount
        recoReadLikeAmount = binding.recoReadLikeAmount;
        recoReadLikeAmount.setText("");


        // recoReadCommentAmount
        recoReadCommentAmount = binding.recoReadCommentAmount;
        recoReadCommentAmount.setText("");


        // recoReadTitleText
        recoReadTitleText = binding.recoReadTitleText;
        recoReadTitleText.setText("");


        // readRecoTag
        recoReadTag = binding.recoReadTag;
        recoReadTag.setText("");


        // recoReadMainText
        recoReadMainText = binding.recoReadMainText;
        recoReadMainText.setHtmlText("");


        // recoReadModifyLayout
        recoReadModifyLayout = binding.recoReadModifyLayout;


        // recoReadLikeButton
        recoReadLikeButton = binding.recoReadLikeButton;
        recoReadLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: ?????? ????????? ???????????? ??? ?????? POST, ???????????? ????????? ?????? ?????? ????????? 1 ???????????? setText
                if (recoCurrentLike) { // ?????? ????????? ??????
                    recoReadLikeButton.setImageResource(R.drawable.baseline_thumb_up_off_alt_black_36);
                    recoCurrentLikeAmount--;
                    recoReadLikeAmount.setText(Integer.toString(recoCurrentLikeAmount));
                    recoCurrentLike = false;

                    AmplifyApi.RecommendBoardLikeDelete(Integer.toString(MainActivity.recoboardId), recoCurrentLikeId);

                    while (!MainActivity.modifyComplete) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    MainActivity.modifyComplete = false;

                } else { // ?????? ????????? ?????? X
                    recoReadLikeButton.setImageResource(R.drawable.baseline_thumb_up_alt_black_36);
                    recoCurrentLikeAmount++;
                    recoReadLikeAmount.setText(Integer.toString(recoCurrentLikeAmount));
                    recoCurrentLike = true;

                    AmplifyApi.RecommendBoardLikePost(MainActivity.recoboardId, MainActivity.userId);

                    while (!MainActivity.modifyComplete) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    MainActivity.modifyComplete = false;

                }
            }
        });

        // recoReadModifyButton
        recoReadModifyButton = binding.recoReadModifyButton;
        recoReadModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setFrag(10);
            }
        });



        // recoReadDeleteButton
        recoReadDeleteButton = binding.recoReadDeleteButton;
        recoReadDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder oDialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);

                oDialog.setMessage("?????? ?????????????????????????")
                        .setTitle("?????? Dialog")
                        .setPositiveButton("?????????", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.i("Dialog", "??????");
                                Toast.makeText(getContext(), "??? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNeutralButton("???", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.d("ReadFragment", "recoboardId = " + Integer.toString(MainActivity.recoboardId) + ", userId = " + MainActivity.userId);
                                AmplifyApi.RecommendBoardDelete(Integer.toString(MainActivity.recoboardId), MainActivity.userId);
                                Toast.makeText(getContext(), "?????? ?????????????????????.", Toast.LENGTH_LONG).show();

                                while (!MainActivity.modifyComplete) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                MainActivity.modifyComplete = false;

                                ((MainActivity) getActivity()).setFrag(3);
                            }
                        })
                        .setCancelable(false) // ??????????????? ???????????? ????????? ????????? ??????.


                        .show();
            }
        });



        // recoCommentInputEditText
        recoCommentInputEditText = binding.recoCommentInputEditText;

        // recoCommentButton
        recoCommentButton = binding.recoCommentButton;
        recoCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO userAttr?????? name??? sub??? ???????????? name, password??? ?????? ???
                AmplifyApi.RecommendBoardCommentPost(MainActivity.recoboardId, MainActivity.userId, recoCommentInputEditText.getText().toString(), MainActivity.userName);
                recoCommentInputEditText.setText(null);

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


        // recoReadBackButton
        recoReadBackButton = binding.recoReadBackButton;
        recoReadBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setFrag(MainActivity.previousPage);
            }
        });

        return view;
    }

    public void setValue(Data newData){

        data = newData;

        Log.d("RecoReadFragment:sV", "\n" + data.getDate() + "\n" + data.getName() + "\n" + data.getTitle() + "\n" + data.getContent() + "\n" + data.getTag());

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

        recoReadWriter.setText("?????????: " + data.getName());
        recoReadWriteTime.setText("?????? ??????: " + year + "??? " + month + "??? " + date + "???  " + hour + "??? " + minute + "??? " + second + "???");

        recoCurrentLikeAmount = data.getLikeAmount();
        recoReadLikeAmount.setText(Integer.toString(recoCurrentLikeAmount));
        // ???????????? ??? ?????? ???????????? ?????? ??????(= data.getLike()??? MainActivity.userId??? ?????????)??? recoCurrentLike=True, ???????????? ?????????
        for (DataLike dataL : data.getLike()) {
            if (dataL.getName().equals(MainActivity.userId)) {
                recoCurrentLike = true;
                recoReadLikeButton.setImageResource(R.drawable.baseline_thumb_up_alt_black_36);
                recoCurrentLikeId = dataL.getId();
            } else {
                recoCurrentLike = false;
            }
        }

        recoReadCommentAmount.setText(Integer.toString(data.getCommentAmount()));
        recoReadTitleText.setText(data.getTitle());
        recoReadMainText.setHtmlText(data.getContent());
        recoReadTag.setText(data.getTag());

        adapter.setValue(data.getComment());
        adapter.notifyDataSetChanged();


        // ??? ????????? ?????? ??????
        MainActivity.modifyTitle = data.getTitle();
        MainActivity.modifyContent = data.getContent();
        MainActivity.modifyTag = data.getTag();


        // ???????????? ??????, ?????? ???????????? ??????
        if (data.getPassword().equals(MainActivity.userId)) {
            recoReadModifyButton.setVisibility(View.VISIBLE);
            recoReadDeleteButton.setVisibility(View.VISIBLE);
        } else {
            recoReadModifyButton.setVisibility(View.GONE);
            recoReadDeleteButton.setVisibility(View.GONE);
        }

    }

}
