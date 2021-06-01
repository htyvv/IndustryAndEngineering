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

import com.example.test3.databinding.FragmentRecoModifyBinding;
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

public class RecoModifyFragment extends Fragment { // 10

    private View view;
    private FragmentRecoModifyBinding binding;

    TextInputEditText recoModifyTitleTextInputEditText;
    Editor recoModifyContentInput;
    ImageButton recoModifyButton;
    ImageButton recoModifyBackButton;

    EditText recoModifyTagInput;
    TextView recoModifyTagResult;
    ImageButton recoModifyTagAdd;
    ImageButton recoModifyTagRemove;

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

        binding = FragmentRecoModifyBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        // recoModifyTitleTextInputEditText
        recoModifyTitleTextInputEditText = binding.recoModifyTitleTextInputEditText;
        recoModifyTitleTextInputEditText.setText(MainActivity.modifyTitle);


        // recoModifyContentInput
        recoModifyContentInput = binding.recoModifyContentInput;

        binding.rmActionH1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.updateTextStyle(EditorTextStyle.H1);
            }
        });

        binding.rmActionH2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.updateTextStyle(EditorTextStyle.H2);
            }
        });

        binding.rmActionH3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.updateTextStyle(EditorTextStyle.H3);
            }
        });

        binding.rmActionBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.updateTextStyle(EditorTextStyle.BOLD);
            }
        });

        binding.rmActionItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.updateTextStyle(EditorTextStyle.ITALIC);
            }
        });

        binding.rmActionColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        binding.rmActionIndent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.updateTextStyle(EditorTextStyle.INDENT);
            }
        });

        binding.rmActionOutdent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.updateTextStyle(EditorTextStyle.OUTDENT);
            }
        });

        binding.rmActionBulleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.insertList(false);
            }
        });

        /*
        binding.rmActionUnorderedNumbered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.insertList(true);
            }
        });

         */

        binding.rmActionBlockquote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.updateTextStyle(EditorTextStyle.BLOCKQUOTE);
            }
        });

        binding.rmActionHyperlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.insertLink();
            }
        });

        binding.rmActionInsertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //modifyContentInput.openImagePicker();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        binding.rmActionErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyContentInput.clearAllContents();
            }
        });

        recoModifyContentInput.setEditorListener(new EditorListener() {
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
                                Log.d("RecoModifyFragment","onUpload:"+respond+"");
                                Log.d("RecoModifyFragment","onUpload:"+respond.getData().asJSONObject().getInt("count")+"");
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
                                            recoModifyContentInput.onImageUploadComplete(url+result.getKey(), uuid);
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

        recoModifyContentInput.render(MainActivity.modifyContent);



        // recoModifyTagInput
        recoModifyTagInput = binding.recoModifyTagInput;
        recoModifyTagInput.setText(null);
        recoModifyTagInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (!recoModifyTagInput.getText().toString().equals("")) {
                            if (tagCount < 5) {
                                tag = tag + "#" + recoModifyTagInput.getText().toString() + " ";
                                recoModifyTagInput.setText(tag);
                                recoModifyTagInput.setText(null);
                                tagCount++;
                            } else {
                                Toast.makeText(getContext(), "태그는 5개까지만 가능합니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                }

                return true;
            }
        });

        // recoModifyTagResult
        recoModifyTagResult = binding.recoModifyTagResult;
        recoModifyTagResult.setText(null);


        // recoModifyTagAdd
        recoModifyTagAdd = binding.recoModifyTagAdd;
        recoModifyTagAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recoModifyTagInput.getText().toString().equals("")) {
                    if (tagCount < 5) {
                        tag = tag + "#" + recoModifyTagInput.getText().toString() + " ";
                        recoModifyTagResult.setText(tag);
                        recoModifyTagInput.setText(null);
                        tagCount++;
                    } else {
                        Toast.makeText(getContext(), "태그는 5개까지만 가능합니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        // recoModifyTagRemove
        recoModifyTagRemove = binding.recoModifyTagRemove;
        recoModifyTagRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagCount > 0) {
                    int lastTag = tag.lastIndexOf("#");
                    tag = tag.substring(0, lastTag);
                    tagCount--;
                }
            }
        });



        // recoModifyButton
        recoModifyButton = binding.recoModifyButton;
        recoModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 첫번째 URL 추출
                String content = recoModifyContentInput.getContentAsHTML();
                String imgURL;
                int imgStart = content.indexOf("<img src=\"");
                if (imgStart == -1) {
                    imgURL = "https://babimagelist.s3.ap-northeast-2.amazonaws.com/public/noimage.jpg";
                } else {
                    int imgEnd = content.indexOf("\" />", imgStart + 10);
                    imgURL = content.substring(imgStart, imgEnd);
                }



                AmplifyApi.RecommendBoardPut(Integer.toString(MainActivity.recoboardId), MainActivity.userId,
                        recoModifyContentInput.getContentAsHTML(), recoModifyTitleTextInputEditText.getText().toString(), MainActivity.userName,
                        tag, imgURL);

                while (!MainActivity.modifyComplete) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.modifyComplete = false;

                recoModifyTitleTextInputEditText.setText(null);
                ((MainActivity) getActivity()).setFrag(11);
            }
        });

        // recoModifyBackButton
        recoModifyBackButton = binding.recoModifyBackButton;
        recoModifyBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoModifyTitleTextInputEditText.setText(null);
                ((MainActivity) getActivity()).setFrag(11);
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
                recoModifyContentInput.insertImage(bitmap);
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
                        recoModifyContentInput.updateTextColor(colors.get(position));  // OK 버튼 클릭 시 이벤트
                    }

                    @Override
                    public void onCancel() {
                        // Cancel 버튼 클릭 시 이벤트
                    }
                }).show();  // dialog 생성
    }

}
