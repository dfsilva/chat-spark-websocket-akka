package br.com.anhanguera.chat;

import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import br.com.anhanguera.chat.controladores.ChatWebSocketHandler;

public class Principal {


	public static List<String> chats = Arrays.asList(new String[] {"Chat 1", "Chat 2"});


	public static void main(String[] args) {
		staticFiles.location("/html");
        staticFiles.expireTime(600);
		webSocket("/message", ChatWebSocketHandler.class);
		
		get("/chats", (req, res)->{
			return new Gson().toJson(chats);
		});
		
		init();
	}
}
