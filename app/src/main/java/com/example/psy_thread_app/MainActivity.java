package com.example.psy_thread_app;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button buttonStartThread;
    private Handler mainHandler = new Handler();
    private  volatile boolean stopThread = false;
    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartThread = findViewById(R.id.button_start_thread);
    }

    public void startThread (View view){
        stopThread = false;
        //Gestione Thread 1
        //ExampleThread thread = new ExampleThread(10);
        //thread.start();

        //Gesione Thread 2 (preferito perchè è possibile continuare a
        ExampleRunnable runnable = new ExampleRunnable(10);
        new Thread(runnable).start();
    }

    public void stopThread (View view){
        stopThread = true;
    }


    class ExampleThread extends Thread{
        int seconds;

        ExampleThread(int seconds){
            this.seconds = seconds;
        }
        @Override
        public void run() {
            for (int i = 0 ; i < seconds ; i++){
                Log.d(TAG, "start Thread: "+i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ExampleRunnable implements Runnable{
        int seconds;

        ExampleRunnable(int seconds){
            this.seconds = seconds;
        }
        @Override
        public void run() {
            for (int i = 0 ; i < seconds ; i++){
                if(stopThread)
                {
                    return;
                }
                if( i == 5){
                    /*
                    // modo 1
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            buttonStartThread.setText("50%");
                        }
                    });
                    */

                    /*// più raffinato ma complicato
                    Handler thereadHandler = new Handler(Looper.getMainLooper());
                    thereadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            buttonStartThread.setText("50%");
                        }
                    });*/

                    /*buttonStartThread.post(new Runnable() {
                        @Override
                        public void run() {
                            buttonStartThread.setText("50%");
                        }
                    });*/

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
        }
    }
}