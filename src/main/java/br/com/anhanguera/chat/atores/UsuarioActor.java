package br.com.anhanguera.chat.atores;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import akka.actor.*;
import akka.util.Timeout;
import br.com.anhanguera.chat.dto.*;
import org.eclipse.jetty.websocket.api.Session;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import br.com.anhanguera.chat.dominio.MensagemChat;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import static br.com.anhanguera.chat.Principal.system;

public class UsuarioActor extends AbstractLoggingActor {

    public static Props props() {
        return Props.create(UsuarioActor.class);
    }

    private Session session;
    private ActorRef mediator;
    private String email;

    private Map<String, List<MensagemChat>> mensagens = new HashMap<>();

    public UsuarioActor() {
        mediator = DistributedPubSub.get(getContext().system()).mediator();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LoginMessage.class, this::login)
                .match(UsuarioConectado.class, msg -> {
                    if (!this.email.equals(msg.email)) {
                        Map<String, String> usuarioConectado = new HashMap<>();
                        usuarioConectado.put("email", msg.email);
                        enviarMensagem(usuarioConectado, "novo_usuario");

                        ActorRef usuarioActor = UsuarioActor.getActorInstance(getContext().getSystem(), msg.email);
                        usuarioActor.tell(new UsuarioActor.OlaUsuarioConectado(this.email), getSelf());
                    }
                }).match(UsuarioDesconectado.class, msg -> {
                    if (!this.email.equals(msg.email)) {
                        Map<String, String> usuarioDesconectado = new HashMap<>();
                        usuarioDesconectado.put("email", msg.email);
                        enviarMensagem(usuarioDesconectado, "usuario_desconectado");
                    }
                }).match(End.class, msg -> {
                    //mediator.tell(new DistributedPubSubMediator.Publish("usuario_status",
                      //      new UsuarioActor.UsuarioDesconectado(this.email)), getSelf());
                    //mediator.tell(new DistributedPubSubMediator.Unsubscribe("usuario_status", getSelf()), getSelf());
                    //getContext().stop(getSelf());
                }).match(OlaUsuarioConectado.class, msg -> {
                    Map<String, String> usuarioConectado = new HashMap<>();
                    usuarioConectado.put("email", msg.email);
                    enviarMensagem(usuarioConectado, "novo_usuario");
                }).match(DistributedPubSubMediator.SubscribeAck.class, msg -> {
                    log().info("Inscrito no topico " + msg.subscribe());
                })
                .match(EncaminharMensagem.class, this::enviarMensagemChat)
                .match(ReceberMensagem.class, this::receberMensagem)
                .build();
    }

    private void receberMensagem(ReceberMensagem msg) {
        if (this.mensagens.containsKey(msg.remetente)) {
            this.mensagens.get(msg.remetente)
                    .add(new MensagemChat(msg.timestamp, msg.remetente, this.email, msg.texto));
        } else {
            this.mensagens.put(msg.remetente, Arrays.asList(
                    new MensagemChat[]{new MensagemChat(msg.timestamp, msg.remetente, this.email, msg.texto)}));
        }

        enviarMensagem(new AtualizarMensagensResponse(msg.remetente, this.mensagens.get(msg.remetente)), "atualizar_mensagens");
    }

    private void enviarMensagemChat(EncaminharMensagem msg) {
        ActorRef destinatario = UsuarioActor.getActorInstance(system, msg.para);
        if (destinatario != null) {
            if (this.mensagens.containsKey(msg.para)) {
                this.mensagens.get(msg.para).add(new MensagemChat(msg.timestamp, this.email, msg.para, msg.texto));
            } else {
                this.mensagens.put(msg.para, Arrays.asList(
                        new MensagemChat[]{new MensagemChat(msg.timestamp, this.email, msg.para, msg.texto)}));
            }

            ReceberMensagem envelopeMensagem = new ReceberMensagem(msg.timestamp, this.email, msg.texto);
            destinatario.tell(envelopeMensagem, getSelf());

            enviarMensagem(new AtualizarMensagensResponse(msg.para, this.mensagens.get(msg.para)), "atualizar_mensagens");
        }
    }

    private void login(LoginMessage msg) {
        this.session = msg.session;
        this.email = msg.email;

        enviarMensagem(new LoginResponse(true, "Login sucesso"), "login_response");

        Login login = new Login(msg.email);

        enviarMensagem(new UsuarioConectadoResponse(login.getEmail()), "usuario_conectado");

        mediator.tell(new DistributedPubSubMediator.Subscribe("usuario_status", getSelf()), getSelf());
        mediator.tell(
                new DistributedPubSubMediator.Publish("usuario_status", new UsuarioActor.UsuarioConectado(this.email)),
                getSelf());
    }

    private void enviarMensagem(Object objeto, String acao) {
        if (this.session.isOpen()) {
            JsonElement response = new Gson().toJsonTree(objeto);
            try {
                this.session.getRemote().sendString(new Gson().toJson(new Mensagem(acao, response.getAsJsonObject())));
            } catch (IOException e) {
            }
        } else {
            getSelf().tell(new UsuarioActor.End(), ActorRef.noSender());
        }
    }

    public static ActorRef getActorInstance(ActorSystem system, String actorId) {
        Timeout TIMEOUT = new Timeout(Duration.create(1, TimeUnit.SECONDS));
        ActorRef usuarioActor = null;

        try {
            ActorSelection usuarioSelection = system.actorSelection("/user/u-" + actorId);
            usuarioActor = Await.result(usuarioSelection.resolveOne(TIMEOUT), TIMEOUT.duration());
        } catch (Exception e) {
            usuarioActor = system.actorOf(UsuarioActor.props(), "u-" + actorId);
        }

        return usuarioActor;
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

    public static class UsuarioConectado implements Serializable {
        private static final long serialVersionUID = 1L;

        public final String email;

        public UsuarioConectado(String email) {
            this.email = email;
        }
    }

    public static class UsuarioDesconectado implements Serializable {
        private static final long serialVersionUID = 1L;

        public final String email;

        public UsuarioDesconectado(String email) {
            this.email = email;
        }
    }

    public static class OlaUsuarioConectado implements Serializable {
        private static final long serialVersionUID = 1L;

        public final String email;

        public OlaUsuarioConectado(String email) {
            this.email = email;
        }
    }

    public static class End implements Serializable {
    }

    public static class EncaminharMensagem implements Serializable {
        public long timestamp;
        public String para;
        public String texto;

        public EncaminharMensagem(long timestamp, String para, String texto) {
            super();
            this.timestamp = timestamp;
            this.para = para;
            this.texto = texto;
        }
    }

    public static class ReceberMensagem implements Serializable {
        public long timestamp;
        public String remetente;
        public String texto;

        public ReceberMensagem(long timestamp, String remetente, String texto) {
            super();
            this.timestamp = timestamp;
            this.remetente = remetente;
            this.texto = texto;
        }
    }

}
