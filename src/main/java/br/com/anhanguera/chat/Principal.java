package br.com.anhanguera.chat;

import java.io.IOException;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import br.com.anhanguera.chat.controladores.ChatWebSocketHandler;
import br.com.anhanguera.chat.utils.Firebase;

import static spark.Spark.*;

public class Principal {

	public static ActorSystem system;

	public static void main(String[] args) throws IOException {
		staticFiles.location("/html");
		staticFiles.expireTime(600);

		port(8080);

		webSocket("/chat", ChatWebSocketHandler.class);

		Config config = ConfigFactory.load();
		system = ActorSystem.create("chat-server", config);

		Firebase.init();

		init();


	}
}
