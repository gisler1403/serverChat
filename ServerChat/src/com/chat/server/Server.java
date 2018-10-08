package com.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Server {

	static ExecutorService pool = Executors.newFixedThreadPool(10);
	private Semaphore semaphore;
	private ServerSocket serverSocket;
	
	static Set<User> userList;
	static Set<ServerListener> serverListenerList;

	public Server(int port) {
		try {
			serverSocket = new ServerSocket(port);
			userList = new HashSet<>();
			serverListenerList = new HashSet<>();
			semaphore = new Semaphore(2);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void serverTurnOn() {
		ObjectInputStream reader;
		ObjectOutputStream writer;
		
		while(true) {
		
		try {
			Socket clientSocket = serverSocket.accept();
			writer = new ObjectOutputStream(clientSocket.getOutputStream());
			reader = new ObjectInputStream(clientSocket.getInputStream());
			ServerListener serverListener = new ServerListener(reader, writer, semaphore, clientSocket);
			serverListenerList.add(serverListener);
			pool.execute(serverListener);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	}
}
