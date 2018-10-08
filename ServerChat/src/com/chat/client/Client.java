package com.chat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.chat.server.Message;
import com.chat.server.User;

public class Client {

	static ExecutorService pool = Executors.newFixedThreadPool(2);

	private Socket socket;
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private User user;
	private User userTo;
	private Scanner scanner;

	public Client(int port, User user, User userTo, Scanner scanner) {
		this.user = user;
		this.userTo = userTo;
		this.scanner = scanner;
		try {
			socket = new Socket("127.0.0.1", port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		createStreams();
	}

	public void createStreams() {
		try {
			writer = new ObjectOutputStream(socket.getOutputStream());
			reader = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setUpConnection() {
		Runnable Odbieranie = new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Message messageTemp = (Message) reader.readObject();
						System.out.println(messageTemp.getMessage());
					} catch (ClassNotFoundException | IOException e) {
						System.out.println("Gniazdo zamkniête");
						try {
							System.out.println("Closing");
							socket.close();
							reader.close();
							pool.shutdownNow();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
					}
				}
			}
		};

		Runnable Nadawanie = new Runnable() {

			@Override
			public void run() {
				boolean firstMessageFlag = true;
				while (true) {

					try {

						if (firstMessageFlag) {
							firstMessageFlag = false;

							Message messageWelcome = new Message();
							messageWelcome.setUserFrom(user);
							messageWelcome.setUserTo(userTo);
							messageWelcome.setMessage("Po³¹czono uzytkownika: " + user.getName());

							writer.writeObject(messageWelcome);
						}

						Message messageTemp = new Message();
						messageTemp.setUserFrom(user);
						messageTemp.setUserTo(userTo);
						String msgString = scanner.nextLine();

						if (socket.isClosed()) {
							writer.close();
							scanner.close();
							break;
						}

						messageTemp.setMessage(msgString);
						writer.writeObject(messageTemp);

					} catch (IOException e) {
						pool.shutdownNow();
						e.printStackTrace();
					}
				}

			}
		};

		pool.execute(Nadawanie);
		pool.execute(Odbieranie);

	}

}
