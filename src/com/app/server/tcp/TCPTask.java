package com.app.server.tcp;

import com.app.server.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ResourceBundle;

public class TCPTask implements Runnable {
    private Socket tcpSocket;
    private Server server;

    private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
    private String fileSendIdentifier = config.getString("file-send-identifier");
    private String fileReceiveIdentifier = config.getString("file-receive-identifier");

    public TCPTask(Socket tcpSocket, Server server) {
        this.tcpSocket = tcpSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            DataInputStream is = new DataInputStream(tcpSocket.getInputStream());

            String message = is.readUTF();
            if (message.startsWith(fileSendIdentifier)) {
                /**
                 * receives a msg like: "/s/serverFileName"
                 * means: server is to send a file
                 */
                message = message.substring(fileSendIdentifier.length(), message.length());
                server.getFileManager().send(message, tcpSocket);

            } else if (message.startsWith(fileReceiveIdentifier)) {
                /**
                 * receives a msg like: "/v/message"
                 * means: server is to receive a file
                 */
                message = message.substring(fileReceiveIdentifier.length(), message.length());
                long byteArrSize = is.readLong();
                byte[] byteArr = new byte[(int) byteArrSize];
                is.readFully(byteArr, 0, byteArr.length);
                server.getFileManager().process(message, byteArr);
            }

            tcpSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getTcpSocket() {
        return tcpSocket;
    }
}
