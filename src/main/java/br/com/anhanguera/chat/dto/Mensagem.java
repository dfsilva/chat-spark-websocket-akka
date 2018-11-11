package br.com.anhanguera.chat.dto;

import com.google.gson.JsonObject;

public class Mensagem {

	private String acao;
	private JsonObject data;
	
	public Mensagem(){
		
	}

	public Mensagem(String acao, JsonObject data) {
		super();
		this.acao = acao;
		this.data = data;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public JsonObject getData() {
		return data;
	}

	public void setData(JsonObject data) {
		this.data = data;
	}

}
