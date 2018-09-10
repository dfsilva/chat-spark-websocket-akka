package br.com.anhanguera.chat.controladores;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ChatWebSocketHandler {

	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
		System.out.println("Conexao estabelecida ");

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					user.getRemote()
							.sendString("Mensagem " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Timer timer = new Timer();
		long delay = 0;
		long intevalPeriod = 1 * 1000;
		timer.scheduleAtFixedRate(task, delay, intevalPeriod);
	}

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {

	}

	@OnWebSocketMessage
	public void onMessage(Session user, String message) {

	}

}
