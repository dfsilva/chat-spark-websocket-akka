package br.com.anhanguera.chat.dto;

import br.com.anhanguera.chat.dominio.MensagemChat;

import java.util.List;

public class AtualizarMensagensResponse {

    public String chat;
    public List<MensagemChat> mensagems;

    public AtualizarMensagensResponse(){}

    public AtualizarMensagensResponse(String chat, List<MensagemChat> mensagems) {
        this.chat = chat;
        this.mensagems = mensagems;
    }
}
