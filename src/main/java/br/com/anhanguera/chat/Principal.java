package br.com.anhanguera.chat;

import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.cluster.singleton.ClusterSingletonManager;
import br.com.anhanguera.chat.controladores.ChatWebSocketHandler;

public class Principal {

	public static List<String> chats = Arrays.asList(new String[] { "Chat 1", "Chat 2" });

	public static void main(String[] args) {
		staticFiles.location("/html");
		staticFiles.expireTime(600);
		
		webSocket("/chat", ChatWebSocketHandler.class);

		Config config = ConfigFactory.load();
		ActorSystem system = ActorSystem.create("chat-server", config);
		
//		system.actorOf(
//                ClusterSingletonManager.props(
//                        SingletonPersistentActor.props(),
//                        PoisonPill.getInstance(),
//                        ClusterSingletonManagerSettings.create(system)
//                ),"enquetePersistence");

		init();
		
		system.terminate();
	}
}
