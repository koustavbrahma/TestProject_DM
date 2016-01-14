package koustav.duelmasters.main.androidgameduelmasters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import koustav.duelmasters.R;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 7/1/2015.
 */
public class ClientLoginScreen extends Screen {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear, buttonBack;
    OnClickListener buttonConnectOnClickListener;
    volatile boolean lock = false;

    public ClientLoginScreen(final AndroidGame game) {
        super(game);
        game.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.setContentView(R.layout.activity_client);

                editTextAddress = (EditText)game.findViewById(R.id.address);
                editTextPort = (EditText)game.findViewById(R.id.port);
                buttonConnect = (Button)game.findViewById(R.id.connect);
                buttonClear = (Button)game.findViewById(R.id.clear);
                buttonBack = (Button) game.findViewById(R.id.back);
                textResponse = (TextView)game.findViewById(R.id.response);
                buttonConnectOnClickListener =
                        new OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                if (lock == false) {
                                    MyClientTask myClientTask = new MyClientTask(
                                            editTextAddress.getText().toString(),
                                            editTextPort.getText().toString());
                                    lock = true;
                                    textResponse.setText("Connecting ...");
                                    InputMethodManager imm = (InputMethodManager) game.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                    myClientTask.execute();
                                }
                            }};
                buttonConnect.setOnClickListener(buttonConnectOnClickListener);
                buttonClear.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (lock == false) {
                            textResponse.setText("");
                        }
                    }});
                buttonBack.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lock == false) {
                            game.setScreen(new MenuScreen(game));
                            InputMethodManager imm = (InputMethodManager) game.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                });
            }
        });
    }

    public class MyClientTask extends AsyncTask<Void , Void, Void > {

        String dstAddress;
        String  dstPort;
        String response = "";

        MyClientTask(String addr, String  port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void ... arg0) {
            try {
                Socket socket = new Socket(dstAddress, Integer.parseInt(dstPort));
                game.getNetwork().setSocket(socket);
                game.getNetwork().getOutStream().println("Client is ready for Duel");
                response = game.getNetwork().getInStream().readLine();
            } catch (UnknownHostException e) {
                response = "Unable to connect";
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } catch (NumberFormatException e) {
                response = "Invalid IP address";
            }
            lock = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (response!= null && response.equals("Host is ready for Duel")) {
                textResponse.setText("Connecting ...");
                game.setTurn(false);
                game.setScreen(new TestScreen(game));
                game.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        game.getRenderObj().resume();
                        game.setContentView(game.getViewObj());
                    }
                });
            } else {
                    if (game.getNetwork().getSocket() != null) {
                        game.getNetwork().setSocket(null);
                    }
                    if (response != null) {
                        textResponse.setText(response);
                    } else {
                        textResponse.setText("Unable to connect");
                    }
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void update(float deltaTime) {
        game.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.getRenderObj().pause();
            }
        });
    }

    @Override
    public void present(float deltaTime) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {
        if (lock == false) {
            game.setScreen(new MenuScreen(game));
        }
    }
}
