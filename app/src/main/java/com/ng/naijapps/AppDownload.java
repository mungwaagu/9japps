package com.ng.naijapps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.ng.naijapps.apphome.AppsHome;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppDownload extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    // declare the dialog as a member field of your activity
    ProgressDialog mProgressDialog;

    String appShortName, appName, appDownloads, appVersion, appDeveloperEmail, appDeveloperImage;
    String appShortDescription, appDescription, appSize, appLogo, appImageOne, appApkUrl;
    String appImageTwo, appImageThree, appImageFour, appImageFive, appDeveloper, appPostedDate;

    ImageView topImage, appLogoImage;
    CircleImageView appDeveloperImageView;
    private SliderLayout imageSlider;
    TextView appShortNameText, appNameText, appDownloadsText, appVersionText, appDeveloperEmailText;
    TextView appShortDescriptionText, appDescriptionText, appSizeText, appDeveloperText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_download);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DownloadTask downloadTask = new DownloadTask(AppDownload.this);

        Bundle extras = getIntent().getExtras();

        appShortName = extras.getString("appShortName");
        appName = extras.getString("appName");
        appDownloads = extras.getString("appDownloads");
        appVersion = extras.getString("appVersion");
        appShortDescription = extras.getString("appShortDescription");
        appDescription = extras.getString("appDescription");
        appSize = extras.getString("appSize");
        appLogo = extras.getString("appLogo");
        appImageOne = extras.getString("appImageOne");
        appImageTwo = extras.getString("appImageTwo");
        appImageThree = extras.getString("appImageThree");
        appImageFour = extras.getString("appImageFour");
        appImageFive = extras.getString("appImageFive");
        appDeveloper = extras.getString("appDeveloper");
        appPostedDate = extras.getString("appPostedDate");
        appDeveloperEmail = extras.getString("appDeveloperEmail");
        appDeveloperImage = extras.getString("appDeveloperImage");
        appApkUrl = extras.getString("appApkUrl");

        imageSlider = (SliderLayout) findViewById(R.id.slider);

        HashMap<String, String> url_maps = new HashMap<String, String>();
        url_maps.put("", appImageOne);
        url_maps.put("", appImageTwo);
        url_maps.put("", appImageThree);
        url_maps.put("", appImageFour);
        url_maps.put("", appImageFive);

        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            imageSlider.addSlider(textSliderView);
        }
        imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new DescriptionAnimation());
        imageSlider.setDuration(10000);
        imageSlider.addOnPageChangeListener(this);

        topImage = (ImageView) findViewById(R.id.topImage);
        Glide.with(getApplicationContext()).load(appImageOne).into(topImage);

        appLogoImage = (ImageView) findViewById(R.id.appLogo);
        Glide.with(getApplicationContext()).load(appLogo).into(appLogoImage);

        appShortNameText = (TextView) findViewById(R.id.appShortName);
        appNameText = (TextView) findViewById(R.id.appName);
        appVersionText = (TextView) findViewById(R.id.appVersion);
        appShortDescriptionText = (TextView) findViewById(R.id.appShortDescription);
        appSizeText = (TextView) findViewById(R.id.appSize);

        appDeveloperImageView = (CircleImageView) findViewById(R.id.appDeveloperImage);
        Glide.with(getApplicationContext()).load(appDeveloperImage).into(appDeveloperImageView);

        appDeveloperEmailText = (TextView) findViewById(R.id.appDeveloperEmail);
        appDeveloperText = (TextView) findViewById(R.id.appDeveloperName);

        appShortNameText.setText(appShortName);
        appNameText.setText(appName);
        appVersionText.setText(appVersion);
        appSizeText.setText(appSize);
        appShortDescriptionText.setText(appShortDescription);
        appDeveloperText.setText(appDeveloper);
        appDeveloperEmailText.setText(appDeveloperEmail);

        String downloadname = "Downloading "+appShortName;
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(AppDownload.this);
        mProgressDialog.setMessage(downloadname);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadTask.cancel(true);
                Intent intent = new Intent(AppDownload.this, AppsHome.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // execute this when the downloader must be fired
                downloadTask.execute(appApkUrl);

            }
        });
    }

    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        imageSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/app-debug.apk");

                byte data[] = new byte[fileLength];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    /*if (isCancelled()) {
                        input.close();
                        return null;
                    }*/
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData( Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/app-debug.apk")) );
                startActivity(intent);
                finish();
            }
        }

    }

}
