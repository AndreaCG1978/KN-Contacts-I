package com.boxico.android.kn.kncontacts.persistencia.dtos;

public class ConfigDTO {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	private boolean ordenadoPorCategoria = false;
	private boolean estanDetallados = false;
	private boolean listaExpandida = false;
	private boolean muestraPreferidos = false;
	public boolean isOrdenadoPorCategoria() {
		return ordenadoPorCategoria;
	}
	public void setOrdenadoPorCategoria(boolean ordenadoPorCategoria) {
		this.ordenadoPorCategoria = ordenadoPorCategoria;
	}
	public boolean isEstanDetallados() {
		return estanDetallados;
	}
	public void setEstanDetallados(boolean estanDetallados) {
		this.estanDetallados = estanDetallados;
	}
	public boolean isListaExpandida() {
		return listaExpandida;
	}
	public void setListaExpandida(boolean listaExpandida) {
		this.listaExpandida = listaExpandida;
	}
	public boolean isMuestraPreferidos() {
		return muestraPreferidos;
	}
	public void setMuestraPreferidos(boolean muestraPreferidos) {
		this.muestraPreferidos = muestraPreferidos;
	}
	
	

}
