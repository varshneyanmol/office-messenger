package com.app.server.tcp;

import java.net.Socket;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedTasksHandler implements RejectedExecutionHandler{

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        TCPTask tcpTask = (TCPTask) r;
        Socket tcpSocket = tcpTask.getTcpSocket();
        System.out.println(tcpSocket.toString() + " Request Rejected");
    }
}