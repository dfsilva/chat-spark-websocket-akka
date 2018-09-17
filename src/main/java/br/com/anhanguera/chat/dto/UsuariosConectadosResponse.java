package br.com.anhanguera.chat.dto;

import java.util.List;

public class UsuariosConectadosResponse {
	
	private List<String> usuarios;

	public UsuariosConectadosResponse(){
		
	}
	
	public UsuariosConectadosResponse(List<String> usuarios) {
		super();
		this.usuarios = usuarios;
	}

	public List<String> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<String> usuarios) {
		this.usuarios = usuarios;
	}
	
	

}
