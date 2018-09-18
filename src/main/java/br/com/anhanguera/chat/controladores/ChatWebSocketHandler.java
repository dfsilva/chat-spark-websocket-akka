package br.com.anhanguera.chat.controladores;

import static br.com.anhanguera.chat.Principal.system;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import br.com.anhanguera.chat.atores.UsuarioActor;
import br.com.anhanguera.chat.dto.Login;
import br.com.anhanguera.chat.dto.Mensagem;

@WebSocket
public class ChatWebSocketHandler {

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
			system.actorOf(ClusterSingletonManager.props(UsuarioActor.props(), PoisonPill.getInstance(),
					ClusterSingletonManagerSettings.create(system)), "u-" + login.getEmail());

			ActorRef usuarioActor = system.actorOf(ClusterSingletonProxy.props("/user/u-" + login.getEmail(),
					ClusterSingletonProxySettings.create(system)));

			usuarioActor.tell(new UsuarioActor.LoginMessage(login.getEmail(), user), ActorRef.noSender());
		}
	}

}
