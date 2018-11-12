package br.com.anhanguera.chat.dto;

import br.com.anhanguera.chat.dominio.MensagemChat;

import java.util.List;

public class AtualizarMensagensResponse {

    public String chat;
    public List<MensagemChat> mensagens;

    public AtualizarMensagensResponse(){}

    public AtualizarMensagensResponse(String chat, List<MensagemChat> mensagens) {
        this.chat = chat;
        this.mensagens = mensagens;
    }
}
