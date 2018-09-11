package br.com.anhanguera.chat.controladores;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ChatWebSocketHandler {
	
	private static Map<Session, String> sessions = new ConcurrentHashMap<>();

	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
		sessions.put(user, "Anonimo "+new Date().getTime());
	}

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {
		
	}

	@OnWebSocketMessage
	public void onMessage(Session user, String message) {

	}

}
