package com.kerberosstudios.flappybird;

import android.app.Application;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

/**
 * Created by Zinzano on 3/11/2018.
 */

public class SocketHandlerApplication extends Application {


    private Socket mSocket;
    {
        /*
        Log.i("Connection with server", "start");
        try {
            // mSocket = IO.socket("http://192.168.1.6:8080");
            // mSocket = IO.socket("http://130.229.145.44:8080");
             mSocket = IO.socket("http://130.229.171.112:8080");



        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Log.i("Connection with server", "done");

        */
    }
}
