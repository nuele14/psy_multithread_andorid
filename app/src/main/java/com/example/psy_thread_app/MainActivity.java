package com.example.psy_thread_app;


import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button buttonStartThread;
    private TextView outTextView;
    private Handler mainHandler = new Handler();
    private  volatile boolean stopThread = false;
    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartThread = findViewById(R.id.button_start_thread);
        outTextView = findViewById(R.id.textView);
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
                    outTextView.setText("..working..");
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            String v="none";
            String address="https://zenquotes.io/api/random";
            try
            {
                URL url=new URL(address);
                BufferedReader rd = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = rd.readLine();
                v=line;
            }
            catch (IOException e)
            {
                v = e.toString();
            }
            return v;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    outTextView.setText(result);
                }
            });
        }
    }

}