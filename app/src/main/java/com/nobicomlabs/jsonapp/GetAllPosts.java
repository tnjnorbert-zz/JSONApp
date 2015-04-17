package com.nobicomlabs.jsonapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GetAllPosts extends ListActivity {

    ProgressDialog progressDialog;

    ArrayList<HashMap<String,String>> all_posts;

    private static final String all_post_url="http://192.168.43.60/Profile/api";
    private static final String TAG_POSTS="all_posts";
    private static final String TAG_TITLE="post_title";
    private static final String TAG_POST_MESSAGE="post";
    private static final String TAD_PID="post_id";

    JSONArray posts=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_all_posts);

        all_posts=new ArrayList<HashMap<String,String>>();


        ListView lv=getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ptitle=((TextView)view.findViewById(R.id.post_title)).toString();
                String pMessage=((TextView)view.findViewById(R.id.post_message)).toString();

                Intent intent=new Intent(getApplicationContext(),singlePost.class);
                intent.putExtra(TAG_TITLE,ptitle);
                intent.putExtra(TAG_POST_MESSAGE,pMessage);
                startActivity(intent);
            }
        });
        new GetPosts().execute();
    }
    public class GetPosts extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(GetAllPosts.this);
            progressDialog.setMessage("Getting posts.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... args) {

            JSONParser jsonParser = new JSONParser();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // JSONObject jsonStr = jsonParser.getJSONFromUrl(all_post_url);
            JSONObject jsonStr = jsonParser.makeHttpRequest(all_post_url,"GET",params);
            Log.d("Response:", ">" + jsonStr.toString());

            if (jsonStr != null) {
                try {
                    // JSONObject jsonObject=new JSONObject((jsonStr);
                    posts = jsonStr.getJSONArray(TAG_POSTS);

                    for (int i=0;i<=posts.length();i++){
                        JSONObject c=posts.getJSONObject(i);
                        String post_title=c.getString(TAG_TITLE);
                        String post_message=c.getString(TAG_POST_MESSAGE);


                        HashMap<String,String> our_posts=new HashMap<String,String>();
                        our_posts.put(TAG_TITLE,post_title);
                        our_posts.put(TAG_POST_MESSAGE,post_message);


                        all_posts.add(our_posts);
                    }
                } catch (JSONException jsone) {
                    jsone.printStackTrace();

                }

            }else{
                Log.e("JSONParser", "Couldn't get any data from the url");
                Intent it=new Intent(getApplicationContext(),MainActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void vv) {
            super.onPostExecute(vv);
            if(progressDialog.isShowing())
            progressDialog.dismiss();

            ListAdapter adapter=new SimpleAdapter(GetAllPosts.this,all_posts,R.layout.list_item,new String[]{TAG_TITLE,TAG_POST_MESSAGE},new int[]{R.id.post_title,R.id.post_message});
            setListAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_all_posts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
