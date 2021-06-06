package com.example.test3;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;
import com.example.test3.databinding.FragmentMainBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends Fragment {

    private View view;
    private FragmentMainBinding binding;


    DrawerLayout drawerLayout;

    RecyclerView recyclerView;
    MainRecyclerAdapter adapter;

    ImageButton setting;
    ImageButton filter;
    TextView mainTitle;
    Button wanteatbutton;
    ImageButton info;
    ImageButton closeButton;

    CheckBox check2, check3, check4, check5, check6, check7, check8, check9, check10, check11, check12;


    ViewPager2 foodViewPager;
    FoodViewPagerAdapter foodViewPagerAdapter;
    int currentPage = -1;
    String currentItem;
    Timer timer;
    final long DELAY_MS = 500;
    final long PERIOD_MS = 3000;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainFragment", "onStart");
        AmplifyApi.PersonalizeGet(adapter, getActivity(), MainActivity.userId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        Log.d("MainFragment", "onCreateView");

        // recyclerView
        recyclerView = binding.recycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new MainRecyclerAdapter(this);
        ArrayList<Integer> checkfirst = new ArrayList<>();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerViewDecoration(0, 60));
        ObjectAnimator.ofFloat(recyclerView, "alpha", 0.0f, 1f).start();

        //((MainActivity)getActivity()).Excel(checkfirst, adapter);

        //setRecent(MainActivity.sharedPref.getInt("current",1));
        //PersonalizePOST(-99);
        //recyclerView.addItemDecoration(new TempRecyclerDecoration(25,25));
            /*
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setAdapter(new BookmarkRecyclerViewAdapter());
             */



        // drawLayout
        drawerLayout = binding.drawerLayout;



        // mainTitle
        mainTitle = binding.mainTitle;
        // TODO: 아마 userName 받아오기 전에 길이를 계산해버려서 0, 0으로 처리되는듯?
        SpannableStringBuilder sp = new SpannableStringBuilder(MainActivity.userName + "님을 위한  \n오늘의 추천 음식!  ");
        sp.setSpan(new StyleSpan(Typeface.BOLD), 0, MainActivity.userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mainTitle.setText(sp);



        // setting
        setting = binding.setting;
        setting.setOnClickListener(new View.OnClickListener(){

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
                                            mainTitle.setText(MainActivity.userName + "님을 위한  \n오늘의 추천 음식!  ");

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



        // filter
        filter = binding.filter;
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });



        // infoOpen
        info = binding.infoButton;
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConstraintLayout infoLayout = binding.infoLayout;

                if(infoLayout.getVisibility() == View.GONE) {
                    infoLayout.setVisibility(View.VISIBLE);
                    info.setImageResource(R.drawable.baseline_remove_24);
                } else {
                    infoLayout.setVisibility(View.GONE);
                    info.setImageResource(R.drawable.baseline_add_24);
                }

            }
        });



        // closeButton
        closeButton = binding.closeButton;
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.infoLayout.setVisibility(View.GONE);
                binding.selectedLayout.setVisibility(View.GONE);
            }
        });



        // foodViewPager
        foodViewPager = binding.foodViewPager;
        foodViewPagerAdapter = new FoodViewPagerAdapter(this);
        foodViewPager.setAdapter(foodViewPagerAdapter);

        foodViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageSelected (int position) {
                super.onPageSelected(position);
                currentPage = position;
                Log.d("MainFragment", "[foodViewPager] currentPage = " + currentPage);
            }
        });

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == foodViewPagerAdapter.getItemCount()) {
                    currentPage = -1;
                }

                foodViewPager.setCurrentItem(++currentPage, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);



        // wanteatbutton
        wanteatbutton = (Button)view.findViewById(R.id.iwanttoeat);
        wanteatbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).Excel4(AmplifyApi.newSet,2);
                AmplifyApi.PersonalizePOST(currentItem,MainActivity.userId);
                ((MainActivity)getActivity()).Excel4(AmplifyApi.newSet,1);
                AmplifyApi.RealTimeBestPost(currentItem,null,null);
                AmplifyApi.InteractionPost(currentItem,MainActivity.userId);

                while (!MainActivity.modifyComplete) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.modifyComplete = false;

                AmplifyApi.PersonalizeGet(adapter, getActivity(), MainActivity.userId);
                //((MainActivity)getActivity()).Excel4(AmplifyApi.newSet,1);
                AmplifyApi.RealTimeBestGet();
                AmplifyApi.InteractionGet(MainActivity.userId);

                ((MainActivity)getActivity()).Excel(checkfirst,adapter);
                //setRecent(MainActivity.sharedPref.getInt("current",1));
                //PersonalizePOST(-99);
                //recyclerView.addItemDecoration(new TempRecyclerDecoration(25,25));
            /*
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setAdapter(new BookmarkRecyclerViewAdapter());
             */

            }
        });



        // 추가입력
        Button writefood = (Button)view.findViewById(R.id.foodwritebutton);
        writefood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).popup();
                //items = new String[]{"sibal","ssibal","sssibal","ssssibal"};

                //edit = (AutoCompleteTextView)view.findViewById(R.id.foodwritetext3);
                //edit.setText("sex");
                //edit.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,items));
            }
        });

        // apply
        Button apply = (Button)view.findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check2 = (CheckBox)view.findViewById(R.id.mainFilter2);
                check3 = (CheckBox)view.findViewById(R.id.mainFilter3);
                check4 = (CheckBox)view.findViewById(R.id.mainFilter4);
                check5 = (CheckBox)view.findViewById(R.id.mainFilter5);
                check6 = (CheckBox)view.findViewById(R.id.mainFilter6);
                check7 = (CheckBox)view.findViewById(R.id.mainFilter7);
                check8 = (CheckBox)view.findViewById(R.id.mainFilter8);
                check9 = (CheckBox)view.findViewById(R.id.mainFilter9);
                check10 = (CheckBox)view.findViewById(R.id.mainFilter10);
                check11 = (CheckBox)view.findViewById(R.id.mainFilter11);
                check12 = (CheckBox)view.findViewById(R.id.mainFilter12);
                ArrayList<Integer> haejin = new ArrayList();
                if(check2.isChecked()){
                    haejin.add(0);
                }
                else if(!check2.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 0){
                            haejin.remove(i);
                        }
                    }
                }
                if(check3.isChecked()){
                    haejin.add(1);
                }
                else if(!check3.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 1){
                            haejin.remove(i);
                        }
                    }
                }
                if(check4.isChecked()){
                    haejin.add(2);
                }
                else if(!check4.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 2){
                            haejin.remove(i);
                        }
                    }
                }
                if(check5.isChecked()){
                    haejin.add(3);
                }
                else if(!check5.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 3){
                            haejin.remove(i);
                        }
                    }
                }
                if(check6.isChecked()){
                    haejin.add(4);
                }
                else if(!check6.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 4){
                            haejin.remove(i);
                        }
                    }
                }
                if(check7.isChecked()){
                    haejin.add(5);
                }
                else if(!check7.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 5){
                            haejin.remove(i);
                        }
                    }
                }
                if(check8.isChecked()){
                    haejin.add(6);
                }
                else if(!check8.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 6){
                            haejin.remove(i);
                        }
                    }
                }
                if(check9.isChecked()){
                    haejin.add(7);
                }
                else if(!check9.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 7){
                            haejin.remove(i);
                        }
                    }
                }
                if(check10.isChecked()){
                    haejin.add(8);
                }
                else if(!check10.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 8){
                            haejin.remove(i);
                        }
                    }
                }
                if(check11.isChecked()){
                    haejin.add(9);
                }
                else if(!check11.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 9){
                            haejin.remove(i);
                        }
                    }
                }
                if(check12.isChecked()){
                    haejin.add(10);
                }
                else if(!check12.isChecked()){
                    for(int i = 0;i < haejin.size(); i++){
                        if(haejin.get(i) == 10){
                            haejin.remove(i);
                        }
                    }
                }
                System.out.println(haejin.size());
                ((MainActivity)getActivity()).Excel(haejin,adapter);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        return view;
    }

    public void setRecent(String recent){
        MainActivity.editor.putString("current", recent).apply();

        //todo 리사이클러 뷰에서 아이템을 선택하면 recent에 선택된 음식을 띄우고, 아래에는 상세정보
        binding.selectedLayout.setVisibility(View.VISIBLE);
        //binding.recent.setText(String.valueOf(recent));
        binding.recent.setText(recent);
        ((MainActivity) getActivity()).Excel2(recent, foodViewPagerAdapter);
        currentItem = recent;
    }

    public void getPersonalize() {
        AmplifyApi.PersonalizeGet(adapter, getActivity(), MainActivity.userId);
    }

    public int ggum(String name){
        //System.out.println(((MainActivity)getActivity()).Excel3(name));
        return ((MainActivity)getActivity()).Excel3(name);
    }

}