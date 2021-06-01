package com.example.test3;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataUtil {

    //{"_id":26,"title":"안녕하세요","name":"허윤석","content":"<p data-tag=\"input\" style=\"color:#000000;\"><u>안녕히계세요</u></p>","comment":[],"date":"2021-05-28T09:46:57.608Z","__v":0}
    public static ArrayList<Data> JsonArrayToDataArray(JSONArray jsonArray) {

        ArrayList<Data> dataArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            Data data = new Data();
            JSONObject object = null;
            try {
                object = (JSONObject) jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                data.setId(object.getInt("_id"));
                data.setName(object.getString("name"));
                data.setTitle(object.getString("title"));
                data.setDate(object.getString("date"));
                data.setComment(JsonArrayToDataCommentArray(object.getJSONArray("comment")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataArrayList.add(data);
        }

        return dataArrayList;
    }


    public static ArrayList<Data> JsonArrayToRecoDataArray(JSONArray jsonArray) {

        ArrayList<Data> dataArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            Data data = new Data();
            JSONObject object = null;
            try {
                object = (JSONObject) jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                data.setId(object.getInt("_id"));
                data.setName(object.getString("name"));
                data.setTitle(object.getString("title"));
                data.setDate(object.getString("date"));
                data.setTag(object.getString("tag"));
                data.setImage(object.getString("imag"));
                data.setComment(JsonArrayToDataCommentArray(object.getJSONArray("comment")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataArrayList.add(data);
        }

        return dataArrayList;

    }


    // "comment":[{"_id":"60b0c7fa1332dd0008a35fdb","name":"허윤석","content":"확인중","password":"1234","date":"2021-05-28T10:37:46.186Z"},
    //            {"_id":"60b0c8121332dd0008a35fdc","name":"허윤석","content":"확인중","password":"1234","date":"2021-05-28T10:38:10.584Z"}]
    public static ArrayList<DataComment> JsonArrayToDataCommentArray(JSONArray jsonArray) {

        ArrayList<DataComment> dataArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            DataComment data = new DataComment();
            JSONObject object = null;
            try {
                object = (JSONObject) jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                data.setId(object.getString("_id"));
                data.setName(object.getString("name"));
                data.setContent(object.getString("content"));
                data.setPassword(object.getString("password"));
                data.setDate(object.getString("date"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataArrayList.add(data);
        }

        return dataArrayList;
    }

}
