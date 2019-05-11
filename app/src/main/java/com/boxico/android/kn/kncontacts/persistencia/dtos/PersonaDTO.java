package com.boxico.android.kn.kncontacts.persistencia.dtos;

public class PersonaDTO {
	private long id = -1;
	private String apellido = null;
	private String nombres = null;
	private String fechaNacimiento = null;
	private long categoriaId = -1;
/*	private String emailParticular = null;
	private String emailLaboral = null;
	private String emailOtro = null;
	private String celular = null;
	private String telLaboral = null;
	private String telParticular = null;
	private String direccionParticular = null;
	private String direccionLaboral = null;	*/
	private String categoriaNombre = null;
	private String categoriaNombreRelativo = null;
	private String datoExtra = null;
	private String foto = null;
	private String idPersonaAgenda = null;
	private String descripcion = null;

	public String getIdPersonaAgenda() {
		return idPersonaAgenda;
	}

	public void setIdPersonaAgenda(String idPersonaAgenda) {
		this.idPersonaAgenda = idPersonaAgenda;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}




	public String getCategoriaNombreRelativo() {

		return categoriaNombreRelativo;
	}

	public void setCategoriaNombreRelativo(String categoriaNombreRelativo) {
		this.categoriaNombreRelativo = categoriaNombreRelativo;
	}

	/*
	public String getEmailParticular() {
		return emailParticular;
	}
	public void setEmailParticular(String emailParticular) {
		this.emailParticular = emailParticular;
	}
	public String getEmailLaboral() {
		return emailLaboral;
	}
	public void setEmailLaboral(String emailLaboral) {
		this.emailLaboral = emailLaboral;
	}
	public String getEmailOtro() {
		return emailOtro;
	}
	public void setEmailOtro(String emailOtro) {
		this.emailOtro = emailOtro;
	}
	public String getDireccionParticular() {
		return direccionParticular;
	}
	public void setDireccionParticular(String direccionParticular) {
		this.direccionParticular = direccionParticular;
	}
	public String getDireccionLaboral() {
		return direccionLaboral;
	}
	public void setDireccionLaboral(String direccionLaboral) {
		this.direccionLaboral = direccionLaboral;
	}*/
	public String getDatoExtra() {
		return datoExtra;
	}
	public void setDatoExtra(String datoExtra) {
		this.datoExtra = datoExtra;
	}
	
	public String getCategoriaNombre() {
		return categoriaNombre;
	}
	public void setCategoriaNombre(String categoriaNombre) {
		this.categoriaNombre = categoriaNombre;
	}

	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public long getCategoriaId() {
		return categoriaId;
	}
	public void setCategoriaId(long categoriaId) {
		this.categoriaId = categoriaId;
	}
/*
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getTelLaboral() {
		return telLaboral;
	}
	public void setTelLaboral(String telLaboral) {
		this.telLaboral = telLaboral;
	}
	public String getTelParticular() {
		return telParticular;
	}
	public void setTelParticular(String telParticular) {
		this.telParticular = telParticular;
	}

	*/
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
// --Commented out by Inspection START (12/11/2018 12:36):
//	public PersonaDTO(String apellido, String nombres,
//			String fechaNacimiento, long categoriaId, String emailParticular, String emailLaboral, String emailOtro,
//			String celular, String telLaboral, String telParticular,
//			String descripcion, String direccionParticular, String direccionLaboral, String categoriaNombre, String datoExtra, long id) {
//		super();
//		this.apellido = apellido;
//		this.nombres = nombres;
//		this.fechaNacimiento = fechaNacimiento;
//		this.categoriaId = categoriaId;
//		this.emailParticular = emailParticular;
//		this.emailLaboral = emailLaboral;
//		this.emailOtro = emailOtro;
//		this.celular = celular;
//		this.telLaboral = telLaboral;
//		this.telParticular = telParticular;
//		this.descripcion = descripcion;
//		this.direccionLaboral = direccionLaboral;
//		this.direccionParticular = direccionParticular;
//		this.categoriaNombre = categoriaNombre;
//		this.datoExtra = datoExtra;
//		this.id = id;
//	}
// --Commented out by Inspection STOP (12/11/2018 12:36)
	public PersonaDTO() {
		// TODO Auto-generated constructor stub
	}
	
	
}
