package com.example.test3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.amplifyframework.core.Amplify;
import com.example.test3.databinding.FragmentLoginBinding;
import com.example.test3.databinding.FragmentReadBinding;

import java.util.ArrayList;

public class LoginFragment extends Fragment {

    private View view;
    private FragmentLoginBinding binding;

    Button loginButton;
    Button registerButton;
    EditText idEditText;
    EditText passEditText;
    CheckBox loginAuto;

    SharedPreferences sharedPref = MainActivity.sharedPref;
    SharedPreferences.Editor editor = MainActivity.sharedPref.edit();


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

        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        view = binding.getRoot();


        // idEditText
        idEditText = binding.idEditText;

        // passEditText
        passEditText = binding.passEditText;
        
        // loginAuto
        loginAuto = binding.loginAuto;



        // 자동 로그인
        if (sharedPref.getBoolean("Auto_Login_Enabled", false)) {
            idEditText.setText(sharedPref.getString("userId", ""));
            passEditText.setText(sharedPref.getString("userPw", ""));
            loginAuto.setChecked(true);
        }


        // loginButton
        loginButton = binding.loginButton;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplify.Auth.signIn(
                        idEditText.getText().toString(),
                        passEditText.getText().toString(),
                        result -> {
                            Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");

                            if (result.isSignInComplete()) {
                                Handler mHandler = new Handler(Looper.getMainLooper());
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "로그인에 성공했습니다!", Toast.LENGTH_LONG).show();
                                    }
                                }, 0);

                                // 로그인한 유저의 정보를 받아옴
                                AmplifyApi.userAttr();

                                // 정보를 받아올 때까지 대기
                                while (!MainActivity.loadComplete) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                MainActivity.loadComplete = false;

                                // 자동 로그인이 활성화됐는지 체크
                                if (loginAuto.isChecked()) {
                                    String userId = idEditText.getText().toString();
                                    String userPw = passEditText.getText().toString();

                                    editor.putString("userId", userId);
                                    editor.putString("userPw", userPw);
                                    editor.putBoolean("Auto_Login_Enabled", true);
                                    editor.commit();
                                } else {
                                    // 아직 id는 저장
                                    editor.putString("userId", "");
                                    editor.putString("userPw", "");
                                    editor.putBoolean("Auto_Login_Enabled", false);
                                    editor.commit();
                                }


                                System.out.println("황자자자자자자자" + MainActivity.userId);
                                AmplifyApi.PersonalizeGet(MainActivity.userId);
                                AmplifyApi.InteractionGet(MainActivity.userId);
                                AmplifyApi.RealTimeBestGet();
                                
                                // Personalize 안될때 쓸 것
                                /*
                                if (AmplifyApi.newSet == null) {
                                    AmplifyApi.newSet = new ArrayList<>();
                                    AmplifyApi.newSet.add("1");
                                    AmplifyApi.newSet.add("2");
                                }
                                 */

                                ((MainActivity)getActivity()).Excel4(AmplifyApi.newSet,1);
                                System.out.println(AmplifyApi.newSet.get(0));

                                // 로그 출력 및 화면 전환
                                Log.d("", "current userId = " + MainActivity.userId);
                                ((MainActivity) getActivity()).setFrag(0);
                            } else {
                                Handler mHandler = new Handler(Looper.getMainLooper());
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "로그인에 실패했습니다.", Toast.LENGTH_LONG).show();
                                    }
                                }, 0);
                            }
                        },
                        error -> {
                            Log.e("AuthQuickstart", error.toString());
                            
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "아이디 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show();
                                }
                            }, 0);
                            passEditText.setText(null);
                        }
                );
            }
        });



        // registerButton
        registerButton = binding.registerButton;
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idEditText.setText(null);
                passEditText.setText(null);

                ((MainActivity) getActivity()).setFrag(8);
            }
        });

        return view;
    }

}
