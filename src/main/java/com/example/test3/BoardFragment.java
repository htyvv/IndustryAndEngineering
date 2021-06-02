package com.example.test3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardFragment extends Fragment {

    private View view;

    private int currentPage = 1;
    private boolean isLoading = false;
    private int searching = 1;
    private String searchText = "";

    ImageButton boardLeft;
    ImageButton boardRight;
    ImageButton boardWrite;
    ImageButton boardUser;
    ImageButton boardFind;
    EditText searchInputEditText;
    Spinner searchSpinner;
    ImageButton searchButton;

    RecyclerView recyclerView;
    BoardRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_board, container, false);

        // recyclerView
        recyclerView = view.findViewById(R.id.boardRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new BoardRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new RecyclerViewDecoration(0, 20));


        // Amplify로부터 게시글을 받아옴
        AmplifyApi.Get(adapter, getActivity(), 0, null, null, false);
        currentPage = 1;


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                        //리스트 마지막
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


        // recoboardLeft
        boardLeft = view.findViewById(R.id.boardLeft);
        boardLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity) getActivity()).setFrag(3);
            }
        });

        // boardRight
        boardRight = view.findViewById(R.id.boardRight);
        boardRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity) getActivity()).setFrag(3);
            }
        });

        // boardWrite
        boardWrite = view.findViewById(R.id.boardWrite);
        boardWrite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setFrag(2);
            }
        });

        // boardUser
        boardUser = view.findViewById(R.id.boardUser);
        boardUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("설정"); //제목
                final String[] versionArray = new String[] {"닉네임 변경", "로그아웃"};

                dlg.setItems(versionArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (versionArray[which].equals("닉네임 변경")) {

                            final LinearLayout linear = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_nickname, null);
                            TextView nickTextView = (TextView) linear.findViewById(R.id.nickTextView);
                            nickTextView.setText("현재 닉네임: " + MainActivity.userName);

                            new AlertDialog.Builder(getActivity())
                                    .setView(linear)
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            EditText nickEditText = (EditText) linear.findViewById(R.id.nickEditText);
                                            String value = nickEditText.getText().toString();
                                            AmplifyApi.setUserNick(value);

                                            while (!MainActivity.modifyComplete) {
                                                try {
                                                    Thread.sleep(500);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            MainActivity.modifyComplete = false;

                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();


                        } else if (versionArray[which].equals("로그아웃")) {

                            Amplify.Auth.signOut(
                                    AuthSignOutOptions.builder().globalSignOut(true).build(),
                                    () -> {
                                        Log.i("AuthQuickstart", "Signed out globally");

                                        Handler mHandler = new Handler(Looper.getMainLooper());
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
                                            }
                                        }, 0);

                                        ((MainActivity) getActivity()).setFrag(7);
                                    },
                                    error -> Log.e("AuthQuickstart", error.toString())
                            );

                        }
                    }
                });

                dlg.show();
            }
        });

        // searchInputEditText
        searchInputEditText = view.findViewById(R.id.searchInputEditText);

        // boardFind
        boardFind = view.findViewById(R.id.boardFind);
        boardFind.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               LinearLayout searchLayout = view.findViewById(R.id.searchLayout);

               if (searchLayout.getVisibility() == View.VISIBLE) {
                   searchLayout.setVisibility(View.GONE);
                   //searchInputEditText.setText(null);
               } else {
                   searchLayout.setVisibility(View.VISIBLE);
               }
           }
        });

        // searchSpinner
        searchSpinner = view.findViewById(R.id.searchSpinner);
        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("searchSpinner: ", (String) parent.getItemAtPosition(position));
                if (((String) parent.getItemAtPosition(position)).equals("제목")) {
                    searching = 1;
                } else if (((String) parent.getItemAtPosition(position)).equals("작성자")) {
                    searching = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // searchButton
        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = searchInputEditText.getText().toString();

                if (searching == 1) {
                    if (searchText.equals("")) { // 제목 검색
                        AmplifyApi.Get(adapter, getActivity(), 0, null, null, false);
                        currentPage = 1;
                    } else {
                        AmplifyApi.Get(adapter, getActivity(), 0, null, searchText, false);
                        currentPage = 1;
                    }
                } else if (searching == 2) { // 닉네임 검색
                    if (searchText.equals("")) {
                        AmplifyApi.Get(adapter, getActivity(), 0, null, null, false);
                        currentPage = 1;
                    } else {
                        AmplifyApi.Get(adapter, getActivity(), 0, searchText, null, false);
                        currentPage = 1;
                    }
                }
            }
        });

        return view;
    }

    private void loadMore() {

        recyclerView.post(new Runnable() {
            public void run() {
                adapter.addItem(null);
                adapter.notifyItemInserted(adapter.getItemCount() - 1);

                adapter.removeItem(adapter.getItemCount() - 1);
                int scrollPosition = adapter.getItemCount();
                adapter.notifyItemRemoved(scrollPosition);


                Log.d("currentPage = ", String.valueOf(currentPage));
                Log.d("maxPage = ", String.valueOf(MainActivity.maxPage));
                // 다음 페이지를 불러옴
                if (currentPage <= MainActivity.maxPage) {

                    if (searching == 1) {
                        if (searchText.equals("")) {
                            AmplifyApi.Get(adapter, getActivity(), currentPage, null, null, true);
                        } else {
                            AmplifyApi.Get(adapter, getActivity(), currentPage, null, searchText, true);
                        }
                    } else if (searching == 2) {
                        if (searchText.equals("")) {
                            AmplifyApi.Get(adapter, getActivity(), currentPage, null, null, true);
                        } else {
                            AmplifyApi.Get(adapter, getActivity(), currentPage, searchText, null, true);
                        }
                    }

                    //AmplifyApi.Get(adapter, getActivity(), currentPage, null, null, true);
                    currentPage++;
                }

                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        });

    }

}