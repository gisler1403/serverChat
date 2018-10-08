package com.chat.client;

import java.util.Scanner;

import com.chat.server.User;

public class ClientMain {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Podaj sw�j nick : ");
		String name = scanner.nextLine();
		
		User user = new User();
		user.setName(name);
		
		System.out.println("Podaj nick rozm�wcy : ");
		name = scanner.nextLine();
		User userTo = new User();
		userTo.setName(name);
		Client client = new Client(20000, user, userTo, scanner);
		client.setUpConnection();
	}
}
