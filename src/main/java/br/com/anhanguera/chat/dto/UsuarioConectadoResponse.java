package br.com.anhanguera.chat.dto;

public class UsuarioConectadoResponse {

	private String usuario;

	public UsuarioConectadoResponse(){
		
	}
	
	public UsuarioConectadoResponse(String usuario) {
		super();
		this.usuario = usuario;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
		
}
