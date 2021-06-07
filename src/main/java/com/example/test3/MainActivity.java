package com.example.test3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
    public MainFragment mainFragment; // fragment_main
    private BoardFragment boardFragment; // fragment_board
    private PostFragment postFragment; // fragment_post
    private RecoboardFragment recoboardFragment;
    private DogPeopleFragment dogpeopleFragment;
    private ReadFragment readFragment;
    private ModifyFragment modifyFragment;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    String[] items;

    AlertDialog ad;
    ProgressDialog progressDialog;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

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


        getHashKey();


        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.mainChangingFrame, new MainFragment()).commit();



        // list_excel
        list_excel = (TextView)findViewById(R.id.mainBanner);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ArrayList<Integer> imsee = new ArrayList();
        Excel(imsee,null);

        AmplifyApi.mainActivity = this;

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
        mainFragment = new MainFragment();

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
                    AmplifyApi.PersonalizePOST(curitem(edit1text),userId);
                    Excel4(AmplifyApi.newSet,1);
                    AmplifyApi.InteractionPost(edit1text,userId);
                }
                if(!edit2.getText().toString().equals("")){
                    edit2text = edit2.getText().toString();
                    Excel4(AmplifyApi.newSet,2);
                    AmplifyApi.PersonalizePOST(curitem(edit2text),userId);
                    Excel4(AmplifyApi.newSet,1);
                    AmplifyApi.InteractionPost(edit2text,userId);
                }
                if(!edit3.getText().toString().equals("")){
                    edit3text = edit2.getText().toString();
                    Excel4(AmplifyApi.newSet,2);
                    AmplifyApi.PersonalizePOST(curitem(edit3text),userId);
                    Excel4(AmplifyApi.newSet,1);
                    AmplifyApi.InteractionPost(edit3text,userId);
                }

                while (!MainActivity.modifyComplete) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.modifyComplete = false;

                AmplifyApi.PersonalizeGet(MainFragment.adapter,MainActivity.this, userId);
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

    public void popup2(){
        if(progressDialog==null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("로딩중...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();
    }

    public void popup3(){
        LinearLayout dialogView;
        dialogView = (LinearLayout) View.inflate(MainActivity.this,R.layout.fragment_foodsirpopup,null);
        final AutoCompleteTextView edit1 = ((AutoCompleteTextView)dialogView.findViewById(R.id.foodsirtext1));
        AlertDialog.Builder dlg=new AlertDialog.Builder(MainActivity.this);


        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!edit1.getText().toString().equals("")){
                    String b = edit1.getText().toString();
                    ArrayList<String> a = new ArrayList<>();
                    a.add(b);
                    MainFragment.adapter.setValue(a);
                }


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
        edit1.setAdapter(adapter);
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
            items = new String[sheet.getRows()-1];
            for(int i = 1;i<sheet.getRows();i++){
                items[i-1] = sheet.getCell(1,i).getContents();
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

    public String curitem(String a) {
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            InputStream inputStream = getBaseContext().getResources().getAssets().open("food2.xls");
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);
            ArrayList<String> Classification = new ArrayList<String>(Arrays.asList("구이류", "국 및 탕류",
                    "찌개 및 전골류", "면 및 만두류", "밥류", "볶음류", "빵류", "죽 및 스프류", "찜류", "튀김류", "회류"));
            for (int i = 1; i < sheet.getRows(); i++) {
                if (sheet.getCell(1, i).getContents().equals(a)) {
                    for (int j = 0; j < 11; j++) {
                        if (sheet.getCell(2, i).getContents().equals(Classification.get(j))) {
                            return Integer.toString(j);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        return "99";
    }

    public void BannerName(){
        list_excel.setText("현재 가장 인기 음식 : " + AmplifyApi.newRealtimeSet.get(0));
    }


    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료한다. 2가지 경우가 있음
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(this, "권한 설정이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요.", Toast.LENGTH_LONG)
                            .show();
                    finish();
                } else {
                    Toast.makeText(this, "권한 설정이 거부되었습니다. 설정(앱 정보)에서 권한을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    public void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}