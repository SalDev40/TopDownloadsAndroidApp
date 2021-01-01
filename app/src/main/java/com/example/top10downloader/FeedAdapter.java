package com.example.top10downloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {

    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;


    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder view = null;

        //if we dont have a view to reuse, inflate a new view
        //optimizes so we dont create a new view each time (bad for RAM)
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            //make sure to findViewById since we are creating a new view
            view = new ViewHolder(convertView);
            convertView.setTag(view);
        } else {
            //make sure to not findViewById if are using previous view
            //which already has views references found and stored in it
            view = (ViewHolder) convertView.getTag();
        }

        FeedEntry currentApp = this.applications.get(position);
        view.tvName.setText(currentApp.getName());
        view.tvArtist.setText(currentApp.getArtist());
        view.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        public ViewHolder(View v) {
            this.tvName = v.findViewById(R.id.tv_name);
            this.tvArtist = v.findViewById(R.id.tv_artist);
            this.tvSummary = v.findViewById(R.id.tv_summary);

        }
    }
}
