package koustav.duelmasters.main.androidgameduelmasterscreens;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import koustav.duelmasters.R;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Screen;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 7/5/2015.
 */
public class HostScreen extends Screen {
    TextView info, infoip, msg;
    String message = "";
    Button buttonBack;
    Thread socketServerThread;

    public HostScreen(final AndroidGame game) {
        super(game);
        game.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.setContentView(R.layout.activity_host);
                info = (TextView) game.findViewById(R.id.info);
                infoip = (TextView) game.findViewById(R.id.infoip);
                msg = (TextView) game.findViewById(R.id.msg);
                buttonBack = (Button) game.findViewById(R.id.back);
                buttonBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (game.getNetwork().getServerSocket() != null) {
                            try {
                                game.getNetwork().getServerSocket().close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        game.setScreen(new MenuScreen(game));
                        }
                    }

                    );
                    infoip.setText(getIpAddress());
                    socketServerThread=new Thread(new SocketServerThread());
                    socketServerThread.start();
                }
            });
    }

    private String getIpAddress() {
        String ip = "";
        try {
            //Loop through all the network interface devices
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements();) {
                NetworkInterface networkInterface = enumeration.nextElement();
                //Loop through all the ip addresses of the network interface devices
                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface.getInetAddresses(); enumerationIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumerationIpAddr.nextElement();
                    //Filter out loopback address and other irrelevant ip addresses
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        //Print the device ip address in to the text view
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;
        String response = "";

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(SocketServerPORT);
                game.getNetwork().setServerSocket(serverSocket);
                game.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info.setText("I'm waiting here: "
                                + game.getNetwork().getServerSocket().getLocalPort());
                    }
                });

                while (true && !serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n";

                    game.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            msg.setText(message);
                        }
                    });
                    game.getNetwork().setSocket(socket);
                    response = game.getNetwork().getInStream().readLine();
                    if (response != null && response.equals("Client is ready for Duel")) {
                        game.getNetwork().getOutStream().println("Host is ready for Duel");
                        game.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                msg.setText("Connecting ...");
                            }
                        });

                        game.setTurn(true);
                        game.setScreen(new TestScreen(game));
                        game.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                game.getRenderObj().resume();
                                game.setContentView(game.getViewObj());
                            }
                        });
                        break;
                    } else {
                        if (game.getNetwork().getSocket() != null) {
                            game.getNetwork().setSocket(null);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                message += "IOException: " + e.toString();
                message += "#Create Host again";
                game.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        msg.setText(message);
                    }
                });
                if (game.getNetwork().getSocket() != null) {
                    game.getNetwork().setSocket(null);
                }
            }
        }
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        game.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.getRenderObj().pause();
            }
        });
    }

    @Override
    public void present() {
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
        if (game.getNetwork().getServerSocket() != null) {
            try {
                game.getNetwork().getServerSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        game.setScreen(new MenuScreen(game));
    }
}
