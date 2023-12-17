package com.example.psy_thread_app;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import java.io.BufferedReader;
import java.io.Console;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button buttonStartThread;
    private TextView quoteTextView;
    private TextView authTextView;
    private ImageView imageView;

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
        imageView = findViewById(R.id.imageViewAvatar);

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
            int x, y, flg;
            int N = 100;
            for (x = 1; x <= N; x++) {

                if (x == 1 || x == 0)
                    continue;

                flg = 1;

                for (y = 2; y <= x / 2; ++y) {
                    if (x % y == 0) {
                        flg = 0;
                        break;
                    }
                }

                if (flg == 1){
                    Log.d(TAG, "numero primo: " + x);
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
            String imageUrl = null;
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
            if(searchForImage){
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

                    XPathFactory factory = XPathFactory.newInstance();
                    XPath xpath = factory.newXPath();

                    InputSource source = new InputSource(new StringReader(responseBody));
                    Document doc = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
                    NodeList imagesNodeList = doc.getElementsByTagName("img");
                    List<String> listaUrl = new ArrayList<>();
                    // Iterate through all img nodes
                    for (int i = 0; i < imagesNodeList.getLength(); i++) {
                        Element imageElement = (Element) imagesNodeList.item(i);
                        String url = imageElement.getAttribute("src");
                        if (!TextUtils.isEmpty(url)) {
                            listaUrl.add(url);
                        }
                    }
                    int size = listaUrl.size();
                    if(size>0){
                        Random random = new Random();
                        int randomNumber = random.nextInt(size);
                        imageUrl = listaUrl.get(randomNumber);
                        //String imageURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCLjkOCer-xuhkZuvvk3LJYiem4YkAJ869lUXSei5OtZ-SDLWi&usqp=CAU";


                        try {
                                        // Create an HTTP client
                            URL url=new URL(imageUrl);
                            InputStream inputStream = url.openStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    // Process the JSON data
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                }



            }

            return "";

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