package com.kerberosstudios.flappybird;

import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by Zinzano on 3/14/2018.
 */

public class SocketHandler {
    private static Socket socket;

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }

    public static synchronized Socket getSocket(){
        return socket;
    }

}
