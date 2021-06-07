package com.example.test3;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AmplifyApi {

    static ArrayList<String> newSet;
    static ArrayList<String> newRealtimeSet;
    static ArrayList<String> newInterSet;
    static MainActivity mainActivity;
    static ArrayList<String> gage;
    static ArrayList<String> gagenumber;
    // User 정보 가져오기
    //User attributes = [AuthUserAttribute {key=AuthUserAttributeKey {attributeKey=sub}, value=47fe45b6-6513-4636-9d1e-8ff09db7549c},
    //                   AuthUserAttribute {key=AuthUserAttributeKey {attributeKey=birthdate}, value=1111/11/11},
    //                   AuthUserAttribute {key=AuthUserAttributeKey {attributeKey=gender}, value=111},
    //                   AuthUserAttribute {key=AuthUserAttributeKey {attributeKey=nickname}, value=허자},
    //                   AuthUserAttribute {key=AuthUserAttributeKey {attributeKey=email}, value=dnjfxjs0812@gmail.com}]
    public static void userAttr() {
        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    Log.i("AuthDemo", "User attributes = " + attributes.toString());

                    Log.d("Get Testing", String.valueOf(attributes.get(0).getKey()));
                    Log.d("Get Testing", attributes.get(0).getValue());
                    MainActivity.userId = attributes.get(0).getValue();
                    MainActivity.userName = attributes.get(3).getValue();
                    MainActivity.loadComplete = true;
                },

                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)
        );
    }

    // 로그아웃
    public static void userLogout() {
        Amplify.Auth.signOut(
                AuthSignOutOptions.builder().globalSignOut(true).build(),
                () -> Log.i("AuthQuickstart", "Signed out globally"),
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }

    // 닉네임 변경 (로그인 이후에 사용 가능)
    public static void setUserNick(String nickname) {
        AuthUserAttribute userNickname = new AuthUserAttribute(AuthUserAttributeKey.custom("nickname"), nickname);
        Amplify.Auth.updateUserAttribute(
                userNickname, result -> {
                    Log.i("AuthDemo", "Updated user attribute = " + result.toString());
                    MainActivity.userName = nickname;
                    MainActivity.modifyComplete = true;
                    },
                error -> Log.e("AuthDemo", "Failed to update user attribute.", error)
        );
    }



    // 음식 히스토리 가져오기
    public static void InteractionGet(String user_id){
        //user_id: userAttr의 sub사용
        RestOptions options = RestOptions.builder()
                .addPath("interaction")
                .addHeader("user_id",user_id)
                .build();
        Amplify.API.get("bab2",options,respond->{
                    try {
                        Log.d("interaction save","interaction:"+respond+"");
                        Log.d("interaction save","interaction:"+respond.getData().asJSONObject()+"");
                        String aa = respond.getData().asJSONObject().toString();
                        System.out.println(aa);
                        newInterSet = new ArrayList<>();
                        int start = 0; int end = 0;
                        for(int i = 0; i<aa.length()-2;i++){
                            if(aa.charAt(i)=='i'&&aa.charAt(i+1)=='t'&&aa.charAt(i+2)=='e'&&aa.charAt(i+3)=='m'){
                                start = i+10;
                            }
                            if(aa.charAt(i)=='d'&&aa.charAt(i+1)=='a'&&aa.charAt(i+2)=='t'&&aa.charAt(i+3)=='e'){
                                end = i - 3;
                            }
                            if(start!=0&&end!=0){
                                newInterSet.add(aa.substring(start,end));
                                start = 0; end = 0;
                            }
                        }
                        System.out.println(newInterSet.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("interaction save", "interaction failed.", error)
        );
    }
    
    // 먹은 음식 알리기
    public static void InteractionPost(String item_id, String user_id){
        // item_id: 선택한 음식의 이름
        // user_id: 저장하는데 사용하는 인자, sub 사용
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("item_id",item_id);
        jsonObject.addProperty("user_id",user_id);
        RestOptions options = RestOptions.builder()
                .addPath("interaction")
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.post("bab2",options,respond->{
                    try {
                        Log.d("interaction save","interaction:"+respond+"");
                        Log.d("interaction save","interaction:"+respond.getData().asJSONObject()+"");

                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("interaction save", "interaction failed.", error)
        );
    }



    // 실시간 인기음식 받아오기
    public static void RealTimeBestGet(){
        // 오름차순으로 반환
        RestOptions options = RestOptions.builder()
                .addPath("real-time-best")
                .build();
        Amplify.API.get("bab2",options,respond->{
                    try {
                        Log.d("RealTimeBestGet","RealTimeBestGet:"+respond+"");
                        JSONArray array=new JSONArray(respond.getData().asString());
                        Log.d("RealTimeBestGet","RealTimeBestGet:"+array.toString()+"");
                        String aa = array.toString();
                        System.out.println(aa);
                        newRealtimeSet = new ArrayList<>();
                        int start = 0; int end = 0;
                        for(int i = 0; i<aa.length()-2;i++){
                            if(aa.charAt(i)==':'&&aa.charAt(i+1)=='"'){
                                start = i+2;
                            }
                            if(aa.charAt(i)=='"'&&aa.charAt(i+1)==','){
                                end = i;
                            }
                            if(start!=0&&end!=0){
                                newRealtimeSet.add(aa.substring(start,end));
                                start = 0; end = 0;
                            }
                        }
                        System.out.println(newRealtimeSet.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("RealTimeBestGet", "RealTimeBestGet failed.", error)
        );
    }


    // 이후 시간되면 필터에 사용할수 있을것같아서 추가한것
    // 이 함수를 호출해야 실시간 순위에 반영이 됨
    public static void RealTimeBestPost(String item_id, String birthdate, String gender){
        // item_id: 선택한 음식의 이름
        // birthdate, gender은 null가능
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("item_id",item_id);
        if(birthdate!=null) {
            jsonObject.addProperty("birthdate", birthdate);
        }

        if(gender!=null) {
            jsonObject.addProperty("gender", gender);
        }

        RestOptions options = RestOptions.builder()
                .addPath("real-time-best")
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.post("bab2",options,respond->{
                    try {
                        Log.d("RealTimeBestPost","RealTimeBestPost:"+respond+"");
                        Log.d("RealTimeBestPost","RealTimeBestPost:"+respond.getData().asJSONObject()+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("RealTimeBestPost", "RealTimeBestPost failed.", error)
        );
    }



    // 일반 게시판 목록 가져오기
    public static void Get(BoardRecyclerAdapter adapter, Activity activity, int pageNum, String name, String title, boolean isAdd){
        // name: 작성자로 검색할 때 사용할 인자
        // title: 제목으로 검색할 때 사용할 인자
        HashMap<String,String> queryMap = new HashMap<>();
        queryMap.put("pageNum",String.valueOf(pageNum));
        //queryMap.put("name","d");
        //queryMap.put("content","gggggggggggg");

        RestOptions.Builder optionsBuilder = RestOptions.builder();
        optionsBuilder = optionsBuilder.addPath("board");
        optionsBuilder = optionsBuilder.addQueryParameters(queryMap);


        HashMap<String,String> nameMap = new HashMap<>();
        if(name != null) {
            nameMap.put("name", name);
            optionsBuilder = optionsBuilder.addQueryParameters(nameMap);
        }

        HashMap<String,String> titleMap = new HashMap<>();
        if(title != null) {
            titleMap.put("title", title);
            optionsBuilder = optionsBuilder.addQueryParameters(titleMap);
        }

        RestOptions options = optionsBuilder.build();

        Log.d("Community","Get:" + name + title);

        Amplify.API.get("bab2", options, respond->{
            try {
                Log.d("Community","Get:" + respond.getData().asString());
                JSONObject result = new JSONObject(respond.getData().asString());

                // 최대 페이지
                Log.d("maxPage", "Get:" + (int) Math.ceil(Double.parseDouble((String) result.get("pagenum"))));
                MainActivity.maxPage = (int) Math.ceil(Double.parseDouble((String) result.get("pagenum"))) - 1;

                Log.d("Community","Get:" + result.get("boardlist"));
                // result = new JSONArray(respond.getData().asString());
                ArrayList<Data> dataArrayList = DataUtil.JsonArrayToDataArray((JSONArray) result.get("boardlist"));

                activity.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        Log.d("Community","Get:" + dataArrayList.toString()+"");

                        if (isAdd) {
                            adapter.addPage(dataArrayList);
                        } else {
                            adapter.setValue(dataArrayList);
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("Community", "Get failed.", error));
    }
    
    
    //특정 id의 게시글만 가져오기
    public static void Get(ReadFragment readFragment, Activity activity, int id){
        RestOptions options = RestOptions.builder()
                .addPath("/board/"+id)

                .build();

        Amplify.API.get("bab2",options, respond->{
            try {
                Log.d("Community","Get:"+respond.getData().asString()+"");

                JSONObject result = new JSONObject(respond.getData().asString());
                Data data = new Data();

                activity.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        Log.d("Community","Get:" + data.toString() + "");

                        try {
                            data.setId(result.getInt("_id"));
                            data.setPassword(result.getString("password"));
                            data.setName(result.getString("name"));
                            data.setTitle(result.getString("title"));
                            data.setContent(result.getString("content"));
                            data.setDate(result.getString("date"));
                            data.setComment(DataUtil.JsonArrayToDataCommentArray(result.getJSONArray("comment")));
                            data.setLike(DataUtil.JsonArrayToDataLikeArray(result.getJSONArray("like")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        readFragment.setValue(data);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("Community", "Get failed.", error));
    }


    // 게시글 Post
    public static void Post(String password, String content, String title, String name){
        // password: 글 쓰는데 사용하는 인자, sub 사용
        // content: 글 내용
        // title: 글 제목
        // name: 글 쓴 사람의 닉네임
        JsonObject jsonObject=new JsonObject();
        //jsonObject.addProperty("id",id);
        jsonObject.addProperty("password",password);
        jsonObject.addProperty("name",name);
        jsonObject.addProperty("title",title);
        jsonObject.addProperty("content",content);
        RestOptions options = RestOptions.builder()
                .addPath("board")
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.post("bab2",options,respond->{
                    Log.d("Community","POST:"+respond.toString());
                    MainActivity.modifyComplete = true;
                }, error -> Log.e("Community", "POST failed.", error)
        );
    }


    // 게시글 Put(수정)
    public static void Put(String id, String password, String content, String title,String name) {
        // id: 수정할 게시글 번호
        // password: 바꾸는데 사용하는 인자, sub 사용
        // content: 수정할 내용
        // title: 수정할 제목
        // name : 수정할 닉네임
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("password",password);
        jsonObject.addProperty("title",title);
        jsonObject.addProperty("content",content);
        jsonObject.addProperty("name",name);

        RestOptions options = RestOptions.builder()
                .addPath("/board/"+id)
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.put("bab2",options,respond->{
                    Log.d("Community","Put:"+respond.getData().asString());//.getData().asJSONObject()+"");
                    MainActivity.modifyComplete = true;
                }, error -> Log.e("Community", "Put failed.", error)
        );
    }


    // 게시글 Delete
    public static void Delete(String id, String password){
        // id: 지울 게시글 번호
        // password: 지우는데 사용하는 인자, sub 사용
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("password", password);
        RestOptions options = RestOptions.builder()
                .addPath("/board/"+ id)
                .addHeader("password", password)
                .build();
        Amplify.API.delete("bab2", options, respond->{
                    try {
                        Log.d("Community","Delete:" + respond.getData().asJSONObject() + "");
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Community", "Delete failed.", error)
        );
    }


    // 댓글 Post
    public static void CommentPost(int boardId, String password, String content, String name){
        // boardId: 현재 게시글 번호
        // password: 지우는데 사용하는 인자, sub 사용
        // content: 댓글 내용
        // name: 닉네임
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("content", content);
        RestOptions options = RestOptions.builder()
                .addPath("/comment/" + boardId)
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.post("bab2", options, respond->{
                    try {
                        Log.d("comment","POST:" + respond.getData().asJSONObject().toString());
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("comment", "POST failed.", error)
        );
    }

    
    // 댓글 Delete
    public static void CommentDelete(String id, String commentId, String commentPassword){
        // id: 현재 게시글 번호
        // commentId: 현재 댓글의 번호
        // commentPassword: 지우는데 사용하는 인자, sub 사용
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id",id);
        jsonObject.addProperty("commentId",commentId);

        jsonObject.addProperty("commentPassword",commentPassword);
        RestOptions options = RestOptions.builder()
                .addPath("/comment/"+id)
                .addHeader("commentId",commentId)
                .addHeader("commentPassword",commentPassword)
                .build();
        Amplify.API.delete("bab2",options,respond->{
                    try {
                        Log.d("Community","Delete:"+respond.getData().asJSONObject()+"");
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Community", "Delete failed.", error)
        );
    }



    // user_id: sub
    public static void PersonalizeGet(MainRecyclerAdapter adapter, Activity activity, String userId){
        RestOptions options = RestOptions.builder()
                .addPath("personalize-test")
                .addHeader("userId",userId)
                .build();

        mainActivity.popup2();
        Amplify.API.get("bab2",options, respond->{
                    Log.d("PersonalizeGet","Get:"+respond.getData().asString()+"");
                    try {
                        JSONArray jsonArray = respond.getData().asJSONObject().getJSONArray("itemList");

                        newSet = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {

                            newSet.add((String) jsonArray.getJSONObject(i).get("itemId"));

                            Log.d("PersonalizeGet", (String) jsonArray.getJSONObject(i).get("itemId"));
                        }
                        ((MainActivity) activity).Excel4(newSet, 1);
                        if(adapter.isSameList(newSet)) {
                            Log.d("AmplifyApi","sleep beacasuse smae");
                            Thread.sleep(500);
                            PersonalizeGet(adapter,activity,userId);
                        }else {
                            mainActivity.progressDialog.dismiss();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setValue(newSet);
                                }
                            });
                        }

                    }catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                , error -> Log.e("PersonalizeGet", "Get failed.", error));
    }


    // userId: sub
    public static void PersonalizePOST(String itemId, String userId){

        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("userId",userId);
        jsonObject.addProperty("itemId",itemId);
        RestOptions options = RestOptions.builder()
                .addPath("personalize-test")
                .addBody(jsonObject.toString().getBytes())
                .build();
        Log.d("PersonalizePOST","clicked itemId:"+itemId+"");



        Amplify.API.post("bab2",options,
                response ->{
                    Log.i("PersonalizePOST", "POST succeeded: " + response.getData().asString());
                    MainActivity.modifyComplete = true;
                },
                error -> Log.e("PersonalizePOST", "POST failed.", error)
        );
    }



    // 추천 게시판 목록 가져오기
    public static void RecommendBoardGet(RecoboardRecyclerAdapter adapter, Activity activity, int pageNum, String name, String title, String tag, boolean isAdd){
        // name: 작성자로 검색할 때 사용할 인자
        // title: 제목으로 검색할 때 사용할 인자
        // tag: 태그로 검색할 때 인자
        HashMap<String,String> queryMap = new HashMap<>();
        queryMap.put("pageNum",String.valueOf(pageNum));
        //queryMap.put("name","d");
        //queryMap.put("content","gggggggggggg");

        RestOptions.Builder optionsBuilder = RestOptions.builder();
        optionsBuilder = optionsBuilder.addPath("recommend-board");
        optionsBuilder = optionsBuilder.addQueryParameters(queryMap);


        HashMap<String,String> nameMap = new HashMap<>();
        if(name != null) {
            nameMap.put("name", name);
            optionsBuilder = optionsBuilder.addQueryParameters(nameMap);
        }

        HashMap<String,String> titleMap = new HashMap<>();
        if(title != null) {
            titleMap.put("title", title);
            optionsBuilder = optionsBuilder.addQueryParameters(titleMap);
        }

        HashMap<String,String> tagMap = new HashMap<>();
        if(tag != null) {
            tagMap.put("tag", tag);
            optionsBuilder = optionsBuilder.addQueryParameters(tagMap);
        }
        RestOptions options = optionsBuilder.build();

        Log.d("recommend-board","Get:" + name + title);

        Amplify.API.get("bab2", options, respond->{
            try {
                Log.d("recommend-board","Get:" + respond.getData().asString());
                JSONObject result = new JSONObject(respond.getData().asString());

                // 최대 페이지
                Log.d("recommend-maxPage", "Get:" + (int) Math.ceil(Double.parseDouble((String) result.get("pagenum"))));
                MainActivity.reco_maxPage = (int) Math.ceil(Double.parseDouble((String) result.get("pagenum"))) - 1;

                Log.d("recommend-board","Get:" + result.get("boardlist"));
                // result = new JSONArray(respond.getData().asString());
                ArrayList<Data> dataArrayList = DataUtil.JsonArrayToRecoDataArray((JSONArray) result.get("boardlist"));

                activity.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        Log.d("recommend-board","Get:" + dataArrayList.toString()+"");

                        if (isAdd) {
                            adapter.addPage(dataArrayList);
                        } else {
                            adapter.setValue(dataArrayList);
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("recommend-board", "Get failed.", error));
    }


    // 추천 게시판의 이미지 가져오기
    public static void RecommendBoardGet(FoodViewPagerAdapter foodviewPagerAdapter, Activity activity, int pageNum, String tag){
        // tag: 태그로 검색할 때 인자
        HashMap<String,String> queryMap = new HashMap<>();
        queryMap.put("pageNum",String.valueOf(pageNum));

        RestOptions.Builder optionsBuilder = RestOptions.builder();
        optionsBuilder = optionsBuilder.addPath("recommend-board");
        optionsBuilder = optionsBuilder.addQueryParameters(queryMap);

        HashMap<String,String> tagMap = new HashMap<>();
        if(tag != null) {
            tagMap.put("tag", tag);
            optionsBuilder = optionsBuilder.addQueryParameters(tagMap);
        }
        RestOptions options = optionsBuilder.build();

        Log.d("recommend-board","Get:" + tag);

        Amplify.API.get("bab2", options, respond->{
            try {
                Log.d("recommend-board","Get:" + respond.getData().asString());
                JSONObject result = new JSONObject(respond.getData().asString());

                Log.d("recommend-board","Get:" + result.get("boardlist"));
                // result = new JSONArray(respond.getData().asString());

                ArrayList<Data> dataArrayList = DataUtil.JsonArrayToRecoDataArray((JSONArray) result.get("boardlist"));

                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        foodviewPagerAdapter.setValue(dataArrayList);
                    }
                });

                //MainActivity.modifyComplete = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("recommend-board", "Get failed.", error));
    }


    // 특정 id의 추천 게시글만 가져오기
    public static void RecommendBoardGet(RecoReadFragment recoReadFragment, Activity activity, int id){
        RestOptions options = RestOptions.builder()
                .addPath("/recommend-board/"+id)

                .build();

        Amplify.API.get("bab2",options, respond->{
            try {
                Log.d("recommend-board-one","Get:"+respond.getData().asString()+"");

                JSONObject result = new JSONObject(respond.getData().asString());
                Data data = new Data();

                activity.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        Log.d("recommend-board-one","Get:" + data.toString() + "");

                        try {
                            //todo 태그랑 이미지 url 매칭
                            data.setId(result.getInt("_id"));
                            data.setPassword(result.getString("password"));
                            data.setName(result.getString("name"));
                            data.setTitle(result.getString("title"));
                            data.setContent(result.getString("content"));
                            data.setTag(result.getString("tag"));
                            data.setDate(result.getString("date"));
                            data.setComment(DataUtil.JsonArrayToDataCommentArray(result.getJSONArray("comment")));
                            data.setLike(DataUtil.JsonArrayToDataLikeArray(result.getJSONArray("like")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        recoReadFragment.setValue(data);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("recommend-board", "Get failed.", error));
    }


    // 추천 게시글 Post
    public static void RecommendBoardPost(String password, String content, String title, String name, String tag, String imageURL){
        // password: 글 쓰는데 사용하는 인자, sub 사용
        // content: 글 내용
        // title: 글 제목
        // name: 글 쓴 사람의 닉네임
        // tag: 태그
        // imageURL: 첫번째 이미지의 url
        JsonObject jsonObject=new JsonObject();
        //jsonObject.addProperty("id",id);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("imag", imageURL);
        jsonObject.addProperty("tag", tag);
        RestOptions options = RestOptions.builder()
                .addPath("recommend-board")
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.post("bab2",options,respond->{
                    Log.d("recommend-board","POST:"+respond.toString());
                    MainActivity.modifyComplete = true;
                }, error -> Log.e("recommend-board", "POST failed.", error)
        );
    }


    // 추천 게시글 Put(수정)
    public static void RecommendBoardPut(String id, String password, String content, String title, String name, String tag, String imageURL) {
        // id: 수정할 게시글 번호
        // password: 바꾸는데 사용하는 인자, sub 사용
        // content: 수정할 내용
        // title: 수정할 제목
        // name : 수정할 닉네임
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("imag", imageURL);
        jsonObject.addProperty("tag", tag);

        RestOptions options = RestOptions.builder()
                .addPath("/recommend-board/"+id)
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.put("bab2",options,respond->{
                    Log.d("recommend-board","Put:"+respond.getData().asString());//.getData().asJSONObject()+"");
                    MainActivity.modifyComplete = true;
                }, error -> Log.e("recommend-board", "Put failed.", error)
        );
    }


    // 추천 게시글 Delete
    public static void RecommendBoardDelete(String id, String password){
        // id: 지울 게시글 번호
        // password: 지우는데 사용하는 인자, sub 사용
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("password", password);
        RestOptions options = RestOptions.builder()
                .addPath("/recommend-board/"+ id)
                .addHeader("password", password)
                .build();
        Amplify.API.delete("bab2", options, respond->{
                    try {
                        Log.d("recommend-board","Delete:" + respond.getData().asJSONObject() + "");
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("recommend-board", "Delete failed.", error)
        );
    }


    // 추천 댓글 Post
    public static void RecommendBoardCommentPost(int boardId, String password, String content, String name){
        // boardId: 현재 게시글 번호
        // password: 지우는데 사용하는 인자, sub 사용
        // content: 댓글 내용
        // name: 닉네임
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("content", content);
        RestOptions options = RestOptions.builder()
                .addPath("/recommend-board-comment/" + boardId)
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.post("bab2", options, respond->{
                    try {
                        Log.d("comment","POST:" + respond.getData().asJSONObject().toString());
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("comment", "POST failed.", error)
        );
    }


    // 추천 댓글 Delete
    public static void RecommendBoardCommentDelete(String id, String commentId, String commentPassword){
        // id: 현재 게시글 번호
        // commentId: 현재 댓글의 번호
        // commentPassword: 지우는데 사용하는 인자, sub 사용
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id",id);
        jsonObject.addProperty("commentId",commentId);

        jsonObject.addProperty("commentPassword",commentPassword);
        RestOptions options = RestOptions.builder()
                .addPath("/recommend-board-comment/"+id)
                .addHeader("commentId",commentId)
                .addHeader("commentPassword",commentPassword)
                .build();
        Amplify.API.delete("bab2",options,respond->{
                    try {
                        Log.d("Community","Delete:"+respond.getData().asJSONObject()+"");
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Community", "Delete failed.", error)
        );
    }
    // 광고 음식점
    //address 구
    //menu_name 메뉴이름
    Amplify.API.get("bab2",options,respond->{
                    try {
                        JSONArray array=new JSONArray(respond.getData().asString());
                        Log.d("marketGet","marketGet:"+array.toString()+"");

                        String aa = array.toString();
                        System.out.println(aa);
                        gage = new ArrayList<>();
                        int start = 0; int end = 0;
                        for(int i = 0; i<aa.length()-2;i++){
                            if(aa.charAt(i)==':'&&aa.charAt(i-2)=='e'&&aa.charAt(i-3)=='m'){
                                start = i+2;
                            }
                            if(aa.charAt(i)=='"'&&aa.charAt(i+1)==','){
                                end = i;
                            }
                            if(end<start){
                                end = 0;
                            }
                            if(start!=0&&end!=0){
                                gage.add(aa.substring(start,end));
                                System.out.println(start + "위치 " + end);
                                start = 0; end = 0;
                            }
                        }
                        start = 0; end = 0;
                        gagenumber = new ArrayList<>();
                        for(int i = 0; i<aa.length()-2;i++){
                            if(aa.charAt(i)==':'&&aa.charAt(i-2)=='e'&&aa.charAt(i-3)=='n'){
                                start = i+2;
                            }
                            if(aa.charAt(i)=='"'&&aa.charAt(i+1)==','){
                                end = i;
                            }
                            if(end<start){
                                end = 0;
                            }
                            if(start!=0&&end!=0){
                                gagenumber.add(aa.substring(start,end));
                                System.out.println(start + "위치 " + end);
                                start = 0; end = 0;
                            }
                        }
                        String b;
                        if(gage.size()!=0) {
                            b = "근처 가게 이름 : ";
                            b += gage.get(0);
                            if(gagenumber.size()!=0){
                                b += "   \n전화 번호 : ";
                                b += gagenumber.get(0);
                            }
                            MainFragment.addressText.setText(b);
                        }
                        else if(gage.size()==0){
                            MainFragment.addressText.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("interaction save", "interaction failed.", error)
        );
    }


    public static void BoardLikePost(int boardId, String user_id){
        // boardId: 현재 게시글 번호
        // user_id: 좋아요누가 하는지
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", user_id);
        RestOptions options = RestOptions.builder()
                .addPath("/board-like/" + boardId)
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.post("bab2", options, respond->{
                    try {
                        Log.d("BoardLikePost","POST:" + respond.getData().asJSONObject().toString());
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("BoardLikePost", "POST failed.", error)
        );
    }


    public static void BoardLikeDelete(String id, String likeid){
        // id: 현재 게시글 번호
        // likeid: 좋아요 번호
        RestOptions options = RestOptions.builder()
                .addPath("/board-like/"+id)
                .addHeader("likeid",likeid)
                .build();
        Amplify.API.delete("bab2",options,respond->{
                    try {
                        Log.d("BoardLikePost","Delete:"+respond.getData().asJSONObject()+"");
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("BoardLikePost", "Delete failed.", error)
        );
    }


    // 추천 댓글 Post
    public static void RecommendBoardLikePost(int boardId, String user_id){
        // boardId: 현재 게시글 번호
        // user_id: 지우는데 사용하는 인자, sub 사용
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", user_id);
        RestOptions options = RestOptions.builder()
                .addPath("/recommend-board-like/" + boardId)
                .addBody(jsonObject.toString().getBytes())
                .build();
        Amplify.API.post("bab2", options, respond->{
                    try {
                        Log.d("RecommendBoardLikePost","POST:" + respond.getData().asJSONObject().toString());
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("RecommendBoardLikePost", "POST failed.", error)
        );
    }

    public static void RecommendBoardLikeDelete(String id, String likeid){
        // id: 현재 게시글 번호
        // commentId: 현재 댓글의 번호

        RestOptions options = RestOptions.builder()
                .addPath("/recommend-board-like/"+id)
                .addHeader("likeid",likeid)
                .build();
        Amplify.API.delete("bab2",options,respond->{
                    try {
                        Log.d("RecommendBoardLikeDelet","Delete:"+respond.getData().asJSONObject()+"");
                        MainActivity.modifyComplete = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("RecommendBoardLikeDelet", "Delete failed.", error)
        );
    }
}