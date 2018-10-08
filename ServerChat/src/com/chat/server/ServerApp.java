package com.chat.server;



public class ServerApp {
	
	public static void main(String[] args) {
		Server server = new Server(20000);
		server.serverTurnOn();
	}

}
