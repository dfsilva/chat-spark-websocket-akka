package br.com.anhanguera.chat.dto;

import java.util.List;

public class LoginResponse {

	private boolean sucesso;
	private String mensagem;
	private List<String> usuarios;

	public LoginResponse() {

	}

	public LoginResponse(boolean sucesso, String mensagem, List<String> usuarios) {
		super();
		this.sucesso = sucesso;
		this.mensagem = mensagem;
		this.usuarios = usuarios;
	}

	public boolean isSucesso() {
		return sucesso;
	}

	public void setSucesso(boolean sucesso) {
		this.sucesso = sucesso;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public List<String> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<String> usuarios) {
		this.usuarios = usuarios;
	}

}
