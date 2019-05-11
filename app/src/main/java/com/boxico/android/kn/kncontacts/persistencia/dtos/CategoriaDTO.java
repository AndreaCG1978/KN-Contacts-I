package com.boxico.android.kn.kncontacts.persistencia.dtos;

public class CategoriaDTO implements Comparable<CategoriaDTO>{
	
	@Override
	public String toString() {
		return this.nombreRelativo;
	}

	private long id = 0;
	private String nombreReal = null;
	private String nombreRelativo = null;
	private String tipoDatoExtra = null;
	private boolean categoriaPersonal = false;
    // --Commented out by Inspection (12/11/2018 12:50):private boolean protegida = false;

// --Commented out by Inspection START (12/11/2018 12:44):
//	public boolean isProtegida() {
//		return protegida;
//	}
// --Commented out by Inspection START (12/11/2018 12:50):
//// --Commented out by Inspection STOP (12/11/2018 12:44)
//
//	public void setProtegida(boolean protegida) {
// --Commented out by Inspection STOP (12/11/2018 12:50)
	//	this.protegida = protegida;
	//}

	public boolean isCategoriaPersonal() {
		return categoriaPersonal;
	}
// --Commented out by Inspection STOP (12/11/2018 12:34)

	public void setCategoriaPersonal(boolean categoriaPersonal) {
		this.categoriaPersonal = categoriaPersonal;
	}

	public String getTipoDatoExtra() {
		return tipoDatoExtra;
	}

	public void setTipoDatoExtra(String tipoDatoExtra) {
		this.tipoDatoExtra = tipoDatoExtra;
	}

	private long activa = 0;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	
	public String getNombreReal() {
		return nombreReal;
	}

	public void setNombreReal(String nombreReal) {
		this.nombreReal = nombreReal;
	}

	public String getNombreRelativo() {
		return nombreRelativo;
	}

	public void setNombreRelativo(String nombreRelativo) {
		this.nombreRelativo = nombreRelativo;
	}

	public long getActiva() {
		return activa;
	}
	
	public void setActiva(long activa) {
		this.activa = activa;
	}
	
	public CategoriaDTO(long id, String nombreReal, String nombreRelativo, long activa, String tipoDatoExtra) {
		super();
		this.id = id;
		this.nombreReal = nombreReal;
		this.nombreRelativo = nombreRelativo;
		this.activa = activa;
		this.tipoDatoExtra = tipoDatoExtra;
	}



	@Override
	public int compareTo(CategoriaDTO another) {
		// TODO Auto-generated method stub
		int val;
		val = this.getNombreRelativo().compareTo(another.getNombreRelativo());
		return val;
	}


	public CategoriaDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
