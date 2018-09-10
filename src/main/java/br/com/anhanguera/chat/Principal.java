package br.com.anhanguera.chat;

import static spark.Spark.init;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import br.com.anhanguera.chat.controladores.ChatWebSocketHandler;

public class Principal {

	public static void main(String[] args) {
		staticFiles.location("/html");
		staticFiles.expireTime(600);
		webSocket("/message", ChatWebSocketHandler.class);

		init();
	}
}
