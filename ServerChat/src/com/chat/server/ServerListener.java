package com.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class ServerListener implements Runnable {

	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private Semaphore semaphore;
	private User user;
	private Socket socket;

	public ServerListener(ObjectInputStream reader, ObjectOutputStream writer, Semaphore semaphore, Socket s) {
		this.reader = reader;
		this.writer = writer;
		this.semaphore = semaphore;
		this.socket = s;
	}

	@Override
	public void run() {
		Queue<Message> messageBuffor = new LinkedList<>();
		Message messageClosing = null;
		while (true) {
			try {

				Message messageTemp = (Message) reader.readObject();
				messageBuffor.add(messageTemp);

				user = messageTemp.getUserFrom();

				
				Server.userList.add(user);
				semaphore.acquire();
				for (ServerListener slTemp : Server.serverListenerList) {
					if (slTemp.getUser() != null) {
						if (slTemp.getUser().getName().equals(messageBuffor.peek().getUserTo().getName())) {
							while (!messageBuffor.isEmpty()) {
								if (messageBuffor.peek().getMessage().equals("exit")) {
									messageClosing = messageBuffor.poll();
									messageClosing.setMessage(
											"U¿ytkownik " + messageClosing.getUserFrom().getName() + " zosta³ roz³¹czony");
									slTemp.getWriter().writeObject(messageClosing);
								} else {
									slTemp.getWriter().writeObject(messageBuffor.poll());
								}

							}
							break;	
						}
					}
				}
				semaphore.release();
				
				if (messageClosing != null) {
					System.out.println(messageClosing.getMessage());
					if (messageClosing.getMessage().equals("U¿ytkownik " + messageClosing.getUserFrom().getName() + " zosta³ roz³¹czony")) {
						System.out.println("Closing socket");
						break;
					}
				}
				System.out.println(user.getName() + " Rozmar kolejki po for : " + messageBuffor.size());
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			socket.close();
			reader.close();
			writer.close();
			Server.serverListenerList.remove(this);
			Server.userList.remove(this.user);
			System.out.println(Server.serverListenerList.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public User getUser() {
		return user;
	}

	public ObjectInputStream getReader() {
		return reader;
	}

	public ObjectOutputStream getWriter() {
		return writer;
	}

}
