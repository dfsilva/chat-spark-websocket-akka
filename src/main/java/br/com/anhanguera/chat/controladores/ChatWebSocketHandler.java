package br.com.anhanguera.chat.controladores;

import static br.com.anhanguera.chat.Principal.system;

import java.util.Date;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import akka.actor.ActorRef;

import br.com.anhanguera.chat.atores.UsuarioActor;
import br.com.anhanguera.chat.dto.EnviarMensagem;
import br.com.anhanguera.chat.dto.Login;
import br.com.anhanguera.chat.dto.Mensagem;


@WebSocket
public class ChatWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {

    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        // if(usuarioActor != null){
        //   usuarioActor.tell(new UsuarioActor.End(), ActorRef.noSender());
        //}
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        Mensagem mensagem = new Gson().fromJson(message, Mensagem.class);
        if (mensagem.getAcao().equals("login")) {
            Login login = new Gson().fromJson(mensagem.getData(), Login.class);
            ActorRef usuarioActor = UsuarioActor.getActorInstance(system, login.getEmail());
            usuarioActor.tell(new UsuarioActor.LoginMessage(login.getEmail(), user), ActorRef.noSender());
        }
        if (mensagem.getAcao().equals("enviar-mensagem")) {
            EnviarMensagem enviarMsg = new Gson().fromJson(mensagem.getData(), EnviarMensagem.class);
            ActorRef usuarioActor = UsuarioActor.getActorInstance(system, mensagem.getUsuario());
            usuarioActor
                    .tell(new UsuarioActor
                            .EncaminharMensagem(new Date().getTime(),
                            enviarMsg.para, enviarMsg.texto), ActorRef.noSender());
        }
    }

}
