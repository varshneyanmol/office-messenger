package com.app.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class ClientNetworking {
	private DatagramSocket socket;
	private InetAddress serverIP;
	private int serverPort;
	private Thread send;

	ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private final int MAX_PACKET_SIZE = Integer.parseInt(config.getString("max-packet-size"));

	public ClientNetworking(InetAddress serverIP, int serverPort) {
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}

	public boolean openConnection() {

		try {
			socket = new DatagramSocket(); // Opens a sockets at any
											// available port
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public String receive() {
		byte[] data = new byte[MAX_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(packet.getData());
		return message;
	}

	public void send(byte[] data) {
		send = new Thread("Send_Client") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, serverIP, serverPort);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

	public void close() {
		new Thread() {
			public void run() {
				synchronized (socket) {
					socket.close();
				}
			}
		};
	}

}
