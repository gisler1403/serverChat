package com.chat.server;

import java.io.Serializable;

public class User implements Serializable {
	
	private String name;
	
	private Message message;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	
	
	

}
