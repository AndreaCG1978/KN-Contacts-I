package com.boxico.android.kn.kncontacts.persistencia.dtos;

public class ContraseniaDTO{
	


	private long id = -1;
	private String contrasenia= null;
	private boolean activa = true;
	private String mail = null;
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getContrasenia() {
		return contrasenia;
	}
	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}
	public boolean isActiva() {

		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}



	
	

}
