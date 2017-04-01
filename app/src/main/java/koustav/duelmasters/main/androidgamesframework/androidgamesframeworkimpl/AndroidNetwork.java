package koustav.duelmasters.main.androidgamesframework.androidgamesframeworkimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by Koustav on 7/4/2015.
 */
public class AndroidNetwork implements Runnable {
    AndroidGame game;
    ServerSocket serverSocket;
    Socket socket;
    Thread socketThread = null;
    PrintWriter out;
    BufferedReader in;
    ArrayList<String> receivedDirective;
    volatile boolean running = false;

    public AndroidNetwork(AndroidGame game) {
        this.game = game;
        socket = null;
        serverSocket = null;
        out = null;
        in = null;
        receivedDirective = new ArrayList<String>();
    }

    public void start() {
        running = true;
        socketThread = new Thread(this);
        socketThread.start();
    }

    public void run() {
        while (running) {
            if (socket != null && !socket.isClosed()) {
                try {
                    if (in != null && in.ready()) {
                        String msg = in.readLine();
                        receivedDirective.add(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        running = false;
        while (true) {
            try {
                socketThread.join();
                break;
            } catch (InterruptedException e) {

            }
        }
    }

    public void setSocket(Socket socket) {

        if (socket != null) {
            this.socket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void setServerSocket (ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public PrintWriter getOutStream() {
        return this.out;
    }

    public BufferedReader getInStream() {
        return this.in;
    }

    public int getreceivedDirectiveSize() {
        return receivedDirective.size();
    }

    public String getreceivedDirectiveMsg() {
        if (receivedDirective.size() != 0) {
            return receivedDirective.remove(0);
        }

        return null;
    }
}
