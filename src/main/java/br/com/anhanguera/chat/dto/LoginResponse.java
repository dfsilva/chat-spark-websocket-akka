package br.com.anhanguera.chat.dto;

public class LoginResponse {

	private boolean sucesso;
	private String mensagem;


	public LoginResponse() {

	}

	public LoginResponse(boolean sucesso, String mensagem) {
		super();
		this.sucesso = sucesso;
		this.mensagem = mensagem;
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
}
