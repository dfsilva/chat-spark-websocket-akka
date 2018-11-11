package br.com.anhanguera.chat.dominio;

public class MensagemChat {
	
	public long timestamp;
	public String de;
	public String para;
	public String texto;
	
	public MensagemChat(long timestamp, String de, String para, String texto) {
		super();
		this.timestamp = timestamp;
		this.de = de;
		this.para = para;
		this.texto = texto;
	}
	
	

}
