package com.boxico.android.kn.kncontacts.persistencia.dtos;

public class TipoValorDTO {
	
	private long id = -1;
	private String tipo = null;
	private String valor = null;
	private String idPersona = null;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getIdPersona() {
		return idPersona;
	}
	public void setIdPersona(String idPersona) {
		this.idPersona = idPersona;
	}
	/*
	public TipoValorDTO getCopy(){
		TipoValorDTO copy = new TipoValorDTO();
		copy.setId(this.getId());
		copy.setIdPersona(this.getIdPersona());
		copy.setTipo(this.getTipo());
		copy.setValor(this.getValor());
		return copy;
	}
	*/
	

}
