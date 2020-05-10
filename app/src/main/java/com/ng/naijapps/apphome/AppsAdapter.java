package com.ng.naijapps.apphome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ng.naijapps.AppDownload;
import com.ng.naijapps.R;

import java.util.List;

/**
 * Created by mungwaagu on 01/09/2018.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private Context context;
    private List<AppsClass> appsList;

    public AppsAdapter(Context context, List<AppsClass> appsList) {
        this.context = context;
        this.appsList = appsList;
    }


    @Override
    public AppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppsAdapter.ViewHolder holder, final int position) {

        holder.appShortName.setText(appsList.get(position).getAppShortName());
        holder.appSize.setText(appsList.get(position).getAppSize());
        Glide.with(context).load(appsList.get(position).getAppLogo()).into(holder.appLogo);

        holder.appLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AppDownload.class);
                intent.putExtra("appId", appsList.get(position).getAppId());
                intent.putExtra("appShortName", appsList.get(position).getAppShortName());
                intent.putExtra("appName", appsList.get(position).getAppName());
                intent.putExtra("appDownloads", appsList.get(position).getAppDownloads());
                intent.putExtra("appVersion", appsList.get(position).getAppVersion());
                intent.putExtra("appShortDescription", appsList.get(position).getAppShortDescription());
                intent.putExtra("appDescription", appsList.get(position).getAppDescription());
                intent.putExtra("appSize", appsList.get(position).getAppSize());
                intent.putExtra("appLogo", appsList.get(position).getAppLogo());
                intent.putExtra("appImageOne", appsList.get(position).getAppImageOne());
                intent.putExtra("appImageTwo", appsList.get(position).getAppImageTwo());
                intent.putExtra("appImageThree", appsList.get(position).getAppImageThree());
                intent.putExtra("appImageFour", appsList.get(position).getAppImageFour());
                intent.putExtra("appImageFive", appsList.get(position).getAppImageFive());
                intent.putExtra("appDeveloper", appsList.get(position).getAppDeveloper());
                intent.putExtra("appPostedDate", appsList.get(position).getAppPostedDate());
                intent.putExtra("appDeveloperEmail", appsList.get(position).getAppDeveloperEmail());
                intent.putExtra("appDeveloperImage", appsList.get(position).getAppDeveloperImage());
                intent.putExtra("appApkUrl", appsList.get(position).getAppApkUrl());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        public TextView appShortName;
        public TextView appSize;
        public ImageView appLogo;

        public RelativeLayout appLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            appShortName = (TextView) itemView.findViewById(R.id.appShortName);
            appSize = (TextView) itemView.findViewById(R.id.appSize);
            appLogo = (ImageView) itemView.findViewById(R.id.appLogo);

            appLayout = (RelativeLayout) itemView.findViewById(R.id.appLayout);

        }
    }
}
