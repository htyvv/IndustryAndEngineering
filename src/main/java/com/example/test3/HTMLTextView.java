package com.example.test3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Hyunwoo Kim (kokohapps.com), on 2017. 6. 15..
 */
public class HTMLTextView extends androidx.appcompat.widget.AppCompatTextView implements Html.ImageGetter{

    public HTMLTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     *
     * @param source HTML 형식의 문자열
     */
    public void setHtmlText(String source){

        //TODO 임시 테스트용: source에 바로 url 넣어버림
        Log.d("setHtmlText", source);

        Spanned spanned = Html.fromHtml(source, this, null);    // Html.ImageGetter 를 여기다 구현해놨다.
        this.setText(spanned);

    }


    /**
     * Html.ImageGetter 구현.
     * @param source <img> 태그의 주소가 넘어온다.
     * @return 일단 LevelListDrawable 을 넘겨줘서 placeholder 처럼 보여주고, AsyncTask 를 이용해서 이미지를 다운로드
     */

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        // TODO: 주석이 원본
        Drawable empty = ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher);
        d.addLevel(0, 0, empty);
        //d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        d.setBounds(0, 0, 0, 0);


        new LoadImage().execute(source, d);

        return d;
    }


    /**
     * 실제 온라인에서 이미지를 다운로드 받을 AsyncTask
     */
    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];

            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 이미지 다운로드가 완료되면, 처음에 placeholder 처럼 만들어서 사용하던 Drawable 에, 다운로드 받은 이미지를 넣어준다.
         */

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(getContext().getResources(), bitmap);

                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);

                // 이미지 다운로드 완료 후, invalidate 의 개념으로, 다시한번 텍스트를 설정해준것이다. 더 좋은방법이 있을법도 하다
                CharSequence t = getText();
                setText(t);
            }
        }
    }
}