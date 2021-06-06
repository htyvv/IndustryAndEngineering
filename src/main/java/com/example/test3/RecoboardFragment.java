package com.example.test3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;

public class RecoboardFragment extends Fragment {

    private View view;

    private int currentPage = 1;
    private boolean isLoading = false;
    private int searching = 1;
    private String recoSearchText = "";

    ImageButton recoboardLeft;
    ImageButton recoboardRight;
    ImageButton recoboardWrite;
    ImageButton recoboardUser;
    ImageButton recoboardFind;
    EditText recoSearchInputEditText;
    Spinner recoSearchSpinner;
    ImageButton recoSearchButton;

    RecyclerView recoRecyclerView;
    RecoboardRecyclerAdapter recoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_recoboard, container, false);

        // recoRecyclerView
        recoRecyclerView = view.findViewById(R.id.recoboardRecyclerView);
        recoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        recoAdapter = new RecoboardRecyclerAdapter(this);
        recoRecyclerView.setAdapter(recoAdapter);

        recoRecyclerView.addItemDecoration(new RecyclerViewDecoration(20, 20));

        // Amplify로부터 게시글을 받아옴
        AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, null, null, false);
        currentPage = 1;


        recoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == recoAdapter.getItemCount() - 1) {
                        //리스트 마지막
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });



        // recoboardLeft
        recoboardLeft = view.findViewById(R.id.recoboardLeft);
        recoboardLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity) getActivity()).setFrag(1);
            }
        });

        // recoboardRight
        recoboardRight = view.findViewById(R.id.recoboardRight);
        recoboardRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity) getActivity()).setFrag(1);
            }
        });

        // recoboardWrite
        recoboardWrite = view.findViewById(R.id.recoboardWrite);
        recoboardWrite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setFrag(9);
            }
        });

        // recoboardUser
        recoboardUser = view.findViewById(R.id.recoboardUser);
        recoboardUser.setOnClickListener(new View.OnClickListener(){
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

        // recoSearchInputEditText
        recoSearchInputEditText = view.findViewById(R.id.recoSearchInputEditText);
        recoSearchInputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        recoSearchText = recoSearchInputEditText.getText().toString().replace("\n", "");

                        if (searching == 1) {
                            if (recoSearchText.equals("")) { // 제목 검색
                                AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, null, null, false);
                                currentPage = 1;
                            } else {
                                AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, recoSearchText, null, false);
                                currentPage = 1;
                            }
                        } else if (searching == 2) { // 닉네임 검색
                            if (recoSearchText.equals("")) {
                                AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, null, null, false);
                                currentPage = 1;
                            } else {
                                AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, recoSearchText, null, null, false);
                                currentPage = 1;
                            }
                        } else if (searching == 3) { // 태그 검색
                            if (recoSearchText.equals("")) {
                                AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, null, null, false);
                                currentPage = 1;
                            } else {
                                AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, null, recoSearchText, false);
                                currentPage = 1;
                            }
                        }

                        recoSearchInputEditText.setText(null);
                        break;
                }

                return true;
            }
        });

        // recoboardFind
        recoboardFind = view.findViewById(R.id.recoboardFind);
        recoboardFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LinearLayout recoSearchLayout = view.findViewById(R.id.recoSearchLayout);

                if (recoSearchLayout.getVisibility() == View.VISIBLE) {
                    recoSearchLayout.setVisibility(View.GONE);
                    //searchInputEditText.setText(null);
                } else {
                    recoSearchLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // recoSearchSpinner
        recoSearchSpinner = view.findViewById(R.id.recoSearchSpinner);
        recoSearchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("recoSearchSpinner: ", (String) parent.getItemAtPosition(position));
                if (((String) parent.getItemAtPosition(position)).equals("제목")) {
                    searching = 1;
                } else if (((String) parent.getItemAtPosition(position)).equals("작성자")) {
                    searching = 2;
                } else if (((String) parent.getItemAtPosition(position)).equals("태그")) {
                    searching = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // recoSearchButton
        recoSearchButton = view.findViewById(R.id.recoSearchButton);
        recoSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoSearchText = recoSearchInputEditText.getText().toString();

                if (searching == 1) {
                    if (recoSearchText.equals("")) { // 제목 검색
                        AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, null, null, false);
                        currentPage = 1;
                    } else {
                        AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, recoSearchText, null, false);
                        currentPage = 1;
                    }
                } else if (searching == 2) { // 닉네임 검색
                    if (recoSearchText.equals("")) {
                        AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, null, null, false);
                        currentPage = 1;
                    } else {
                        AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, recoSearchText, null, null, false);
                        currentPage = 1;
                    }
                } else if (searching == 3) { // 태그 검색
                    if (recoSearchText.equals("")) {
                        AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, null, null, false);
                        currentPage = 1;
                    } else {
                        AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), 0, null, null, recoSearchText, false);
                        currentPage = 1;
                    }
                }
            }
        });

        return view;
    }

    private void loadMore() {

        recoRecyclerView.post(new Runnable() {
            public void run() {
                recoAdapter.addItem(null);
                recoAdapter.notifyItemInserted(recoAdapter.getItemCount() - 1);

                recoAdapter.removeItem(recoAdapter.getItemCount() - 1);
                int scrollPosition = recoAdapter.getItemCount();
                recoAdapter.notifyItemRemoved(scrollPosition);


                Log.d("currentPage = ", String.valueOf(currentPage));
                Log.d("reco_maxPage = ", String.valueOf(MainActivity.reco_maxPage));
                // 다음 페이지를 불러옴
                if (currentPage <= MainActivity.reco_maxPage) {

                    if (searching == 1) {
                        if (recoSearchText.equals("")) {
                            AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), currentPage, null, null, null, true);
                        } else {
                            AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), currentPage, null, recoSearchText, null, true);
                        }
                    } else if (searching == 2) {
                        if (recoSearchText.equals("")) {
                            AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), currentPage, null, null, null, true);
                        } else {
                            AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), currentPage, recoSearchText, null, null, true);
                        }
                    } else if (searching == 3) {
                        if (recoSearchText.equals("")) {
                            AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), currentPage, null, null, null, true);
                        } else {
                            AmplifyApi.RecommendBoardGet(recoAdapter, getActivity(), currentPage, null, null, recoSearchText, true);
                        }
                    }

                    currentPage++;
                }

                recoAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        });

    }

}