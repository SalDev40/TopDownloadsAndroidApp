package com.example.top10downloader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appList = findViewById(R.id.xml_list_view);

        DownloadData downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=25/xml");

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
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this,
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