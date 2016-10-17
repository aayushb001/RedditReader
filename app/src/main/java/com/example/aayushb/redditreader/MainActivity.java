package com.example.aayushb.redditreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView titlesListView;
    EditText subredditInput;
    Button searchButton;
    static ArrayList<String> redditTitlesList = new ArrayList<>();
    static HashMap<String, String> linksMap = new HashMap<>();

    public class getRedditData extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            JSONObject jsonData;
            try {
                jsonData = new JSONObject(result);
                populateList(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void populateList(JSONObject redditJsonData){

        try {
            for(int i = 0; i < redditJsonData.getJSONObject("data").getJSONArray("children").length(); i++){

                String title = redditJsonData.getJSONObject("data").getJSONArray("children")
                        .getJSONObject(i).getJSONObject("data").getString("title");
                String link = redditJsonData.getJSONObject("data").getJSONArray("children")
                        .getJSONObject(i).getJSONObject("data").getString("url");

                redditTitlesList.add(title);
                linksMap.put(title,link);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter tileListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, redditTitlesList);
        titlesListView.setAdapter(tileListAdapter);


    }

    public void searchForSubreddit(View view){

        String subreddit = subredditInput.getText().toString();

        if (subreddit.equals("frontpage")) {
            linksMap.clear();
            redditTitlesList.clear();
            new getRedditData().execute("https://www.reddit.com/.json");
        }else{
            linksMap.clear();
            redditTitlesList.clear();
            new getRedditData().execute("https://www.reddit.com/r/" + subreddit + ".json");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide(); //hides the action bar
        setContentView(R.layout.activity_main);
        titlesListView = (ListView) findViewById(R.id.titlesListView);
        subredditInput = (EditText) findViewById(R.id.subredditInput);
        searchButton = (Button) findViewById(R.id.searchButton);

        titlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedLink = (titlesListView.getItemAtPosition(position)).toString();
                String link = linksMap.get(selectedLink);

                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("targetLink", link);
                startActivity(intent);
            }
        });

        new getRedditData().execute("https://www.reddit.com/.json");
    }
}