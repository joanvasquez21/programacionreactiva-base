package com.springboot.reactor.app.models;

public class UsuarioComentarios {
	
	private Usuario usuario;

	private Comentarios comentarios;
	
	
	public UsuarioComentarios(Usuario usuario, Comentarios comentarios) {
		super();
		this.usuario = usuario;
		this.comentarios = comentarios;
	}


	@Override
	public String toString() {
		return "UsuarioComentarios [usuario=" + usuario + ", comentarios=" + comentarios + "]";
	}
	
	
	
	

}
