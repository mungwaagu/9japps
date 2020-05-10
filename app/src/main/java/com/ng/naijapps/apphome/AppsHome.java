package com.ng.naijapps.apphome;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.ng.naijapps.R;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppsHome extends AppCompatActivity {

    private List<AppsClass> appsList;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;
    private AppsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_home);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        appsList = new ArrayList<>();
        getAppsList();

        gridLayout = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayout);

        adapter = new AppsAdapter(this, appsList);
        recyclerView.setAdapter(adapter);


    }

    private void getAppsList() {

        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... matchIds) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://naijapps.developervisiongroup.com/fetch_apps_script.php")
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        AppsClass apps = new AppsClass(object.getInt("app_id"), object.getString("app_short_name"),
                                object.getString("app_name"), object.getString("app_downloads"),
                                object.getString("app_version"), object.getString("app_short_description"),
                                object.getString("app_description"), object.getString("app_size"),
                                object.getString("app_logo"), object.getString("app_image_one"),
                                object.getString("app_image_two"), object.getString("app_image_three"),
                                object.getString("app_image_four"), object.getString("app_image_five"),
                                object.getString("app_developer"), object.getString("app_posted_date"),
                                object.getString("app_developer_email"), object.getString("app_developer_image"),
                                object.getString("app_apk_url"));

                        AppsHome.this.appsList.add(apps);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
                if(appsList.size() == 0){

                    new LovelyStandardDialog(AppsHome.this, R.style.EditTextTintTheme)
                            .setTopColorRes(R.color.colorPrimary)
                            .setTitle("Unable to Connect")
                            .setMessage("Check Your Internet Connection")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(true)
                            .show();

                }
            }
        };

        asyncTask.execute();
    }


}
