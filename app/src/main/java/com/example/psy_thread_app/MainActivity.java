package com.example.psy_thread_app;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
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
    private Button buttonStartAll;
    private Button buttonStartQuote;
    private Button buttonStartNumber;
    private TextView quoteTextView;
    private TextView authTextView;
    private ImageView imageView;
    private TextView numbersView;
    private Switch switchBackground;

    private  volatile boolean stopThread = false;
    private volatile boolean quoteThreadActive = false;
    private volatile boolean numberThreadActive = false;
    NumbersRunnable runnableNumbers = new NumbersRunnable();
    QuotesRunnable runnableQuotes = new QuotesRunnable();
    Thread quotesThread;
    Thread numbersThread;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartAll = findViewById(R.id.buttonStartAll);
        buttonStartQuote = findViewById(R.id.buttonStartQuote);
        buttonStartNumber = findViewById(R.id.buttonStartNumbers);
        quoteTextView = findViewById(R.id.textViewQuotes);
        authTextView = findViewById(R.id.textViewAuthor);
        imageView = findViewById(R.id.imageViewAvatar);
        numbersView = findViewById(R.id.textViewPrimaryNumbers);
        switchBackground = findViewById(R.id.switchBackground);

        buttonStartNumber.setText("START NUMBER");
        buttonStartQuote.setText("START QUOTES");

        switchBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Set the background color to red
                    getWindow().setBackgroundDrawable(new ColorDrawable(Color.CYAN));
                } else {
                    // Set the background color to blue
                    getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                }
            }
        });
    }




    public void startNumberThread (View view) throws InterruptedException {
        if(!numberThreadActive){
            numbersView.setText("Inizio il calcolo dei numeri primi");
            buttonStartNumber.setText("STOP NUMBER");
            numberThreadActive = true;
            stopThread = false;
            numbersThread = new Thread(runnableNumbers);
            numbersThread.start();
        }else{
            numberThreadActive = false;
            stopThread = true;
            numbersThread.join();
        }

    }
    public void startQuoteThread (View view) throws InterruptedException {
        if(!quoteThreadActive){
            buttonStartQuote.setText("STOP QUOTES");
            quoteThreadActive = true;
            quotesThread = new Thread(runnableQuotes);
            quotesThread.start();
        }else{
            quoteThreadActive = false;
            quotesThread.join();
        }

    }

    public void stopThread (View view){
        stopThread = true;
    }

  public class NumbersRunnable implements Runnable{

        @Override
        public void run() {

            int x, y, flg;
            int N = 100;
            for (x = 1; x <= N; x++) {
                if(stopThread)
                {
                    break;
                }

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
                    int finalX = x;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String numeroPrimo = String.valueOf(finalX);
                            StringBuilder builder = new StringBuilder(numbersView.getText().toString());
                            builder.insert(0, "trovato: "+numeroPrimo+"\n");
                            numbersView.setText(builder.toString());
                        }
                    });
                }
            }
            //ripristino stato iniziale
            numberThreadActive = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonStartNumber.setText("START NUMBER");
                }
            });

        }
    }

    public class QuotesRunnable implements Runnable{

        @Override
        public void run() {
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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                }
            }
            quoteThreadActive = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonStartQuote.setText("START QUOTES");
                }
            });

        }
    }


}