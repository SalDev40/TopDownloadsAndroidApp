package com.example.top10downloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    private ListView appList = null;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;

    private String feedCachedUrl = "None";
    public static final String STATE_FEED_URL = "feedUrl";
    public static final String STATE_FEED_LIMIT = "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //when screen rotates, it causes screen to be recreated
        //restore the feedUrl and feedLimit so its not lost
        if (savedInstanceState != null) {
            this.feedUrl = savedInstanceState.getString(STATE_FEED_URL);
            this.feedLimit = savedInstanceState.getInt(STATE_FEED_LIMIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.appList = findViewById(R.id.xml_list_view);
        Log.d(TAG, "Feed Url: " + String.format(this.feedUrl, this.feedLimit));
        downloadUrl(String.format(this.feedUrl, this.feedLimit));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.feeds_menu, menu);

        //restore the proper checked button
        if (feedLimit == 10) {
            menu.findItem(R.id.menu_top_10).setChecked(true);
        } else {
            menu.findItem(R.id.menu_top_25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_free:
                this.feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.menu_paid:
                this.feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.menu_songs:
                this.feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.menu_top_10:
            case R.id.menu_top_25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    this.feedLimit = 35 - this.feedLimit;
                }
                break;
            case R.id.menu_refresh:
                feedCachedUrl = "None";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        String selectedUrl = String.format(this.feedUrl, this.feedLimit);

        Log.d(TAG, "Selected feed Url: " + selectedUrl);
        //optimize so we dont re download the same URL code
        if (!selectedUrl.equalsIgnoreCase(feedCachedUrl)) {
            Log.d(TAG, "New Feed Cached Url: " + String.format(this.feedUrl, this.feedLimit));
            this.feedCachedUrl = selectedUrl;
            downloadUrl(selectedUrl);
        } else {
            Log.d(TAG, "Using Cached Url: " + feedCachedUrl);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STATE_FEED_URL, feedUrl);
        outState.putInt(STATE_FEED_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void downloadUrl(String url) {
        new DownloadData().execute(url);
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ParseApplications newParser = new ParseApplications();
            boolean parseStatus = newParser.parse(s);
            if (!parseStatus) {
                Log.e(TAG, "Error in parsing xml");
            }

            //uses toString method to display the list items
            FeedAdapter feedAdapter = new FeedAdapter(
                    MainActivity.this,
                    R.layout.list_record,
                    newParser.getApplications());

            appList.setAdapter(feedAdapter);
//
//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<>(
//                    MainActivity.this,
//                    R.layout.list_item,
//                    newParser.getApplications()
//            );
//            appList.setAdapter(arrayAdapter);

        }

        @Override
        protected String doInBackground(String... strings) {
            //download xml from strings[0] (url)
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "Error in downloading xml");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d(TAG, "Response code:" + connection.getResponseCode());
                BufferedReader bufferedReader
                        = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead = 0;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsRead = bufferedReader.read(inputBuffer);
                    if (charsRead < 0) break;
                    if (charsRead > 0)
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                }

                bufferedReader.close();
                return xmlResult.toString();

            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML, invalid url: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML, IO Exception Reading data: " + e.getMessage());
            }

            return null;
        }
    }


}