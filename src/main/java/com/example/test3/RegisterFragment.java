package com.example.test3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.example.test3.databinding.FragmentRegisterBinding;

import java.util.ArrayList;
import java.util.List;

public class RegisterFragment extends Fragment {

    private View view;
    private FragmentRegisterBinding binding;

    RegisterFragment currentFragment;
    Bundle bundle = new Bundle();

    EditText registerID;
    EditText registerEmail;
    EditText registerNick;
    EditText registerPass;
    EditText registerPassCheck;
    EditText registerBirth;

    Button registerBirthSelectButton;
    Button registerJoinButton;
    Button registerCancelButton;

    RadioGroup registerGenderGroup;
    RadioButton registerMale;
    RadioButton registerFemale;

    ImageButton registerBack;
    EditText registerVerifyCode;
    Button registerVerifyCheckButton;


    String currentGender;


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

        binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        currentFragment = this;


        // registerID
        registerID = binding.registerID;

        // registerEmail
        registerEmail = binding.registerEmail;

        // registerNick
        registerNick = binding.registerNick;

        // registerPass
        registerPass = binding.registerPass;

        // registerPassCheck
        registerPassCheck = binding.registerPassCheck;

        // registerBirth
        registerBirth = binding.registerBirth;


        // registerBirthSelectButton
        registerBirthSelectButton = binding.registerBirthSelectButton;
        registerBirthSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(currentFragment);
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
            }
        });


        // registerJoinButton
        registerJoinButton = binding.registerJoinButton;
        registerJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForm() == 0) {
                    signUp();
                }
            }
        });

        // registerCancelButton
        registerCancelButton = binding.registerCancelButton;
        registerCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 초기화 후 가입 화면으로 전환
                changeRegisterText(null);

                ((MainActivity) getActivity()).setFrag(7);
            }
        });


        // registerGenderGroup
        registerGenderGroup = binding.registerGenderGroup;

        // registerMale
        registerMale = binding.registerMale;
        registerMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentGender = "male";
            }
        });

        // registerFemale
        registerFemale = binding.registerFemale;
        registerFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentGender = "female";
            }
        });



        // registerBack
        registerBack = binding.registerBack;
        registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRegisterEnabled(true);
                changeRegisterVisibility(View.VISIBLE);

                changeVerifyEnabled(false);
                changeVerifyVisibility(View.GONE);
            }
        });

        // registerVerifyCode
        registerVerifyCode = binding.registerVerifyCode;

        // registerVerifyCheckButton
        registerVerifyCheckButton = binding.registerVerifyCheckButton;
        registerVerifyCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });


        return view;
    }

    public void processDatePickerResult(int year, int month, int day){
        String year_string = Integer.toString(year);

        String month_string;
        if (month < 10) month_string = "0" + Integer.toString(month + 1);
        else month_string = Integer.toString(month + 1);

        String day_string;
        if (day < 10) day_string = "0" + Integer.toString(day);
        else day_string = Integer.toString(day);

        String dateMessage = (year_string + "/" + month_string + "/" + day_string);
        bundle.putInt("s_year", Integer.parseInt(year_string));
        bundle.putInt("s_month", Integer.parseInt(month_string));
        bundle.putInt("s_day", Integer.parseInt(day_string));

        registerBirth.setText(dateMessage);
    }

    public int checkForm() {

        AlertDialog.Builder oDialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);

        if (registerID.getText().toString().length() == 0
                || registerPass.getText().toString().length() == 0
                || registerPassCheck.getText().toString().length() == 0
                || registerEmail.getText().toString().length() == 0
                || registerBirth.getText().toString().length() == 0) {

            oDialog.setMessage("입력되지 않은 내용이 있습니다.")
                    .setTitle("오류")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Log.i("Dialog", "확인");
                        }
                    })
                    .setCancelable(true) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                    .show();

            return -1;
        }

        if (registerPass.toString().length() < 6) {

            registerPass.setText(null);
            registerPassCheck.setText(null);

            oDialog.setMessage("비밀번호는 6자 이상이어야 합니다.")
                    .setTitle("오류")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Log.i("Dialog", "확인");
                        }
                    })
                    .setCancelable(true) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                    .show();

            return -1;
        }

        if (!(registerPass.getText().toString().equals(registerPassCheck.getText().toString()))) {

            registerPassCheck.setText(null);

            oDialog.setMessage("비밀번호가 일치하지 않습니다.")
                    .setTitle("오류")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Log.i("Dialog", "확인");
                        }
                    })
                    .setCancelable(true) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                    .show();

            return -1;
        }



        return 0;
    }

    public void signUp() {

        Log.d("signUp", registerEmail.getText().toString() + "\n" + registerBirth.getText().toString() + "\n" + currentGender + "\n" + registerNick.getText().toString());

        List<AuthUserAttribute> attributesList = new ArrayList<>();
        attributesList.add(new AuthUserAttribute(AuthUserAttributeKey.email(), registerEmail.getText().toString()));
        attributesList.add(new AuthUserAttribute(AuthUserAttributeKey.birthdate(), registerBirth.getText().toString()));
        attributesList.add(new AuthUserAttribute(AuthUserAttributeKey.gender(), currentGender));
        attributesList.add(new AuthUserAttribute(AuthUserAttributeKey.nickname(), registerNick.getText().toString()));

        // 정보 입력
        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttributes(attributesList)
                .build();
        Amplify.Auth.signUp(registerID.getText().toString(), registerPass.getText().toString(), options,
                result -> {
                    Log.i("AuthQuickStart", "Result: " + result.toString());

                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            changeRegisterEnabled(false);
                            changeRegisterVisibility(View.GONE);

                            changeVerifyEnabled(true);
                            changeVerifyVisibility(View.VISIBLE);
                        }
                    }, 0);

                    attributesList.clear();
                },
                error -> {
                    Log.e("AuthQuickStart", "Sign up failed", error);

                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "회원가입에 실패했습니다.\n" + error, Toast.LENGTH_LONG).show();
                        }
                    }, 0);

                    attributesList.clear();
                }
        );

    }

    public void verify() {

        // 이메일 인증
        Amplify.Auth.confirmSignUp(
                registerID.getText().toString(),
                registerVerifyCode.getText().toString(),
                result -> {
                    Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");

                    if (result.isSignUpComplete()) {
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "회원가입에 성공했습니다!", Toast.LENGTH_LONG).show();

                                Amplify.Auth.signIn(
                                        registerID.getText().toString(),
                                        (registerPass.getText().toString()),
                                        result -> {
                                            Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");

                                            if (result.isSignInComplete()) {

                                                Handler mHandler = new Handler(Looper.getMainLooper());
                                                mHandler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

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

                                                        changeRegisterEnabled(true);
                                                        changeRegisterVisibility(View.VISIBLE);
                                                        changeRegisterText(null);

                                                        changeVerifyEnabled(false);
                                                        changeVerifyVisibility(View.GONE);
                                                        changeVerifyText(null);

                                                        ((MainActivity) getActivity()).setFrag(0);

                                                    }
                                                }, 0);


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
                                        }
                                );
                            }
                        }, 0);
                    } else {
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                changeRegisterEnabled(true);
                                changeRegisterVisibility(View.VISIBLE);

                                changeVerifyEnabled(false);
                                changeVerifyVisibility(View.GONE);
                                changeVerifyText(null);

                                Toast.makeText(getContext(), "회원가입에 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }, 0);
                    }
                },
                error -> {
                    Log.e("AuthQuickstart", error.toString());

                    changeVerifyText(null);

                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "이메일 인증코드가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                        }
                    }, 0);
                }
        );

    }

    public void changeRegisterEnabled(boolean b) {
        registerID.setEnabled(b);
        registerEmail.setEnabled(b);
        registerNick.setEnabled(b);
        registerPass.setEnabled(b);
        registerPassCheck.setEnabled(b);
        registerBirth.setEnabled(b);
        registerBirthSelectButton.setEnabled(b);
        registerGenderGroup.setEnabled(b);
        registerJoinButton.setEnabled(b);
        registerCancelButton.setEnabled(b);
    }

    public void changeRegisterVisibility(int i) {
        registerID.setVisibility(i);
        registerEmail.setVisibility(i);
        registerNick.setVisibility(i);
        registerPass.setVisibility(i);
        registerPassCheck.setVisibility(i);
        registerBirth.setVisibility(i);
        registerBirthSelectButton.setVisibility(i);
        registerGenderGroup.setVisibility(i);
        registerJoinButton.setVisibility(i);
        registerCancelButton.setVisibility(i);
    }

    public void changeRegisterText(String s) {
        registerID.setText(s);
        registerEmail.setText(s);
        registerNick.setText(s);
        registerPass.setText(s);
        registerPassCheck.setText(s);
        registerBirth.setText(s);
    }


    public void changeVerifyEnabled(boolean b) {
        registerBack.setEnabled(b);
        registerVerifyCode.setEnabled(b);
        registerVerifyCheckButton.setEnabled(b);
    }

    public void changeVerifyVisibility(int i) {
        registerBack.setVisibility(i);
        registerVerifyCode.setVisibility(i);
        registerVerifyCheckButton.setVisibility(i);
    }

    public void changeVerifyText(String s) {
        registerVerifyCode.setText(s);
    }

}