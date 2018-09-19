package br.com.anhanguera.chat.atores;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import br.com.anhanguera.chat.dto.Login;
import br.com.anhanguera.chat.dto.LoginResponse;
import br.com.anhanguera.chat.dto.Mensagem;
import br.com.anhanguera.chat.dto.UsuarioConectadoResponse;

public class UsuarioActor extends AbstractLoggingActor {

	public static Props props() {
		return Props.create(UsuarioActor.class);
	}

	private Session session;
	private ActorRef mediator;
	private String email;
	
	public UsuarioActor() {
		mediator = DistributedPubSub.get(getContext().system()).mediator();
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(LoginMessage.class, this::inserirEmail)
				.match(UsuarioConectado.class, msg -> {
					Map<String, String> usuarioConectado = new HashMap<>();
					usuarioConectado.put("email", msg.email);
					enviarMensagem(this.session, usuarioConectado, "novo_usuario");
					
					mediator.tell(new DistributedPubSubMediator.Publish("msg_"+msg.email,
							new UsuarioActor.OlaUsuarioConectado(this.email)), getSelf());
					
				})
				.match(OlaUsuarioConectado.class, msg ->{
					Map<String, String> usuarioConectado = new HashMap<>();
					usuarioConectado.put("email", msg.email);
					enviarMensagem(this.session, usuarioConectado, "novo_usuario");
				})
				.match(DistributedPubSubMediator.SubscribeAck.class, msg -> log().info("subscribed " + msg.subscribe()))
				.build();
	}

	private void inserirEmail(LoginMessage msg) {
		this.session = msg.session;
		this.email = msg.email;
		
		enviarMensagem(this.session, new LoginResponse(true, "Login sucesso"), "login_response");
		
		Firestore fdb = FirestoreClient.getFirestore();
		Map<String, Object> dados = new HashMap<>();
		dados.put("email", msg.email);
		dados.put("data_hora_atualizacao", new Date());
		fdb.collection("usuarios").document(msg.email).set(dados);
		
		Login login = new Login(msg.email);
		
		enviarMensagemUsuarioConectado(login);

		
		mediator.tell(new DistributedPubSubMediator.Subscribe("msg_" + msg.email, getSelf()), getSelf());
		
		mediator.tell(new DistributedPubSubMediator.Publish("usuario_conectado",
				new UsuarioActor.UsuarioConectado(msg.email)), getSelf());
		
		mediator.tell(new DistributedPubSubMediator.Subscribe("usuario_conectado", getSelf()), getSelf());

	}

	private void enviarMensagem(Session sess, Object objeto, String acao) {
		JsonElement response = new Gson().toJsonTree(objeto);
		try {
			sess.getRemote().sendString(new Gson().toJson(new Mensagem(acao, response.getAsJsonObject())));
		} catch (IOException e) {
		}
	}

	private void enviarMensagemUsuarioConectado(Login login) {
		enviarMensagem(this.session, new UsuarioConectadoResponse(login.getEmail()), "usuario_conectado");
	}

	public static class LoginMessage implements Serializable {
		private static final long serialVersionUID = 1L;
		public final String email;
		public final Session session;

		public LoginMessage(String email, Session session) {
			this.email = email;
			this.session = session;
		}
	}
	
	public static class UsuarioConectado implements Serializable{
		private static final long serialVersionUID = 1L;
		
		public final String email;
		
		public UsuarioConectado(String email){
			this.email = email;
		}
	}
	
	public static class OlaUsuarioConectado implements Serializable{
		private static final long serialVersionUID = 1L;
		
		public final String email;
		
		public OlaUsuarioConectado(String email){
			this.email = email;
		}
	}

}
