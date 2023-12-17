package com.example.psy_thread_app;


import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button buttonStartThread;
    private TextView quoteTextView;
    private TextView authTextView;
    private Handler mainHandler = new Handler();
    private  volatile boolean stopThread = false;
    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartThread = findViewById(R.id.button_start_thread);
        quoteTextView = findViewById(R.id.textViewQuotes);
        authTextView = findViewById(R.id.textViewAuthor);

    }

    public void startThread (View view){
        stopThread = false;
        WebAsyncTask myAsyncTask = new WebAsyncTask();
        myAsyncTask.execute();
    }

    public void stopThread (View view){
        stopThread = true;
    }







    public class MyAsyncTask extends AsyncTask<Void, Void, String> {
        int seconds;
        public MyAsyncTask(int i) {
            this.seconds = i;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonStartThread.setText("..working..");
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            for (int i = 0 ; i < seconds ; i++){
                if(stopThread)
                {
                    return "aborted";
                }
                if( i == 5){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttonStartThread.setText("50%");
                        }
                    });
                }
                Log.d(TAG, "start Thread: "+i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonStartThread.setText("Start");
                }
            });
        }
    }

    public class WebAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    authTextView.setText("..working..");
                    quoteTextView.setText("..working..");
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "error";
            boolean searchForImage = false;
            String jsonString="";
            String author = null;
            String address="https://zenquotes.io/api/random";
            try
            {
                URL url=new URL(address);
                BufferedReader rd = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = rd.readLine();
                jsonString=line;
            }
            catch (IOException e)
            {
                jsonString = e.toString();
            }
            try {
                // Parse JSON array
                JSONArray jsonArray = new JSONArray(jsonString);

                // Access the first object in the array
                JSONObject firstObject = jsonArray.getJSONObject(0);

                // Access values by key
                String quote = firstObject.getString("q");
                author = firstObject.getString("a");
                if(author!=null){
                    searchForImage = true;
                }
                String finalAuthor = author;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        authTextView.setText(finalAuthor);
                        quoteTextView.setText(quote);
                    }
                });
                // Print decoded values
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*if(searchForImage){
                String searchUrl = "https://www.google.com/search?q=" + author + "&tbm=isch";
                // Create an HTTP client
                OkHttpClient client = new OkHttpClient();

                // Create a GET request
                Request request = new Request.Builder()
                        .url(searchUrl)
                        .build();

                // Execute the request
                try (Response response = client.newCall(request).execute()) {
                    // Check the response code
                    if (response.code() != 200) {
                        //TODO:inserire una eccezzione
                    }

                    // Get the response body as a string
                    String responseBody = response.body().string();

                    // Parse the JSON response
                    JSONObject jsonObject = new JSONObject(responseBody);
                    // Process the JSON data
                }

                // Get the response entity
                HttpEntity entity = response.getEntity();

                Response response = client.newCall(request).execute();
                String imageUrl = "";
                Document doc = Jsoup.parse(response.body().string());
                Elements imgElements = doc.getElementsByTag("img");
                if (!imgElements.isEmpty()) {
                    Element firstImg = imgElements.first();
                    imageUrl = firstImg.attr("src");
                }
                if (!TextUtils.isEmpty(imageUrl)) {
                    Picasso.get().load(imageUrl).into(ivImage);
                }
            }*/

            return "done";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //outTextView.setText(result);
                }
            });
        }
    }

}