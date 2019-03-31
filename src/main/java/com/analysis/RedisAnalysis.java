package com.analysis;

import java.io.IOException;
import java.net.Socket;

public class RedisAnalysis {

    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int port = 6379;
        Socket socket = new Socket(host, port);
        socket.setSoTimeout(3000);
        CommandDecoder decoder = new CommandDecoder();
    }

}
