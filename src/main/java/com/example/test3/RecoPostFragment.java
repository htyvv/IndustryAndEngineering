package com.example.test3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.example.test3.databinding.FragmentPostBinding;
import com.example.test3.databinding.FragmentRecoPostBinding;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.github.irshulx.models.EditorTextStyle;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import petrov.kristiyan.colorpicker.ColorPicker;

public class RecoPostFragment extends Fragment { // 9

    private View view;
    private FragmentRecoPostBinding binding;

    TextInputEditText recoPostTitleTextInputEditText;
    Editor recoPostContentInput;
    ImageButton recoPostButton;
    ImageButton recoPostBackButton;

    EditText recoPostTagInput;
    TextView recoPostTagResult;
    ImageButton recoPostTagAdd;
    ImageButton recoPostTagRemove;

    String tag = "";
    int tagCount = 0;
    private final int GET_GALLERY_IMAGE = 200;


    @Override
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

        binding = FragmentRecoPostBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        // recoPostTitleTextInputEditText
        recoPostTitleTextInputEditText = binding.recoPostTitleTextInputEditText;
        recoPostTitleTextInputEditText.setHint("제목을 입력해주세요.");
        recoPostTitleTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    recoPostTitleTextInputEditText.setHint("");
                } else
                    recoPostTitleTextInputEditText.setHint("제목을 입력해주세요.");
            }
        });


        // recoPostContentInput
        recoPostContentInput = binding.recoPostContentInput;

        binding.rActionH1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.updateTextStyle(EditorTextStyle.H1);
            }
        });

        binding.rActionH2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.updateTextStyle(EditorTextStyle.H2);
            }
        });

        binding.rActionH3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.updateTextStyle(EditorTextStyle.H3);
            }
        });

        binding.rActionBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.updateTextStyle(EditorTextStyle.BOLD);
            }
        });

        binding.rActionItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.updateTextStyle(EditorTextStyle.ITALIC);
            }
        });

        binding.rActionColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        binding.rActionIndent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.updateTextStyle(EditorTextStyle.INDENT);
            }
        });

        binding.rActionOutdent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.updateTextStyle(EditorTextStyle.OUTDENT);
            }
        });

        binding.rActionBulleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.insertList(false);
            }
        });

        /*
        binding.rActionUnorderedNumbered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.insertList(true);
            }
        });

         */

        binding.rActionBlockquote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.updateTextStyle(EditorTextStyle.BLOCKQUOTE);
            }
        });

        binding.rActionHyperlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.insertLink();
            }
        });

        binding.rActionInsertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //postContentInput.openImagePicker();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        binding.rActionErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoPostContentInput.clearAllContents();
            }
        });

        recoPostContentInput.setEditorListener(new EditorListener() {
            @Override
            public void onTextChanged(EditText editText, Editable text) {
                // Toast.makeText(EditorTestActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpload(Bitmap image, String uuid) {

                String url="https://s3-ap-northeast-2.amazonaws.com/babimagelist/public/";
                RestOptions options = RestOptions.builder()
                        .addPath("image-count")
                        .build();
                Amplify.API.get("bab2",options, respond->{
                            try {
                                Log.d("RecoPostFragment","onUpload:"+respond+"");
                                Log.d("RecoPostFragment","onUpload:"+respond.getData().asJSONObject().getInt("count")+"");
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                                byte[] bitmapdata = bos.toByteArray();
                                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                                InputStream exampleInputStream = bs;
                                Amplify.Storage.uploadInputStream(
                                        respond.getData().asJSONObject().getInt("count")+".jpeg",
                                        exampleInputStream,
                                        result -> {
                                            Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey());
                                            recoPostContentInput.onImageUploadComplete(url+result.getKey(), uuid);
                                        },
                                        storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
                                );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> Log.e("interaction save", "interaction failed.", error)
                );


                //do your upload image operations here, once done, call onImageUploadComplete and pass the url and uuid as reference.
                //postContentInput.onImageUploadComplete("https://babimagebucket.s3.ap-northeast-2.amazonaws.com/23231.png", uuid);
                //postContentInput.onImageUploadFailed(uuid);
            }

            @Override
            public View onRenderMacro(String name, Map<String, Object> props, int index) {
                return null;
            }
        });

        recoPostContentInput.render();



        // recoPostTagInput
        recoPostTagInput = binding.recoPostTagInput;
        recoPostTagInput.setText(null);
        recoPostTagInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (!recoPostTagInput.getText().toString().equals("")) {
                            if (tagCount < 5) {
                                tag = tag + "#" + recoPostTagInput.getText().toString().replace("\n", "") + " ";
                                recoPostTagResult.setText(tag);
                                recoPostTagInput.setText(null);
                                tagCount++;
                            } else {
                                recoPostTagInput.setText(null);
                                Toast.makeText(getContext(), "태그는 5개까지만 가능합니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                }

                return true;
            }
        });

        // recoPostTagResult
        recoPostTagResult = binding.recoPostTagResult;
        recoPostTagResult.setText(null);


        // recoPostTagAdd
        recoPostTagAdd = binding.recoPostTagAdd;
        recoPostTagAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recoPostTagInput.getText().toString().equals("")) {
                    if (tagCount < 5) {
                        tag = tag + "#" + recoPostTagInput.getText().toString() + " ";
                        recoPostTagResult.setText(tag);
                        recoPostTagInput.setText(null);
                        tagCount++;
                    } else {
                        recoPostTagInput.setText(null);
                        Toast.makeText(getContext(), "태그는 5개까지만 가능합니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        // recoPostTagRemove
        recoPostTagRemove = binding.recoPostTagRemove;
        recoPostTagRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagCount > 0) {
                    int lastTag = tag.lastIndexOf("#");
                    if (lastTag != -1) {
                        tag = tag.substring(0, lastTag);
                        recoPostTagResult.setText(tag);
                        tagCount--;
                    }
                }
            }
        });



        // recoPostButton
        recoPostButton = binding.recoPostButton;
        recoPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: userAttr에서 sub, name 받아와서 password, name 인자로 넘겨줘야 함
                // 첫번째 URL 추출
                String content = recoPostContentInput.getContentAsHTML();
                String imgURL;
                int imgStart = content.indexOf("<img src=\"");
                if (imgStart == -1) {
                    imgURL = "https://babimagelist.s3.ap-northeast-2.amazonaws.com/public/noimage.jpg";
                } else {
                    int imgEnd = content.indexOf("\" />", imgStart + 10);
                    imgURL = content.substring(imgStart + 10, imgEnd);
                }



                AmplifyApi.RecommendBoardPost(MainActivity.userId, recoPostContentInput.getContentAsHTML(),
                        recoPostTitleTextInputEditText.getText().toString(), MainActivity.userName,
                        tag, imgURL);
                recoPostTitleTextInputEditText.setText(null);
                ((MainActivity) getActivity()).setFrag(3);
            }
        });

        // recoPostBackButton
        recoPostBackButton = binding.recoPostBackButton;
        recoPostBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setFrag(3);
            }
        });

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                recoPostContentInput.insertImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // editor.RestoreState();
            Toast.makeText(getActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(getActivity());  // ColorPicker 객체 생성
        ArrayList<String> colors = new ArrayList<>();  // Color 넣어줄 list

        colors.add("#000000"); // black
        colors.add("#9b0303"); // Sangria
        colors.add("#55647E"); // Kashmir Blue
        colors.add("#784491"); // Studio
        colors.add("#DB8000"); // Mango Tango
        colors.add("#31571B"); // Verdun Green

        colors.add("#3B3B3B"); // Eclipse
        colors.add("#B30F0F"); // Venetian Red
        colors.add("#013A65"); // Prussian Blue
        colors.add("#A460DC"); // Violet
        colors.add("#BF5C1A"); // Chocolate
        colors.add("#318500"); // Green

        colors.add("#525252"); // Mortar
        colors.add("#DD3730"); // Cinnabar
        colors.add("#104A77"); // Dark Cerculean
        colors.add("#B07ACB"); // Lilac Bush
        colors.add("#E44E0C"); // Persimmon
        colors.add("#3BA500"); // Islamic Green

        colors.add("#979797"); // Shady Lady
        colors.add("#E71313"); // Fire Engine Red
        colors.add("#457598"); // Jelly Bean
        colors.add("#C378E7"); // Medium Purple
        colors.add("#FF8100"); // Dark Orange
        colors.add("#66BF34"); // Sushi

        colors.add("#A7A7A7"); // Dark Gray
        colors.add("#F14C4C"); // Sunset Orange
        colors.add("#0053B3"); // Cobalt
        colors.add("#C793E1"); // Wisteria
        colors.add("#FF9500"); // Orange
        colors.add("#9ED10F"); // Inch Worm

        colors.add("#E2E5E8"); // Zircon
        colors.add("#FF9595"); // Mona Lisa
        colors.add("#2476FF"); // Dodger Blue
        colors.add("#F9EFFF"); // Magnolia
        colors.add("#EEB102"); // Selective Yellow
        colors.add("#D2F6D0"); // Tea Green

        colors.add("#E0E0E0"); // Gainsboro
        colors.add("#FFD2D2"); // Pink
        colors.add("#4CB2FF"); // Maya Blue
        colors.add("#EB00A0"); // Hollywood Cerise
        colors.add("#FFCD00"); // Tangerine Yellow
        colors.add("#00D88F"); // Caribbean Green

        colors.add("#F0F0F0"); // White Smoke
        colors.add("#FFF0F0"); // Snow
        colors.add("#9CD6FF"); // Columbia Blue
        colors.add("#FF25E5"); // Razzle Dazzle Rose
        colors.add("#FFDCA3"); // Frangipani
        colors.add("#27BEB8"); // Light Sea Green

        colors.add("#F8F8F8"); // White Snow
        colors.add("#FFFFFF"); // 임시
        colors.add("#D1E9FF"); // Light Cyan
        colors.add("#FFFFFF"); // 임시
        colors.add("#FDF0DD"); // Forget Me Not
        colors.add("#25B7D3"); // Summer Sky

        colors.add("#FFFFFF"); // White
        colors.add("#FFFFFF"); // 임시
        colors.add("#EFF6FF"); // Alice Blue
        colors.add("#FFFFFF"); // 임시
        colors.add("#FFF2BD"); // Moccasin
        colors.add("#FFFFFF"); // 임시


        colorPicker.setColors(colors)  // 만들어둔 list 적용
                .setColumns(6)  // 6열로 설정
                .setRoundColorButton(false)  // 원형 버튼으로 설정
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        recoPostContentInput.updateTextColor(colors.get(position));  // OK 버튼 클릭 시 이벤트
                    }

                    @Override
                    public void onCancel() {
                        // Cancel 버튼 클릭 시 이벤트
                    }
                }).show();  // dialog 생성
    }
}