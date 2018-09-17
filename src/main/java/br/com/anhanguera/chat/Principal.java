package br.com.anhanguera.chat;

import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import br.com.anhanguera.chat.atores.UsuarioActor;
import br.com.anhanguera.chat.controladores.ChatWebSocketHandler;
import br.com.anhanguera.chat.utils.Firebase;

public class Principal {

	public static ActorSystem system;

	public static void main(String[] args) throws IOException {
		staticFiles.location("/html");
		staticFiles.expireTime(600);
		
		webSocket("/chat", ChatWebSocketHandler.class);

		Config config = ConfigFactory.load();
		system = ActorSystem.create("chat-server", config);
		
		
		
		Firebase.init();

		init();
		
//		system.terminate();
	}
}
