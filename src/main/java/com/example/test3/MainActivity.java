package com.example.test3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {

    TextView list_excel;
    TextView Carlorie;
    TextView Carbo;
    TextView Protein;
    TextView Fat;
    TextView Price;
    ImageView foodpic;
    ArrayAdapter<String> arrayAdapter;
    String edit1text = null;
    String edit2text = null;
    String edit3text = null;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private BottomNavigationView bottomNavigationView; // 바텀네비게이션 뷰
    private MainFragment mainFragment; // fragment_main
    private BoardFragment boardFragment; // fragment_board
    private PostFragment postFragment; // fragment_post
    private RecoboardFragment recoboardFragment;
    private DogPeopleFragment dogpeopleFragment;
    private ReadFragment readFragment;
    private ModifyFragment modifyFragment;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    String[] items;

    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    static boolean loadComplete = false;
    static boolean modifyComplete = false;

    static String userId = "";
    static String userName = "";

    static int maxPage = 1;
    static int reco_maxPage = 1;
    static int boardId;
    static int recoboardId;
    static int previousPage = 3;

    static String modifyTitle = "";
    static String modifyContent = "";
    static String modifyTag = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.mainChangingFrame, new MainFragment()).commit();



        // list_excel
        list_excel = (TextView)findViewById(R.id.mainBanner);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ArrayList<Integer> imsee = new ArrayList();
        Excel(imsee,null);



        // 상단 바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // bottomNavigation 추가
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.recommend:
                        setFrag(0);
                        break;
                    case R.id.board:
                        setFrag(3);
                        break;
                }

                return true;
            }
        });

        // fragment_main 추가
        //mainFragment = new MainFragment();

        // fragment_board 추가
        //boardFragment = new BoardFragment();

        // fragment_post 추가
        //postFragment = new PostFragment();

        // fragment_recoboard 추가
        //recoboardFragment = new RecoboardFragment();

        // fragment_dogpeople 추가
        //dogpeopleFragment = new DogPeopleFragment();

        // fragment_read 추가
        //readFragment = new ReadFragment();

        // fragment_modify 추가
        //modifyFragment = new ModifyFragment();

        // fragment_login 추가
        //loginFragment = new LoginFragment();

        // fragment_register 추가
        //registerFragment = new RegisterFragment();

        TextView mainBanner = (TextView) findViewById(R.id.mainBanner);
        mainBanner.setSingleLine();
        mainBanner.setMarqueeRepeatLimit(-1);
        mainBanner.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mainBanner.setSelected(true);



        setFrag(7); // 첫화면은 fragment_main으로 설정

    }

    // 프래그먼트 교체가 일어나는 메소드
    public void setFrag(int n){
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        switch (n){
            case 0:
                transaction.replace(R.id.mainChangingFrame, new MainFragment());
                transaction.commit();
                break;
            case 1:
                transaction.replace(R.id.mainChangingFrame, new BoardFragment());
                transaction.commit();
                break;
            case 2:
                transaction.replace(R.id.mainChangingFrame, new PostFragment());
                transaction.commit();
                break;
            case 3:
                transaction.replace(R.id.mainChangingFrame, new RecoboardFragment());
                transaction.commit();
                break;
            case 4:
                transaction.replace(R.id.mainChangingFrame, new DogPeopleFragment());
                transaction.commit();
                break;
            case 5:
                transaction.replace(R.id.mainChangingFrame, new ReadFragment());
                transaction.commit();
                break;
            case 6:
                transaction.replace(R.id.mainChangingFrame, new ModifyFragment());
                transaction.commit();
                break;
            case 7:
                transaction.replace(R.id.mainChangingFrame, new LoginFragment());
                transaction.commit();
                break;
            case 8:
                transaction.replace(R.id.mainChangingFrame, new RegisterFragment());
                transaction.commit();
                break;
            case 9:
                transaction.replace(R.id.mainChangingFrame, new RecoPostFragment());
                transaction.commit();
                break;
            case 10:
                transaction.replace(R.id.mainChangingFrame, new RecoModifyFragment());
                transaction.commit();
                break;
            case 11:
                transaction.replace(R.id.mainChangingFrame, new RecoReadFragment());
                transaction.commit();
                break;
        }
    }

    public void popup(){
        LinearLayout dialogView;
        dialogView = (LinearLayout) View.inflate(MainActivity.this,R.layout.fragment_foodwritepopup,null);
        final AutoCompleteTextView edit1 = ((AutoCompleteTextView)dialogView.findViewById(R.id.foodwritetext1));
        final AutoCompleteTextView edit2 = ((AutoCompleteTextView)dialogView.findViewById(R.id.foodwritetext2));
        final AutoCompleteTextView edit3 = ((AutoCompleteTextView)dialogView.findViewById(R.id.foodwritetext3));
        AlertDialog.Builder dlg=new AlertDialog.Builder(MainActivity.this);

        TextView historytext = (TextView)dialogView.findViewById(R.id.historytext);
        historytext.setSingleLine();
        historytext.setMarqueeRepeatLimit(-1);
        historytext.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        historytext.setSelected(true);
        String historytextset = "";
        for(int i = AmplifyApi.newInterSet.size(); i>0;i--){
            historytextset += AmplifyApi.newInterSet.get(i-1);
            if(i != 1){
                historytextset += " - ";
            }
        }
        historytext.setText("최근 먹은 기록 : " + historytextset);
        System.out.println(AmplifyApi.newSet.get(0));

        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!edit1.getText().toString().equals("")){
                    edit1text = edit1.getText().toString();
                    Excel4(AmplifyApi.newSet,2);
                    AmplifyApi.PersonalizePOST(edit1text,userId);
                    Excel4(AmplifyApi.newSet,1);
                    AmplifyApi.InteractionPost(edit1text,userId);
                }
                if(!edit2.getText().toString().equals("")){
                    edit2text = edit1.getText().toString();
                    Excel4(AmplifyApi.newSet,2);
                    AmplifyApi.PersonalizePOST(edit2text,userId);
                    Excel4(AmplifyApi.newSet,1);
                    AmplifyApi.InteractionPost(edit2text,userId);
                }
                if(!edit3.getText().toString().equals("")){
                    edit3text = edit1.getText().toString();
                    Excel4(AmplifyApi.newSet,2);
                    AmplifyApi.PersonalizePOST(edit3text,userId);
                    Excel4(AmplifyApi.newSet,1);
                    AmplifyApi.InteractionPost(edit3text,userId);
                }
                try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                AmplifyApi.PersonalizeGet(userId);
                Excel4(AmplifyApi.newSet,1);
                AmplifyApi.InteractionGet(userId);
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.setView(dialogView);
        dlg.show();
        System.out.println(items[1]);
        //String[] items = new String[]{"ss","ssb","sse","ssser"};
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line,items);

        //edit1.setAnimation(null);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_dropdown_item_1line,items);
        edit1.setAdapter(adapter); edit2.setAdapter(adapter); edit3.setAdapter(adapter);
        //edit.setAdapter(adapter);
        //edit.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_dropdown_item_1line,items));
    }

    public void Excel(ArrayList<Integer> aa, MainRecyclerAdapter madapter) {
        Workbook workbook = null;
        Sheet sheet = null;
        ArrayList<Integer> testlist1 = new ArrayList<Integer>();
        ArrayList<String> testlist2 = new ArrayList<String>();
        ArrayList<String> Classification = new ArrayList<String>(Arrays.asList("구이류","국 및 탕류",
                "찌개 및 전골류","면 및 만두류","밥류","볶음류","빵류","죽 및 스프류","찜류","튀김류","회류"));
        ArrayList<String> testlist3 = new ArrayList<String>();

        testlist1.add(0); testlist2.add("BLT샌드위치"); testlist2.add("간짜장"); testlist2.add("김밥");
        testlist1 = aa;
        if(AmplifyApi.newSet!=null){
            Excel4(AmplifyApi.newSet,1);
            testlist2 = AmplifyApi.newSet;
        }
        if(testlist1.size()!=0) {
            System.out.println(testlist1.get(0));
        }
        try {
            InputStream inputStream = getBaseContext().getResources().getAssets().open("food2.xls");
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);
            items = new String[sheet.getRows()];
            for(int i = 0;i<sheet.getRows();i++){
                items[i] = sheet.getCell(1,i).getContents();
            }
            if(testlist1.size()!=0){
                for(int i = 0; i < testlist2.size(); i++){
                    for(int j = 1; j < sheet.getRows(); j++){
                        String a = sheet.getCell(1,j).getContents();
                        if(testlist2.get(i).equals(a)){
                            for(int k = 0; k < testlist1.size(); k++){
                                if(sheet.getCell(2,j).getContents().equals(Classification.get(testlist1.get(k)))){
                                    testlist3.add(testlist2.get(i));
                                }
                            }
                        }
                    }
                }
            }
            else{
                for(int i = 0; i < testlist2.size(); i++){
                    testlist3.add(testlist2.get(i));
                }
            }

            if(madapter!=null){
                madapter.setValue(testlist3);
                if(AmplifyApi.newRealtimeSet!=null&&AmplifyApi.newRealtimeSet.size()!=0){
                    BannerName();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    public void Excel2(String recent, FoodViewPagerAdapter foodviewPagerAdapter) {
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            InputStream inputStream = getBaseContext().getResources().getAssets().open("food2.xls");
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);
            //System.out.println(sheet.getCell(0,1).getContents());
            Carlorie = (TextView)findViewById(R.id.carlorieValueText);
            Carbo = (TextView)findViewById(R.id.carboValueText);
            Protein = (TextView)findViewById(R.id.proteinValueText);
            Fat = (TextView)findViewById(R.id.fatValueText);
            Price = (TextView)findViewById(R.id.priceValueText);
            //foodpic = (FoodViewPagerAdapter)findViewById(R.id.foodviewPagerAdapter);

            AmplifyApi.RecommendBoardGet(foodviewPagerAdapter, this,0, recent);
            /*
            while (!MainActivity.modifyComplete) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            MainActivity.modifyComplete = false;

            */


            for(int i = 1; i < sheet.getRows(); i++) {
                if(sheet.getCell(1,i).getContents().equals(String.valueOf(recent))){
                    String c1 = sheet.getCell(6,i).getContents() + "Kcal";
                    String c2 = sheet.getCell(9,i).getContents() + "g";
                    String c3 = sheet.getCell(7,i).getContents() + "g";
                    String c4 = sheet.getCell(8,i).getContents() + "g";
                    String c5 = sheet.getCell(4,i).getContents() + "g";
                    Carlorie.setText(c1);
                    Carbo.setText(c2);
                    Protein.setText(c3);
                    Fat.setText(c4);
                    Price.setText(c5);
                    String name = "@drawable/fooddata" + sheet.getCell(0, i).getContents();
                    String packname = this.getPackageName();
                    System.out.println(name);

                    //int resId = getResources().getIdentifier(name,"drawable", packname);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    public int Excel3(String name){
        Workbook workbook = null;
        Sheet sheet = null;
        int resId = 0;
        try {
            InputStream inputStream = getBaseContext().getResources().getAssets().open("food2.xls");
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);
            for(int i = 1;i<sheet.getRows();i++) {
                if(sheet.getCell(1,i).getContents().equals(name)){
                    String name2 = "@drawable/fooddata" + sheet.getCell(0,i).getContents();
                    String packname = this.getPackageName();
                    System.out.println(name2);
                    resId = getResources().getIdentifier(name2,"drawable",packname);
                    System.out.println(resId);
                    return resId;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        return resId;
    }

    public void Excel4(ArrayList<String> num, int a){
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            InputStream inputStream = getBaseContext().getResources().getAssets().open("food2.xls");
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);
            //받아올 때 한글로 변환
            System.out.println("어레이 사이즈 : " + num.size());
            if(a==1){
                for(int k = 0;k<num.size();k++) {
                    //System.out.println("어레이 사이즈 : " + num.size());
                    for (int i = 1; i < sheet.getRows(); i++) {
                        if (sheet.getCell(0, i).getContents().equals(num.get(k))){
                            //System.out.println("받아왔을 때 전 : " + num.get(k));
                            num.set(k,sheet.getCell(1,i).getContents());
                            //System.out.println("받아왔을 때 후 : " + num.get(k));
                            break;
                        }
                    }
                }
            }
            //보낼 때 숫자로 변환
            else if(a==2){
                for(int k = 0;k<num.size();k++) {
                    for (int i = 1; i < sheet.getRows(); i++) {
                        if (sheet.getCell(1, i).getContents().equals(num.get(k))){
                            num.set(k,sheet.getCell(0,i).getContents());
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    public void BannerName(){
        list_excel.setText("현재 가장 인기 음식 : " + AmplifyApi.newRealtimeSet.get(0));
    }
}