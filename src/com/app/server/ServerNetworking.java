package com.app.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ResourceBundle;

public class ServerNetworking {

	private int port;
	private DatagramSocket socket;

	private Thread send;
	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private final int MAX_PACKET_SIZE = Integer.parseInt(config.getString("max-packet-size"));

	public ServerNetworking(int port) {
		this.port = port;
	}

	public boolean openSocket() {
		boolean result = false;
		try {
			socket = new DatagramSocket(port);
			result = true;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return result;
	}

	public DatagramPacket receive() {
		byte[] data = new byte[MAX_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packet;
	}

	public void send(byte[] data, InetAddress destIP, int destPort) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, destIP, destPort);
				try {
					socket.send(packet);
				} catch (SocketException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

}
