package br.com.anhanguera.chat.controladores;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import br.com.anhanguera.chat.dto.Login;
import br.com.anhanguera.chat.dto.LoginResponse;
import br.com.anhanguera.chat.dto.Mensagem;
import br.com.anhanguera.chat.dto.UsuarioConectadoResponse;

@WebSocket
public class ChatWebSocketHandler {

	private static Map<Session, String> usuarios = new ConcurrentHashMap<>();

	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
	}

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {

	}

	@OnWebSocketMessage
	public void onMessage(Session user, String message) {
		Mensagem mensagem = new Gson().fromJson(message, Mensagem.class);
		if (mensagem.getAcao().equals("login")) {
			Login login = new Gson().fromJson(mensagem.getData(), Login.class);
			usuarios.put(user, login.getEmail());

			enviarMensagem(user,
					new LoginResponse(true, "Login sucesso",
							usuarios.entrySet().stream().filter(valor -> !login.getEmail().equals(valor.getValue()))
									.map(valor -> valor.getValue()).collect(Collectors.toList())),
					"login_response");

			enviarMensagemUsuarioConectado(login);

		}
	}

	private void enviarMensagem(Session sess, Object objeto, String acao) {
		JsonElement response = new Gson().toJsonTree(objeto);
		try {
			sess.getRemote().sendString(new Gson().toJson(new Mensagem(acao, response.getAsJsonObject())));
		} catch (IOException e) {
		}
	}

	private void enviarMensagemUsuarioConectado(Login login) {
		usuarios.entrySet().stream().forEach(entry -> {
			if (!entry.getValue().equals(login.getEmail())) {
				enviarMensagem(entry.getKey(), new UsuarioConectadoResponse(login.getEmail()), "usuario_conectado");
			}
		});
	}

}
