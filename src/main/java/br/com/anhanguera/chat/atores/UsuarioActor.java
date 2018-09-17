package br.com.anhanguera.chat.atores;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.cloud.FirestoreClient;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class UsuarioActor extends AbstractLoggingActor {

	public static Props props() {
		return Props.create(UsuarioActor.class);
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Inserir.class, this::inserirEmail)
				.build();
	}

	private void inserirEmail(Inserir msg) {
		System.out.println("--------------Inserindo email -------------- " + msg.email);
//		Firestore fdb = FirestoreClient.getFirestore();
		FirestoreOptions options = 
				  FirestoreOptions.newBuilder().setTimestampsInSnapshotsEnabled(true).build();
		Firestore fdb = options.getService();
		Map<String, Object> dados = new HashMap<>();
		dados.put("email", msg.email);
		fdb.collection("usuarios").document(msg.email).set(dados);
	}

	public static class Inserir implements Serializable {
		private static final long serialVersionUID = 1L;
		public final String email;

		public Inserir(String email) {
			this.email = email;
		}
	}

}
