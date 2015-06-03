package com.tuccro.socketclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends Activity {
    Socket connect;

    EditText editTextIP;
    EditText editTextPort;
    EditText editTextMessage;

    Button buttonConnect;
    Button buttonSendMessage;

    TextView textAnswer;

    LinearLayout layoutSendMessage;

    private String textIP;
    private String outText;
    private String text;
    private int port;

    private PrintWriter printWriter;
    public static final String TAG = "MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextIP = (EditText) findViewById(R.id.edit_text_ip_address);
        editTextPort = (EditText) findViewById(R.id.edit_text_port);
        editTextMessage = (EditText) findViewById(R.id.edit_text_out_message);

        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonSendMessage = (Button) findViewById(R.id.button_send);

        textAnswer = (TextView) findViewById(R.id.text_in_message);

        layoutSendMessage = (LinearLayout) findViewById(R.id.layout_send_message);

        buttonConnect.setOnClickListener(onClickListener);
        buttonSendMessage.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button_connect:
                    textIP = editTextIP.getText().toString();
                    port = Integer.parseInt(editTextPort.getText().toString());
                    new CreateConnect().execute(textIP, port);
                    break;

                case R.id.button_send:
                    String message;
                    if (connect.isConnected()) {
                        new SendDate().execute(editTextMessage.getText().toString());
                    } else new CreateConnect().execute(textIP, port);
                    break;
            }
        }
    };


    private class CreateConnect extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "START create socket IP = " + textIP + "\n Port = " + port);
        }

        @Override
        protected String doInBackground(Object[] params) {
            try {
                connect = new Socket(textIP, port);
                Log.i(TAG, "Good create socket");
            } catch (IOException e) {
                e.printStackTrace();
                return text = "Error in creare Connect " + e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
//            if (connect != null) {
//                textInfo.setText("Port is: " + port + "\nIP adress: " + textIP + " \n" + connect.toString());
//            } else {
//                Toast.makeText(getApplicationContext(), "Socket not valid", Toast.LENGTH_SHORT).show();
//                textInfo.setText("Port is: " + port + "\nIP adress: " + textIP);
//            }
        }
    }

    private class SendDate extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            outText = editTextMessage.getText().toString();
        }

        @Override
        protected String doInBackground(String[] params) {
            try {
                getDatafromServer();
                printWriter = new PrintWriter(new OutputStreamWriter(connect.getOutputStream()), true);
                printWriter.println(outText);
//                    if (printWriter != null) {
//                        printWriter.close();
//                    }
            } catch (IOException e) {
                e.printStackTrace();
                return text = "Error in send Date " + e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            Toast.makeText(getApplicationContext(), "All messages send to client", Toast.LENGTH_SHORT).show();
            textAnswer.setText(text);
            editTextMessage.setText("");
        }

        void getDatafromServer() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean w = true;
                    while (w) {
                        try {
                            new CreateConnect().execute();
                            InputStream inputStream = connect.getInputStream();
                            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                            text = br.readLine();
                            if (inputStream != null)
                                inputStream.close();
                            if (br != null)
                                br.close();
                            w = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                            text = "Error in the Run method " + e.toString();
                        }
                    }
                }
            }).start();
        }
    }
}