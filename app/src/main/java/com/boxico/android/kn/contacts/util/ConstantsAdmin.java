package com.boxico.android.kn.contacts.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.boxico.android.kn.contacts.*;
import com.boxico.android.kn.contacts.R;
import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.ConfigDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.ContraseniaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.TipoValorDTO;

public class ConstantsAdmin {
	
	// CONSTANTES DE LA BASE DE DATOS
	
	public static DataBaseManager mDBManager = null;
	private static Map<String, List<PersonaDTO>> organizadosAlfabeticamente = null;
	private static Map<String, List<PersonaDTO>> organizadosPorCategoria = null;
	public static ListadoPersonaActivity mainActivity = null;

	public static Map<String, List<PersonaDTO>> obtenerOrganizadosAlfabeticamente(Activity context){
		if(organizadosAlfabeticamente == null){
			cargarPersonasAlfabeticamenteYPorCategoria(context);
		}
		return organizadosAlfabeticamente;
	}

	
	public static Map<String, List<PersonaDTO>> obtenerOrganizadosPorCategoria(Activity context){
		if(organizadosPorCategoria == null){
			cargarPersonasAlfabeticamenteYPorCategoria(context);
		}
		return organizadosPorCategoria;
	}
	
	public static boolean isResetPersonasOrganizadas(){
		return organizadosAlfabeticamente == null;
	}
	
	public static void resetPersonasOrganizadas(){
		organizadosAlfabeticamente = null;
		organizadosPorCategoria = null;
	}
	
	private static void cargarPersonasAlfabeticamenteYPorCategoria(Activity context) {
		// TODO Auto-generated method stub
		List<PersonaDTO> personas = obtenerPersonas(context);
		Iterator<PersonaDTO> it = personas.iterator();
		PersonaDTO per = null;
		organizadosAlfabeticamente = new HashMap<String, List<PersonaDTO>>();
		organizadosPorCategoria = new HashMap<String, List<PersonaDTO>>();
		List<PersonaDTO> listTemp = null;
		String primeraLetra = null;
		String nombreCategoria = null;
        while(it.hasNext()){
        	per = (PersonaDTO) it.next();
        
        	// ORGANIZO ALFABETICAMENTE
        	if(!per.getApellido().equals("")){
            	primeraLetra = per.getApellido().substring(0, 1).toUpperCase();        		
        	}else{
        		primeraLetra = "";
        	}

        	listTemp = new ArrayList<PersonaDTO>();
        	if (organizadosAlfabeticamente.containsKey(primeraLetra)) {
        		listTemp = organizadosAlfabeticamente.get(primeraLetra);
        	}
    		listTemp.add(per);
    		organizadosAlfabeticamente.put(primeraLetra, listTemp);        	

    		// ORGANIZO POR CATEGORIA
    		nombreCategoria = per.getCategoriaNombreRelativo();
        	listTemp = new ArrayList<PersonaDTO>();
        	if (organizadosPorCategoria.containsKey(nombreCategoria)) {
        		listTemp = organizadosPorCategoria.get(nombreCategoria);
        	}
    		listTemp.add(per);
    		organizadosPorCategoria.put(nombreCategoria, listTemp);        	
    		
        
        }

		
		
		
		
		
		
	}

		
	public static DataBaseManager getmDBManager() {
		return mDBManager;
	}

	public static void setmDBManager(DataBaseManager mDBManager) {
		ConstantsAdmin.mDBManager = mDBManager;
	}
	
    public static void inicializarBD(Activity act){
    	if(mDBManager == null){
    		mDBManager = new DataBaseManager(act);	
    	}
    	mDBManager.open();
    }
    
    public static void abrirCursor(Cursor cur){
    	if(cur.isClosed()){
    	//	cur.
    	}
    }
    
    public static void finalizarBD(){
    	if(mDBManager != null){
    		mDBManager.close();
    	}
    }
    
    public static void inicializarBD(){
    	mDBManager.upgradeDB();
    }
    
    public static void createBD(){
    	mDBManager.createBD();
    }
    
    
    public static void actualizarTablaCategorias(Activity context){
    	boolean actualizo = mDBManager.actualizarTablaCategoria();
    	List<CategoriaDTO> categorias = null;
    	Iterator<CategoriaDTO> it = null;
    	CategoriaDTO cat = null;
    	if(actualizo){
    	      Cursor cursor = mDBManager.fetchAllCategoriasPorNombre(null);
    	      context.startManagingCursor(cursor);
    	      categorias = ConstantsAdmin.categoriasCursorToDtos(cursor);
    	      cursor.close();
    	      context.stopManagingCursor(cursor);
    	      it = categorias.iterator();
    	      while(it.hasNext()){
	    	      cat = (CategoriaDTO) it.next();
	    	      cat.setTipoDatoExtra(obtenerTipoDatoExtraPorCategoria(cat.getNombreReal(), context));
	    	      mDBManager.actualizarCategoria(cat);
    	      }
    	}
    }

    public static void actualizarTablaContrasenia(){
    	mDBManager.actualizarTablaContrasenia();
    }
    
    
    public static void cargarCategorias(Activity context){
    	long catSize = 0;
    	catSize = mDBManager.tablaCategoriaSize();
    	if(catSize == 0){
    		cargarCategoriasPrivado(context);
    	}
    }
    
    public static void registrarTelefonos(Activity context, List<TipoValorDTO> telefonos){
    	registrarTipoValor(context, telefonos, TABLA_TELEFONOS);
    }
    
    public static void registrarMails(Activity context, List<TipoValorDTO> mails){
    	registrarTipoValor(context, mails, TABLA_EMAILS);
    }
    
    public static void registrarDirecciones(Activity context, List<TipoValorDTO> direcciones){
    	registrarTipoValor(context, direcciones, TABLA_DIRECCIONES);
    }
    
    private static void registrarTipoValor(Activity context, List<TipoValorDTO> valores, String tablaNombre){
    	Iterator<TipoValorDTO> it = valores.iterator();
    	TipoValorDTO tv = null;
    	inicializarBD(context);
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		mDBManager.createTipoValor(tv, tablaNombre);
    	}
    	finalizarBD();
    }
    
    public static long tablaPreferidosSize(Activity context){
    	long valor = 0;
    	inicializarBD(context);
    	valor = mDBManager.tablaPreferidosSize();
    	finalizarBD();
    	return valor;
    }
    
    private static String obtenerTipoDatoExtraPorCategoria(String categoria, Activity context){
    	String result = null;
    	
    	if(categoria.equals(ConstantsAdmin.CATEGORIA_AMIGOS)){
    		result = context.getString(R.string.hint_lugarOActividad);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_CLIENTES)){
    		result = context.getString(R.string.hint_lugarOActividad);
    	}else if(context.equals(ConstantsAdmin.CATEGORIA_COLEGAS)){
    		result = context.getString(R.string.hint_lugarOActividad);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_COMPANIEROS)){
    		result = context.getString(R.string.hint_lugarOActividad);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_CONOCIDOS)){
    		result = context.getString(R.string.hint_lugarOActividad);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_FAMILIARES)){
    		result = context.getString(R.string.hint_parentesco);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_JEFES)){
    		result = context.getString(R.string.hint_empresa);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_MEDICO)){
    		result = context.getString(R.string.hint_especialidad);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_OTROS)){
    		result = context.getString(R.string.hint_rol);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_PACIENTES)){
    		result = context.getString(R.string.hint_obraSocial);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_PROFESORES)){
    		result = context.getString(R.string.hint_lugarOActividad);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_PROVEEDORES)){
    		result = context.getString(R.string.hint_empresa);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_SOCIOS)){
    		result = context.getString(R.string.hint_empresa);
    	}else if(categoria.equals(ConstantsAdmin.CATEGORIA_VECINOS)){
    		result = context.getString(R.string.hint_zona);
    	}    	
     	
    	return result;
    }
    
	private static void cargarCategoriasPrivado(Activity context){
		CategoriaDTO cat = null;
		String tipoDE = null;
		
		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_AMIGOS, context);
		cat = new CategoriaDTO(0 , ConstantsAdmin.CATEGORIA_AMIGOS, ConstantsAdmin.CATEGORIA_AMIGOS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_CLIENTES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_CLIENTES,ConstantsAdmin.CATEGORIA_CLIENTES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_COMPANIEROS, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_COMPANIEROS, ConstantsAdmin.CATEGORIA_COMPANIEROS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);
		
//		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_CONOCIDOS, ConstantsAdmin.CATEGORIA_CONOCIDOS, 0);
//		mDBManager.crearCategoria(cat);
		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_FAMILIARES, context);		
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_FAMILIARES, ConstantsAdmin.CATEGORIA_FAMILIARES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_JEFES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_JEFES,ConstantsAdmin.CATEGORIA_JEFES,  1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_MEDICO, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_MEDICO, ConstantsAdmin.CATEGORIA_MEDICO, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);
	
		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_OTROS, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_OTROS, ConstantsAdmin.CATEGORIA_OTROS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_PACIENTES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_PACIENTES,ConstantsAdmin.CATEGORIA_PACIENTES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_PROFESORES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_PROFESORES, ConstantsAdmin.CATEGORIA_PROFESORES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_PROVEEDORES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_PROVEEDORES, ConstantsAdmin.CATEGORIA_PROVEEDORES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_SOCIOS, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_SOCIOS, ConstantsAdmin.CATEGORIA_SOCIOS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);
	
		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_VECINOS, context);		
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_VECINOS, ConstantsAdmin.CATEGORIA_VECINOS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

	
	}



	public static final String DATABASE_NAME = "FreshAir_ContactsApp";
	public static final int DATABASE_VERSION = 1;
	public static final String TAG = "DataBaseManager";
	public static final String PERSONA_SELECCIONADA = "personaSeleccionada";
	

	// TABLA: Persona
	public static final int MAX_CONTACTS = 8;
	
	public static final String TABLA_PERSONA = "persona";
	public static final String KEY_APELLIDO = "apellido";
    public static final String KEY_NOMBRES = "nombre";
    public static final String KEY_FECHA_NACIMIENTO = "fechaNacimiento";
	public static final String KEY_TEL_PARTICULAR = "telParticular";
	public static final String KEY_TEL_CELULAR = "telCelular";
	public static final String KEY_TEL_LABORAL = "telLaboral";
	public static final String KEY_EMAIL_PARTICULAR = "emailParticular";
	public static final String KEY_EMAIL_LABORAL = "emailLaboral";
	public static final String KEY_EMAIL_OTRO = "emailOtro";
	public static final String KEY_CATEGORIA = "categoria";
	public static final String KEY_DESCRIPCION = "descripcion";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DIRECCION_PARTICULAR = "direccionParticular";
    public static final String KEY_DIRECCION_LABORAL = "direccionLaboral";
    public static final String KEY_DATO_EXTRA = "empresa";
    public static final String KEY_NOMBRE_CATEGORIA_RELATIVO = "nombreCategoriaRelativo";

    public static final String CATEGORIA_PACIENTES = "Paciente";
    public static final String CATEGORIA_PROVEEDORES = "Proveedor";
    public static final String CATEGORIA_CLIENTES = "Cliente";
    public static final String CATEGORIA_OTROS = "Otro";
    public static final String CATEGORIA_FAMILIARES = "Familiar";
    public static final String CATEGORIA_SOCIOS= "Socio";
    public static final String CATEGORIA_JEFES = "Jefe";
    public static final String CATEGORIA_AMIGOS = "Amigo";
    public static final String CATEGORIA_COMPANIEROS = "Companero";
    public static final String CATEGORIA_CONOCIDOS = "Conocido";
    public static final String CATEGORIA_PROFESORES = "Profesor";
    public static final String CATEGORIA_VECINOS = "Vecino";
    public static final String CATEGORIA_COLEGAS = "Colega";
    public static final String CATEGORIA_MEDICO = "Doctor";
    public static final String CATEGORIA_TODOS = "TODAS";
    
    // TABLA: Categoria
    
    public static final String TABLA_CATEGORIA = "categorias";
    public static final String CATEGORIA_SELECCIONADA = "categoriaSeleccionada";
    public static final String TABLA_CATEGORIA_PERSONALES = "categoriasPersonales";
    public static final String KEY_NOMBRE_CATEGORIA = "nombreCategoria";
    public static final String KEY_CATEGORIA_ACTIVA = "categoriaActiva";
    public static final String KEY_CATEGORIA_TIPO_DATO_EXTRA = "tipoDatoExtra";
    
    public static final String TABLA_PREFERIDOS = "preferidos";
    
    
    
    public static final String smesEne="Enero";
    public static final String smesFeb="Febrero";
    public static final String smesMar="Marzo";
    public static final String smesAbr="Abril";
    public static final String smesMay="Mayo";
    public static final String smesJun="Junio";
    public static final String smesJul="Julio";
    public static final String smesAgo="Agosto";
    public static final String smesSep="Septiembre";
    public static final String smesOct="Octubre";
    public static final String smesNov="Noviembre";
    public static final String smesDic="Diciembre";

    
    
    // CODIGOS DE REQUERIMIENTOS DE INTENTS
    
    public static final int ACTIVITY_EJECUTAR_LISTADO_PERSONAS=1;
    public static final int ACTIVITY_EJECUTAR_DETALLE_PERSONA=3;
    public static final int ACTIVITY_EJECUTAR_ALTA_PERSONA=2;
    public static final int ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS=4;
    public static final int ACTIVITY_EJECUTAR_IMPORTAR_CONTACTOS=5;
    public static final int ACTIVITY_EJECUTAR_ELIMINAR_CONTACTO=6;
    public static final int ACTIVITY_LLAMAR_CONTACTO = 7;
    public static final int ACTIVITY_EJECUTAR_ABOUT_ME = 8;
    public static final int ACTIVITY_EJECUTAR_MIS_CATEGORIAS = 9;
    public static final int ACTIVITY_EJECUTAR_ALTA_CATEGORIA=10;
    public static final int ACTIVITY_EJECUTAR_EDITAR_CATEGORIA=11;
    public static final int ACTIVITY_EJECUTAR_ELIMINAR_CATEGORIA=12;
    public static final int ACTIVITY_EJECUTAR_EDICION_TELEFONO=13;
    public static final int ACTIVITY_EJECUTAR_ALTA_TELEFONO=14;    
    public static final int ACTIVITY_EJECUTAR_EDICION_EMAIL=15;
    public static final int ACTIVITY_EJECUTAR_ALTA_EMAIL=16; 
    public static final int ACTIVITY_EJECUTAR_EDICION_DIRECCION=17;
    public static final int ACTIVITY_EJECUTAR_ALTA_DIRECCION=18; 
    
    public static final String TIPO_DATO_EXTRA_ACTIVIDAD = "ACTIVIDAD";
    public static final String TIPO_DATO_EXTRA_PARENTESCO = "PARENTESCO";
    public static final String TIPO_DATO_EXTRA_EMPRESA = "EMPRESA";
    public static final String TIPO_DATO_EXTRA_OBRA_SOCIAL = "OBRA SOCIAL";
    public static final String TIPO_DATO_EXTRA_OBRA_ZONA = "ZONA";
    public static final String TIPO_DATO_EXTRA_OBRA_ESPECIALIDAD = "ESPECIALIDAD";
    public static final String TIPO_DATO_EXTRA_ROL = "ROL";
    
    public static final String SEPARADOR_FECHA = "-";
    public static final String LANG_ESPANOL = "es";
	public static final String TABLA_TELEFONOS = "telefonos";
	public static final String TABLA_EMAILS = "emails";
	public static final String TABLA_DIRECCIONES = "direcciones";
	public static final String TABLA_CONFIGURACION = "configuraciones";
	public static final String KEY_VALOR = "valor";
	public static final String KEY_TIPO = "tipo";
	public static final String KEY_ID_PERSONA = "idPersona";
	public static final String KEY_ORDENADO_POR_CATEGORIA = "ordenadoPorCateg";
	public static final String KEY_ESTAN_DETALLADOS = "estanDetallados";
	public static final String KEY_LISTA_EXPANDIDA = "listaExpandida";
	public static final String KEY_MUESTRA_PREFERIDOS = "muestraPreferidos";
	
	public static final String TIPO_ELEMENTO = "tipoElemento";
	public static final String TIPO_TELEFONO = "tipoTelefono";
	public static final String TIPO_EMAIL = "tipoEmail";
	public static final String TIPO_DIRECCION = "tipoDireccion";
	
	
    
    public static List<CategoriaDTO> categoriasCursorToDtos(Cursor cursor){
    	ArrayList<CategoriaDTO> result = new ArrayList<CategoriaDTO>();
    	CategoriaDTO cat = null; 
    	
    	String catName = null;
    	String tipoDatoExtra = null;
    	long catId = 0;
    	long catActiva = 0;
    	
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
      	  catName = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA));
      	  catId = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
      	  catActiva = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_ACTIVA));
      	  tipoDatoExtra = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA));
      	  cat = new CategoriaDTO(catId, catName, catName, catActiva, tipoDatoExtra);
      	  cat.setCategoriaPersonal(false);
      	  result.add(cat);
      	  cursor.moveToNext();
        }
        
        return result;

    }   
    
    public static List<CategoriaDTO> categoriasProtegidasCursorToDtos(Cursor cursor){
    	ArrayList<CategoriaDTO> result = new ArrayList<CategoriaDTO>();
    	CategoriaDTO cat = null; 
    	
    	String catName = null;
    	long catId = 0;
    	
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
      	  catName = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA));
      	  catId = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
       	  cat = new CategoriaDTO(catId, catName, catName, 0, "");
      	  result.add(cat);
      	  cursor.moveToNext();
        }
        
        return result;

    }   
    
    public static List<CategoriaDTO> categoriasPersonalesCursorToDtos(Cursor cursor){
    	ArrayList<CategoriaDTO> result = new ArrayList<CategoriaDTO>();
    	CategoriaDTO cat = null; 
    	
    	String catName = null;
    	String tipoDatoExtra = null;
    	long catId = 0;
    	long catActiva = 0;
    	
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
      	  catName = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA));
      	  catId = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
      	  catActiva = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_ACTIVA));
      	  tipoDatoExtra = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA));
      	  cat = new CategoriaDTO(catId, catName, catName, catActiva, tipoDatoExtra);
      	  cat.setCategoriaPersonal(true);
      	  result.add(cat);
      	  cursor.moveToNext();
        }
        
        return result;

    }   
    
    public static PersonaDTO obtenerPersonaId(Activity context, long idPer){
    	PersonaDTO per = new PersonaDTO();
        per.setId(new Long(idPer));
    	Cursor perCursor = null;
		inicializarBD(context);
    	perCursor = mDBManager.fetchPersonaPorId(idPer);
    	context.startManagingCursor(perCursor);
    	per = cursorToPersonaDto(perCursor);
    	perCursor.close();
    	context.stopManagingCursor(perCursor);
    	finalizarBD();
    	return per;
    }
    
    public static CategoriaDTO obtenerCategoriaPersonalId(Activity context, long idCat){
    	CategoriaDTO cat = new CategoriaDTO();
    	cat.setId(new Long(idCat));
    	Cursor catCursor = null;
		inicializarBD(context);
    	catCursor = mDBManager.fetchCategoriaPersonalPorId(idCat);
    	context.startManagingCursor(catCursor);
    	cat = cursorToCategoriaDto(catCursor);
    	catCursor.close();
    	context.stopManagingCursor(catCursor);
    	finalizarBD();
    	return cat;
    }
    
    
    public static List<TipoValorDTO> cursorToTipoValorDtos(Cursor cursor){
    	ArrayList<TipoValorDTO> result = new ArrayList<TipoValorDTO>();
    	TipoValorDTO tv = null; 

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
        	tv = cursorToTipoValorDto(cursor);
      	  	result.add(tv);
      	  	cursor.moveToNext();
        }
        
        return result;

    }   
        

    
    public static List<TipoValorDTO> obtenerTelefonosIdPersona(Activity context, long id){
    	return obtenerTipoValorDtosIdPersona(context, id, ConstantsAdmin.TABLA_TELEFONOS);
    }
    
    public static List<TipoValorDTO> obtenerEmailsIdPersona(Activity context, long id){
    	return obtenerTipoValorDtosIdPersona(context, id, ConstantsAdmin.TABLA_EMAILS);
    }

    public static List<TipoValorDTO> obtenerDireccionesIdPersona(Activity context, long id){
    	return obtenerTipoValorDtosIdPersona(context, id, ConstantsAdmin.TABLA_DIRECCIONES);
    }
    
    public static List<TipoValorDTO> obtenerTipoValorDtosIdPersona(Activity context, long id, String tablaName){
    	Cursor cur = null;
    	List<TipoValorDTO> result = null;
		inicializarBD(context);
    	cur = mDBManager.fetchTipoValorPorIdPersona(id, tablaName);
    	context.startManagingCursor(cur);
    	result = cursorToTipoValorDtos(cur);
    	cur.close();
    	context.stopManagingCursor(cur);
    	finalizarBD();
    	return result;
    }

    
    private static CategoriaDTO cursorToCategoriaDto(Cursor catCursor){
    	String temp = null;
    	CategoriaDTO cat = new CategoriaDTO();
    	if(catCursor != null){
	  //  	context.stopManagingCursor(catCursor);
	     //   context.startManagingCursor(catCursor);
	        temp = catCursor.getString(catCursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        cat.setId(Long.valueOf(temp));
	        temp = catCursor.getString(catCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA));
	        cat.setNombreReal(temp);
	        temp = catCursor.getString(catCursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_ACTIVA));
	       	cat.setActiva(Long.valueOf(temp));
	        temp = catCursor.getString(catCursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA));
	        cat.setTipoDatoExtra(temp);
    	}
    	return cat;
    }

    
    private static TipoValorDTO cursorToTipoValorDto(Cursor cur){
    	String temp = null;
    	TipoValorDTO tv = new TipoValorDTO();
    	if(cur != null){
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        tv.setId(Long.valueOf(temp));
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_TIPO));
	        tv.setTipo(temp);
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_VALOR));
	       	tv.setValor(temp);
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_ID_PERSONA));
	        tv.setIdPersona(temp);
    	}
    	return tv;
    }    

    private static Long cursorToPreferido(Cursor cur){
    	String temp = null;
    	Long id = null;
    	if(cur != null){
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        id = new Long(temp);
    	}
    	return id;
    }     
    
    private static PersonaDTO cursorToPersonaDto(Cursor perCursor){
    	String temp = null;
    	PersonaDTO per = new PersonaDTO();
    	if(perCursor != null){
	 //   	context.stopManagingCursor(perCursor);
	//        context.startManagingCursor(perCursor);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        per.setId(Long.valueOf(temp));
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_APELLIDO));
	        per.setApellido(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRES));
	        per.setNombres(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_TEL_PARTICULAR));
	        per.setTelParticular(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_TEL_CELULAR));
	        per.setCelular(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_TEL_LABORAL));
	        per.setTelLaboral(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_EMAIL_PARTICULAR));
	        per.setEmailParticular(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_EMAIL_LABORAL));
	        per.setEmailLaboral(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_EMAIL_OTRO));
	        per.setEmailOtro(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DIRECCION_PARTICULAR));
	        per.setDireccionParticular(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DIRECCION_LABORAL));
	        per.setDireccionLaboral(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_FECHA_NACIMIENTO));
	        per.setFechaNacimiento(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA)); 
	        per.setCategoriaId(Integer.valueOf(temp));
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA)); 
	        per.setCategoriaNombre(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO)); 
	        per.setCategoriaNombreRelativo(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DATO_EXTRA));
	        per.setDatoExtra(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DESCRIPCION));
	        per.setDescripcion(temp);
    	}
    	return per;
    }
    
    
    public static PersonaDTO obtenerPersonaConNombreYApellido(String name, String apellido, Activity context){
    	Cursor cursor = null;
    	PersonaDTO per = null;
    	cursor = ConstantsAdmin.mDBManager.fetchPersonaPorNombreYApellido(name, apellido);
    	if(cursor != null){
    		context.startManagingCursor(cursor);
    		if(cursor.getCount() > 0){
        		per = cursorToPersonaDto(cursor);
    		}
    		cursor.close();
    		context.stopManagingCursor(cursor);
    	}
    	if(per == null){
        	cursor = ConstantsAdmin.mDBManager.fetchPersonaPorNombreYApellido(apellido, name);
        	if(cursor != null){
        		context.startManagingCursor(cursor);
        		if(cursor.getCount() > 0){
            		per = cursorToPersonaDto(cursor);
        		}
        		cursor.close();
        		context.stopManagingCursor(cursor);
        	}
    		
    	}
    	return per;
    }

    public static String obtenerNombreCategoria(String nCat, Context context){
    	String nombreCat = null;
     	if(nCat.equals(ConstantsAdmin.CATEGORIA_AMIGOS))
    		nombreCat = context.getString(R.string.cat_Amigo);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_CLIENTES))
    		nombreCat = context.getString(R.string.cat_Cliente);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_COLEGAS))
    		nombreCat = context.getString(R.string.cat_Colega);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_COMPANIEROS))
    		nombreCat = context.getString(R.string.cat_Companiero);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_CONOCIDOS))
    		nombreCat = context.getString(R.string.cat_Conocido);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_FAMILIARES))
    		nombreCat = context.getString(R.string.cat_Familiar);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_JEFES))
    		nombreCat = context.getString(R.string.cat_Jefe);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_MEDICO))
    		nombreCat = context.getString(R.string.cat_Medico);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_OTROS))
    		nombreCat = context.getString(R.string.cat_Otro);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_PACIENTES))
    		nombreCat = context.getString(R.string.cat_Paciente);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_PROFESORES))
    		nombreCat = context.getString(R.string.cat_Profesor);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_PROVEEDORES))
    		nombreCat = context.getString(R.string.cat_Proveedor);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_SOCIOS))
    		nombreCat = context.getString(R.string.cat_Socio);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_VECINOS))
    		nombreCat = context.getString(R.string.cat_Vecino);
    	else if(nCat.equals(ConstantsAdmin.CATEGORIA_TODOS))
    		nombreCat = context.getString(R.string.cat_TODAS);
    	return nombreCat;
    }

    public static void mostrarMensaje(Context context, String message){
    	Toast t = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
		t.show();    	
    }
    
    public static TipoValorDTO tipoValorSeleccionado = null;
    public static TipoValorDTO tipoValorAnteriorSeleccionado = null;
    public static ArrayList<String> tiposValores = null;
	public static PersonaDTO personaSeleccionada = null;
	
	public static ArrayList<TipoValorDTO> telefonosARegistrar = null;
	public static ArrayList<TipoValorDTO> mailsARegistrar = null;
	public static ArrayList<TipoValorDTO> direccionesARegistrar = null;

    public static Asociacion comprobarSDCard(Activity context){
    	Asociacion map = null;
        String auxSDCardStatus = Environment.getExternalStorageState();
        boolean sePuede = false;
        String msg = null;

        if (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED))
            sePuede = true;
        else if (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            msg = context.getString(R.string.mensaje_error_tarjeta_solo_lectura);
            sePuede = false;
        }
        else if(auxSDCardStatus.equals(Environment.MEDIA_NOFS)){
        	msg = context.getString(R.string.mensaje_error_tarjeta_no_formato);
            sePuede = false;
        }
        else if(auxSDCardStatus.equals(Environment.MEDIA_REMOVED)){
            msg =  context.getString(R.string.mensaje_error_tarjeta_no_montada);
            sePuede = false;
        }
        else if(auxSDCardStatus.equals(Environment.MEDIA_SHARED)){
            msg = context.getString(R.string.mensaje_error_tarjeta_shared);
            sePuede = false;
        }
        else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTABLE)){
            msg = context.getString(R.string.mensaje_error_tarjeta_unmountable);
            sePuede = false;
        }
        else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTED)){
            msg = context.getString(R.string.mensaje_error_tarjeta_unmounted);
            sePuede = false;
        }
        map = new Asociacion(sePuede, msg);
        
        return map;
    }
    
    public static String mensaje = null;
    public static String folderCSV = "KN-Contacts";
    private static String fileCSV = "kncontacts.csv";
    private static String fileEsteticoCSV = "kncontactsExcel.csv";
    
    public static void importarCSV(Activity context){
        String body = null;
        File file = null;
        mensaje = context.getString(R.string.error_importar_csv);
        try {
        	file = obtenerFileCSV(context);
        	if(file != null){
                if(file.getName().equals(fileCSV)){
                	body = obtenerContenidoArchivo(file, context);
                	procesarStringDatos(body, context);
                	mensaje = context.getString(R.string.mensaje_exito_importar_csv);
                }
            }
		} catch (Exception e) {
			mensaje = context.getString(R.string.error_importar_csv);

		}
    }
    
    public static void procesarStringDatos(String body, Activity context){

    	String[] lineas = body.split(ENTER);
    	int i = 0;
    	String linea = null;
    	String[] campos = null;
    	ArrayList<String> lista = null;
    	HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
    	while(i < lineas.length){
    		linea = lineas[i];
    		campos = linea.split(PUNTO_COMA);
    		lista = map.get(campos[0]);
    		if(lista == null){
    			lista = new ArrayList<String>();
    			map.put(campos[0], lista);
    		}
    		lista.add(linea);
    		i++;
    	}
    	List<PersonaDTO> personas = procesarPersonas(map.get(HEAD_PERSONA));
    	List<CategoriaDTO> categorias = procesarCategorias(map.get(HEAD_CATEGORIA));
    	List<CategoriaDTO> categoriasPersonales = procesarCategoriasPersonales(map.get(HEAD_CATEGORIA_PERSONAL));
    	List<Long> preferidos = procesarPreferidos(map.get(HEAD_PREFERIDO));
    	List<CategoriaDTO> categoriasProtegidas = procesarCategoriasProtegidas(map.get(HEAD_CATEGORIA_PROTEGIDA));
    	ContraseniaDTO pass = procesarContrasenia(map.get(HEAD_CONTRASENIA));
    	almacenarDatos(context, personas, categorias, categoriasPersonales, preferidos, categoriasProtegidas, pass);
    	
    }
    
    private static void almacenarDatos(Activity context, List<PersonaDTO> personas,List<CategoriaDTO> categorias, List<CategoriaDTO> categoriasPersonales,List<Long> preferidos, List<CategoriaDTO> catProtegidas, ContraseniaDTO pass){
    	PersonaDTO per = null;
    	CategoriaDTO cat = null;
    	Long pref = null;
    	TipoValorDTO tv = null;
    	
    	Iterator<PersonaDTO> itPers = null;
    	Iterator<CategoriaDTO> itCat = null;
    	Iterator<Long> itPref = null;
    	Iterator<TipoValorDTO> itTv = null;
    	
    	itPers = personas.iterator();
    	itCat = categorias.iterator();
    	itPref = preferidos.iterator();
    	itTv = telefonosARegistrar.iterator();
    	
    	inicializarBD(context);
    	mDBManager.deleteAll();
    	while(itPers.hasNext()){
    		per = (PersonaDTO) itPers.next();
    		mDBManager.createPersona(per, true);
    	}
    	while(itCat.hasNext()){
    		cat = (CategoriaDTO) itCat.next();
    		mDBManager.crearCategoria(cat, true);
    	}
    	itCat = categoriasPersonales.iterator();
    	while(itCat.hasNext()){
    		cat = (CategoriaDTO) itCat.next();
    		mDBManager.crearCategoriaPersonal(cat, null, true);
    	}   
    	
    	itCat = catProtegidas.iterator();
    	categoriasProtegidas = new ArrayList<CategoriaDTO>();
    	while(itCat.hasNext()){
    		cat = (CategoriaDTO) itCat.next();
    		mDBManager.crearCategoriaProtegida(cat);
    		categoriasProtegidas.add(cat);
    		
    	}  
    	while(itPref.hasNext()){
    		pref = (Long) itPref.next();
    		mDBManager.crearPreferido(pref);
    	}   
    	while(itTv.hasNext()){
    		tv = (TipoValorDTO) itTv.next();
    		mDBManager.createTelefono(tv);
    	} 
    	itTv = mailsARegistrar.iterator();
    	while(itTv.hasNext()){
    		tv = (TipoValorDTO) itTv.next();
    		mDBManager.createEmail(tv);
    	}     	
    	itTv = direccionesARegistrar.iterator();
    	while(itTv.hasNext()){
    		tv = (TipoValorDTO) itTv.next();
    		mDBManager.createDireccion(tv);
    	}     	
    	
    	contrasenia = pass;
    	if(pass.getId() != -1){
    		pass.setId(-1);
    		long id = mDBManager.crearContrasenia(pass);
    		contrasenia.setId(id);
    	}
    	finalizarBD();
    	telefonosARegistrar = null;
    	mailsARegistrar = null;
    	direccionesARegistrar = null;
    	
    }
    
    private static List<PersonaDTO> procesarPersonas(ArrayList<String> lista){
    	String linea = null;
    	List<PersonaDTO> resultado = new ArrayList<PersonaDTO>();
    	String[] campos = null;
    	String temp = null;
    	Iterator<String> it = lista.iterator();
    	PersonaDTO per = null;
    	int i = 0;
    	String[] par = null;
    	TipoValorDTO tv = null;
    	telefonosARegistrar = new ArrayList<TipoValorDTO>();
    	mailsARegistrar = new ArrayList<TipoValorDTO>();
    	direccionesARegistrar = new ArrayList<TipoValorDTO>();
    	while(it.hasNext()){
    		linea = (String) it.next();
    		campos = linea.split(PUNTO_COMA);
    		per = new PersonaDTO();
    		per.setId(new Long(campos[1]));
    		per.setApellido(campos[2]);
    		if(!campos[3].equals(CAMPO_NULO)){
    			per.setNombres(campos[3]);
    		}
    		if(!campos[4].equals(CAMPO_NULO)){
    			per.setFechaNacimiento(campos[4]);
    		}
    		
    		//PROCESO LOS TELEFONOS
    		temp = campos[6];
    		i = 6;
    		while(!temp.equals(FIN)){
    			par = campos[i].split(SEPARACION_ATRIBUTO);
    			if(par[0].equals(PARTICULAR)){
    				per.setTelParticular(par[1]);
    			}else if(par[0].equals(LABORAL)){
    				per.setTelLaboral(par[1]);
    			}else if(par[0].equals(MOVIL)){
    				per.setCelular(par[1]);
    			}else{
    				tv = new TipoValorDTO();
    				tv.setIdPersona(String.valueOf(per.getId()));
    				tv.setTipo(par[0]);
    				tv.setValor(par[1]);
    				telefonosARegistrar.add(tv);
    			}
    			
    			i++;
    			temp = campos[i];
    		}
    		
    		//PROCESO LOS MAILS
    		
    		i++;
    		i++;
    		temp = campos[i];
    		while(!temp.equals(FIN)){
    			par = campos[i].split(SEPARACION_ATRIBUTO);
    			if(par[0].equals(PARTICULAR)){
    				per.setEmailParticular(par[1]);
    			}else if(par[0].equals(LABORAL)){
    				per.setEmailLaboral(par[1]);
    			}else if(par[0].equals(OTRO)){
    				per.setEmailOtro(par[1]);
    			}else{
    				tv = new TipoValorDTO();
    				tv.setIdPersona(String.valueOf(per.getId()));
    				tv.setTipo(par[0]);
    				tv.setValor(par[1]);
    				mailsARegistrar.add(tv);
    			}
    			
    			i++;
    			temp = campos[i];
    		}
    		
    		//PROCESO LAS DIRECCIONES
    		
    		i++;
    		i++;
    		temp = campos[i];
    		while(!temp.equals(FIN)){
    			par = campos[i].split(SEPARACION_ATRIBUTO);
    			if(par[0].equals(PARTICULAR)){
    				per.setDireccionParticular(par[1]);
    			}else if(par[0].equals(LABORAL)){
    				per.setDireccionLaboral(par[1]);
    			}else{
    				tv = new TipoValorDTO();
    				tv.setIdPersona(String.valueOf(per.getId()));
    				tv.setTipo(par[0]);
    				tv.setValor(par[1]);
    				direccionesARegistrar.add(tv);
    			}
    			
    			i++;
    			temp = campos[i];
    		}
    		//PROCESO LOS DATOS POR CATEGORIAS
    		i++;
    		per.setCategoriaId(new Long(campos[i]));
    		i++;
    		per.setCategoriaNombre(campos[i]);
    		i++;
    		per.setCategoriaNombreRelativo(campos[i]);
    		i++;
    		temp = campos[i];
    		if(!temp.equals(CAMPO_NULO)){
    			per.setDatoExtra(temp);
    		}
    		i++;
    		temp = campos[i];
    		if(!temp.equals(CAMPO_NULO)){
    			per.setDescripcion(temp);
    		}
    		resultado.add(per);
    		    		
    	}
    	return resultado;
    	
    }

    private static List<CategoriaDTO> procesarCategorias(ArrayList<String> lista){
    	String linea = null;
    	List<CategoriaDTO> resultado = new ArrayList<CategoriaDTO>();
    	String[] campos = null;
    	if(lista != null){
        	Iterator<String> it = lista.iterator();
        	CategoriaDTO cat = null;
        	while(it.hasNext()){
        		linea = (String) it.next();
        		campos = linea.split(PUNTO_COMA);
        		cat = new CategoriaDTO();
        		cat.setId(new Long(campos[1]));  
        		cat.setNombreReal(campos[2]);
        		cat.setActiva(new Long(campos[3]));
        		cat.setTipoDatoExtra(campos[4]);
        		resultado.add(cat);
        	}   		
    	}

    	return resultado;
    }

    private static List<CategoriaDTO> procesarCategoriasProtegidas(ArrayList<String> lista){
    	String linea = null;
    	List<CategoriaDTO> resultado = new ArrayList<CategoriaDTO>();
    	String[] campos = null;
    	if(lista != null){
        	Iterator<String> it = lista.iterator();
        	CategoriaDTO cat = null;
        	while(it.hasNext()){
        		linea = (String) it.next();
        		campos = linea.split(PUNTO_COMA);
        		cat = new CategoriaDTO();
        		cat.setId(new Long(campos[1]));  
        		cat.setNombreReal(campos[2]);
        		resultado.add(cat);
        	}   		
    	}

    	return resultado;
    }
    
    private static List<CategoriaDTO> procesarCategoriasPersonales(ArrayList<String> lista){
    	List<CategoriaDTO> result = procesarCategorias(lista);
    	Iterator<CategoriaDTO> it = result.iterator();
    	CategoriaDTO cat = null;
    	while(it.hasNext()){
    		cat = (CategoriaDTO) it.next();
    		cat.setCategoriaPersonal(true);
    	}
    	return result;
    }
    


    private static List<Long> procesarPreferidos(ArrayList<String> lista){
    	String linea = null;
    	List<Long> resultado = new ArrayList<Long>();
    	String[] campos = null; 
    	if(lista != null){
        	Iterator<String> it = lista.iterator();
        	Long id = null;
        	while(it.hasNext()){
        		linea = (String) it.next();
        		campos = linea.split(PUNTO_COMA);
        		id = new Long(campos[1]);
        		resultado.add(id);
        	}    		
    	}

    	return resultado;
    }
    
    private static ContraseniaDTO procesarContrasenia(ArrayList<String> lista){
    	String linea = null;
    	ContraseniaDTO pass = new ContraseniaDTO();
    	String[] campos = null;
    	if(lista != null){
        	Iterator<String> it = lista.iterator();
        	Long id = null;
        	if(it.hasNext()){
        		linea = (String) it.next();
        		campos = linea.split(PUNTO_COMA);
        		id = new Long(campos[1]);
        		pass.setId(id);
        		pass.setContrasenia(campos[2]);

         	}    		
    	}

    	return pass;
    }
    
    private static String obtenerContenidoArchivo(File file, Activity context)throws FileNotFoundException, IOException{
        // ACA DEBERIA CARGAR EL CONTENIDO DEL ARCHIVO PASADO COMO PARAMETRO, HACER LOS CONTROLES DE LECTURA
    	String line = null;
    	Asociacion canStore = comprobarSDCard(context);
    	boolean boolValue = (Boolean)canStore.getKey();
    	String msg = (String) canStore.getValue();
    	String result = "";
    	if(boolValue){
    		BufferedReader input =  new BufferedReader(new FileReader(file));
    		line = input.readLine();
    		while(line != null){
    			if(!line.equals("")){
    				result = result + line + ENTER;
    			}
    			
    			line = input.readLine();
    		}
    		
    	}else{
			  mensaje = msg;
    	}
    	
        return result;

    }    
    
    public static void exportarCSV(Activity context){
    	Asociacion canStore = null;
    	Boolean boolValue = true;
    	String msg = null;
    	String body = null;
        try
        {	
        	canStore = comprobarSDCard(context);
     		boolValue = (Boolean)canStore.getKey();
     		msg = (String) canStore.getValue();
     		if(boolValue){
     			body = obtenerCSVdeContactos(context);
     			almacenarArchivo(folderCSV, fileCSV , body);
     			mensaje = context.getString(R.string.mensaje_exito_exportar_csv);
     		}else{
    			mensaje = msg;
     		}
     		
	  	  }catch (Exception e) {
	  		  mensaje = context.getString(R.string.error_exportar_csv);
	  	  }    	
    }

    public static void exportarCSVEstetico(Activity context, String separador, List<CategoriaDTO> categoriasProtegidas){
    	Asociacion canStore = null;
    	Boolean boolValue = true;
    	String msg = null;
    	String body = null;
        try
        {	
        	canStore = comprobarSDCard(context);
     		boolValue = (Boolean)canStore.getKey();
     		msg = (String) canStore.getValue();
     		if(boolValue){
     			body = obtenerCSVdeContactosEstetico(context, separador, categoriasProtegidas);
     			almacenarArchivo(folderCSV, fileEsteticoCSV , body);
     			mensaje = context.getString(R.string.mensaje_exito_exportar_csv);
     		}else{
    			mensaje = msg;
     		}
     		
	  	  }catch (Exception e) {
	  		  mensaje = context.getString(R.string.error_exportar_csv);
	  	  }    	
    }
    
    
    public static void almacenarArchivo(String nombreDirectorio, String nombreArchivo, String body) throws IOException, FileNotFoundException{
    	String path = obtenerPath(nombreDirectorio);

	    File dir = new File(path);
	    dir.mkdir();
      
	    File file = new File(dir.getPath(), nombreArchivo);
	    if(!file.exists()){
	    	file.createNewFile();
	    }
	    PrintWriter writer = new PrintWriter(file);
	    writer.append(body);
	    writer.flush();
	    writer.close();
    }
  
    public static String obtenerPath(String nombreDirectorio){
    	String path = Environment.getExternalStorageDirectory().toString();
    	return path + File.separator + nombreDirectorio;
    }

    
    private static String obtenerCSVdeContactosEstetico(Activity context, String separador, List<CategoriaDTO> categoriasProtegidas){
    	String result = "";
    	PersonaDTO per = null;
    	
    	// RECUPERO PERSONAS
    	List<PersonaDTO> personas = obtenerPersonas(context, categoriasProtegidas);
    	Iterator<PersonaDTO> itPer = personas.iterator();
    	while(itPer.hasNext()){
    		per = (PersonaDTO) itPer.next();
    	//	result = result + obtenerStringPersona(per, context);
    		result = result + obtenerStringEsteticoPersona(per, context, separador);

    	}
    	return result;
    }
    
    private static String obtenerCSVdeContactos(Activity context){
    	String result = "";
    	PersonaDTO per = null;
    	CategoriaDTO cat = null;
    	
    	
    	// RECUPERO PERSONAS
    	List<PersonaDTO> personas = obtenerPersonas(context);
    	Iterator<PersonaDTO> itPer = personas.iterator();
    	while(itPer.hasNext()){
    		per = (PersonaDTO) itPer.next();
    		result = result + obtenerStringPersona(per, context);
    	}
    	
    	
    	// RECUPERO CATEGORIAS FIJAS
    	List<CategoriaDTO> categorias = obtenerCategorias(context);
    	Iterator<CategoriaDTO> itCat = categorias.iterator();
    	while(itCat.hasNext()){
    		cat = (CategoriaDTO) itCat.next();
    		result = result + obtenerStringCategoria(cat, HEAD_CATEGORIA);
    	}
    	
    	
    	// RECUPERO CATEGORIAS PERSONALES
    	categorias = obtenerCategoriasPersonales(context);
    	itCat = categorias.iterator();
    	while(itCat.hasNext()){
    		cat = (CategoriaDTO) itCat.next();
    		result = result + obtenerStringCategoria(cat, HEAD_CATEGORIA_PERSONAL);
    	}
    	
    	
    	// RECUPERO PREFERIDOS
    	Long idPref = null;
    	List<Long> preferidos = obtenerPreferidos(context);
    	Iterator<Long> itPref = preferidos.iterator();
    	while(itPref.hasNext()){
    		idPref = (Long) itPref.next();
    		result = result + HEAD_PREFERIDO + PUNTO_COMA +idPref + ENTER;
    	}
    	
    	
    	// RECUPERO CATEGORIAS PROTEGIDAS
    //	categorias = obtenerCategoriasProtegidas(context);
    	categorias = categoriasProtegidas;
    	itCat = categorias.iterator();
    	while(itCat.hasNext()){
    		cat = (CategoriaDTO) itCat.next();
    		result = result + obtenerStringCategoriaProtegida(cat, HEAD_CATEGORIA_PROTEGIDA);
    	}
    	result = result + obtenerStringContrasenia();
    	
    	return result;
    	
    }
    
    private static String obtenerStringCategoria(CategoriaDTO cat, String header){
    	String result = "";
    	result = header + PUNTO_COMA + cat.getId() + PUNTO_COMA + cat.getNombreReal() + PUNTO_COMA + cat.getActiva() + PUNTO_COMA + cat.getTipoDatoExtra() + ENTER;
    	return result;
    }
    
    private static String obtenerStringContrasenia(){
    	String result = "";
    	result = HEAD_CONTRASENIA + PUNTO_COMA + contrasenia.getId() + PUNTO_COMA + contrasenia.getContrasenia() + ENTER;
    	return result;
    }
    
    
    private static String obtenerStringCategoriaProtegida(CategoriaDTO cat, String header){
    	String result = "";
    	result = header + PUNTO_COMA + cat.getId() + PUNTO_COMA + cat.getNombreReal() + ENTER;
    	return result;
    }
    
    
    
    private static String obtenerStringPersona(PersonaDTO per, Activity context){
    	String result = "";
    	List<TipoValorDTO> masTVs = null;
    	TipoValorDTO tv = null;
    	Iterator<TipoValorDTO> it = null; 
    	//result = result + SEPARACION_PERSONA + ENTER;
 
    	// DATOS PERSONALES
    	
    	result = result + HEAD_PERSONA + PUNTO_COMA + per.getId() + PUNTO_COMA + per.getApellido() + PUNTO_COMA;
    	if(per.getNombres() != null && !per.getNombres().equals("")){
    		result = result + per.getNombres() + PUNTO_COMA;	
    	}else{
    		result = result + CAMPO_NULO + PUNTO_COMA;
    	}
    	if(per.getFechaNacimiento() != null){
    		result = result + per.getFechaNacimiento() + PUNTO_COMA;	
    	}else{
    		result = result + CAMPO_NULO + PUNTO_COMA;
    	}
    	
    	// TELEFONOS
    	
    	result = result + INICIO + PUNTO_COMA;
    	if(per.getTelParticular() != null){
    		result = result + PARTICULAR + SEPARACION_ATRIBUTO + per.getTelParticular() + PUNTO_COMA;
    	}
    	if(per.getCelular() != null){
    		result = result + MOVIL + SEPARACION_ATRIBUTO + per.getCelular() + PUNTO_COMA;
    	}
    	if(per.getTelLaboral() != null){
    		result = result + LABORAL + SEPARACION_ATRIBUTO + per.getTelLaboral() + PUNTO_COMA;
    	}
    	
    
    	masTVs = obtenerTelefonosIdPersona(context, per.getId());
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		result = result + tv.getTipo() + SEPARACION_ATRIBUTO + tv.getValor() + PUNTO_COMA;
    	}

    	result = result + FIN + PUNTO_COMA;
    	// MAILS
    	
    	result = result + INICIO + PUNTO_COMA;
    	if(per.getEmailParticular() != null){
    		result = result + PARTICULAR + SEPARACION_ATRIBUTO + per.getEmailParticular() + PUNTO_COMA;
    	}
    	if(per.getEmailLaboral() != null){
    		result = result + LABORAL + SEPARACION_ATRIBUTO + per.getEmailLaboral() + PUNTO_COMA;
    	}
    	if(per.getEmailOtro() != null){
    		result = result + OTRO + SEPARACION_ATRIBUTO + per.getEmailOtro() + PUNTO_COMA;
    	}
    	
    	masTVs = obtenerEmailsIdPersona(context, per.getId());
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		result = result + tv.getTipo() + SEPARACION_ATRIBUTO + tv.getValor() + PUNTO_COMA;
    	}
    	
    	result = result + FIN + PUNTO_COMA;
    	
    	// DIRECCIONES
    	
    	result = result + INICIO + PUNTO_COMA;
    	if(per.getDireccionParticular() != null){
    		result = result + PARTICULAR + SEPARACION_ATRIBUTO + per.getDireccionParticular() + PUNTO_COMA;
    	}
    	if(per.getDireccionLaboral() != null){
    		result = result + LABORAL + SEPARACION_ATRIBUTO + per.getDireccionLaboral() + PUNTO_COMA;
    	}
    	
    	masTVs = obtenerDireccionesIdPersona(context, per.getId());
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		result = result + tv.getTipo() + SEPARACION_ATRIBUTO + tv.getValor() + PUNTO_COMA;
    	}
    	
    	result = result + FIN + PUNTO_COMA;
    	
    	// DATOS POR CATEGORIA

    	result = result + per.getCategoriaId() + PUNTO_COMA;
    	result = result + per.getCategoriaNombre() + PUNTO_COMA;
    	result = result + per.getCategoriaNombreRelativo() + PUNTO_COMA;
    	if(per.getDatoExtra()!= null && !per.getDatoExtra().equals("")){
    		result = result + per.getDatoExtra() + PUNTO_COMA;	
    	}else{
    		result = result + CAMPO_NULO + PUNTO_COMA;
    	}
    	if(per.getDescripcion() != null && !per.getDescripcion().equals("")){
    		result = result + per.getDescripcion();
    	}else{
    		result = result + CAMPO_NULO ;
    	}

    	result = result + ENTER;
    	
    	
    	return result;
    	
    }

    private static String obtenerStringEsteticoPersona(PersonaDTO per, Activity context, String separador){
    	String result = "";
    	List<TipoValorDTO> masTVs = null;
    	TipoValorDTO tv = null;
    	Iterator<TipoValorDTO> it = null; 
    	//result = result + SEPARACION_PERSONA + ENTER;
 
    	// DATOS PERSONALES
    	
    	result = result + per.getApellido() + separador;
    	if(per.getNombres() != null && !per.getNombres().equals("")){
    		result = result + per.getNombres() + separador;	
    	}else{
    		result = result + separador;
    	}
    	if(per.getFechaNacimiento() != null){
    		result = result + context.getString(R.string.label_fechaNacimiento) + SEPARACION_ATRIBUTO + per.getFechaNacimiento() + separador;	
    	}else{
    		result = result + context.getString(R.string.label_fechaNacimiento) + SEPARACION_ATRIBUTO + separador;
    	}
    	
    	// DATOS POR CATEGORIA
    	String datosCategoria = per.getCategoriaNombreRelativo();
    	
    	if(per.getDatoExtra()!= null && !per.getDatoExtra().equals("")){
    		datosCategoria = datosCategoria + "-" + per.getDatoExtra();	
    	}
    	if(per.getDescripcion() != null && !per.getDescripcion().equals("")){
    		datosCategoria = datosCategoria + "-" + per.getDescripcion();
    	}
    	result = result + datosCategoria + separador;
    	
    	// TELEFONOS
    	
    	if(per.getTelParticular() != null){
    		result = result + context.getString(R.string.label_telefono) + "-" + context.getString(R.string.hint_particular) + SEPARACION_ATRIBUTO + per.getTelParticular() + PIPE;
    	}
    	if(per.getCelular() != null){
    		result = result + context.getString(R.string.label_telefono) + "-" + context.getString(R.string.hint_telMovil) + SEPARACION_ATRIBUTO + per.getCelular() + PIPE;
    	}
    	if(per.getTelLaboral() != null){
    		result = result + context.getString(R.string.label_telefono) + "-" + context.getString(R.string.hint_laboral) + SEPARACION_ATRIBUTO + per.getTelLaboral() + PIPE;
    	}
    	
    
    	masTVs = obtenerTelefonosIdPersona(context, per.getId());
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		result = result + context.getString(R.string.label_telefono) + "-" +  tv.getTipo() + SEPARACION_ATRIBUTO + tv.getValor() + PIPE;
    	}
    	
    	result = result + separador;

    	// MAILS

    	if(per.getEmailParticular() != null){
    		result = result + context.getString(R.string.label_email) + "-" + context.getString(R.string.hint_particular) + SEPARACION_ATRIBUTO + per.getEmailParticular() + PIPE;
    	}
    	if(per.getEmailLaboral() != null){
    		result = result + context.getString(R.string.label_email) + "-" + context.getString(R.string.hint_laboral) + SEPARACION_ATRIBUTO + per.getEmailLaboral() + PIPE;
    	}
    	if(per.getEmailOtro() != null){
    		result = result + context.getString(R.string.label_email) + "-" + context.getString(R.string.hint_otro) + SEPARACION_ATRIBUTO + per.getEmailOtro() + PIPE;
    	}
    	
    	masTVs = obtenerEmailsIdPersona(context, per.getId());
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		result = result + context.getString(R.string.label_email) + "-" + tv.getTipo() + SEPARACION_ATRIBUTO + tv.getValor() + PIPE;
    	}
    	
    	result = result + separador;
    	
    	// DIRECCIONES
    	
    	if(per.getDireccionParticular() != null){
    		result = result + context.getString(R.string.label_direccion) + "-" + context.getString(R.string.hint_particular) + SEPARACION_ATRIBUTO + per.getDireccionParticular() + PIPE;
    	}
    	if(per.getDireccionLaboral() != null){
    		result = result + context.getString(R.string.label_direccion) + "-" + context.getString(R.string.hint_laboral) + SEPARACION_ATRIBUTO + per.getDireccionLaboral() + PIPE;
    	}
    	
    	masTVs = obtenerDireccionesIdPersona(context, per.getId());
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		result = result + context.getString(R.string.label_direccion) + "-" + tv.getTipo() + SEPARACION_ATRIBUTO + tv.getValor() + PIPE;
    	}
    	
    	result = result + separador;

    	result = result + ENTER;
    	
    	
    	return result;
    	
    }


    private static String obtenerStringEsteticoPersonaParaEnviar(PersonaDTO per, Activity context, String separador){
    	String result = "";
    	List<TipoValorDTO> masTVs = null;
    	TipoValorDTO tv = null;
    	Iterator<TipoValorDTO> it = null; 
    	//result = result + SEPARACION_PERSONA + ENTER;
 
    	// DATOS PERSONALES
    	
    	result = result + "- " + per.getApellido() + " ";
    	if(per.getNombres() != null && !per.getNombres().equals("")){
    		result = result + per.getNombres() + separador;	
    	}
		result = result + separador;
    	// TELEFONOS
    	
    	result = separador + result + context.getResources().getString(R.string.label_telefonos) + separador;
    	
    	result = result + separador;
    	
    	if(per.getTelParticular() != null){
    		result = result + "* " + context.getString(R.string.label_telefono) + "-" + context.getString(R.string.hint_particular) + SEPARACION_ATRIBUTO + per.getTelParticular() + separador;
    	}
    	if(per.getCelular() != null){
    		result = result + "* " + context.getString(R.string.label_telefono) + "-" + context.getString(R.string.hint_telMovil) + SEPARACION_ATRIBUTO + per.getCelular() + separador;
    	}
    	if(per.getTelLaboral() != null){
    		result = result + "* " + context.getString(R.string.label_telefono) + "-" + context.getString(R.string.hint_laboral) + SEPARACION_ATRIBUTO + per.getTelLaboral() + separador;
    	}
    	
    
    	masTVs = obtenerTelefonosIdPersona(context, per.getId());
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		result = result + "* " + context.getString(R.string.label_telefono) + "-" +  tv.getTipo() + SEPARACION_ATRIBUTO + tv.getValor() + separador;
    	}

    	result = result + separador;
    	// MAILS
    	result = separador + result + context.getResources().getString(R.string.label_mail) + separador;

    	result = result + separador;
    	if(per.getEmailParticular() != null){
    		result = result + "* " + context.getString(R.string.label_email) + "-" + context.getString(R.string.hint_particular) + SEPARACION_ATRIBUTO + per.getEmailParticular() + separador;
    	}
    	if(per.getEmailLaboral() != null){
    		result = result + "* " + context.getString(R.string.label_email) + "-" + context.getString(R.string.hint_laboral) + SEPARACION_ATRIBUTO + per.getEmailLaboral() + separador;
    	}
    	if(per.getEmailOtro() != null){
    		result = result + "* " + context.getString(R.string.label_email) + "-" + context.getString(R.string.hint_otro) + SEPARACION_ATRIBUTO + per.getEmailOtro() + separador;
    	}
    	
    	masTVs = obtenerEmailsIdPersona(context, per.getId());
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		result = result + "* " + context.getString(R.string.label_email) + "-" + tv.getTipo() + SEPARACION_ATRIBUTO + tv.getValor() + separador;
    	}
    	
    	result = result + separador;
    	// DIRECCIONES

    	result = separador + result + context.getResources().getString(R.string.label_direcciones) + separador;

    	result = result + separador;
    	if(per.getDireccionParticular() != null){
    		result = result + "* " + context.getString(R.string.label_direccion) + "-" + context.getString(R.string.hint_particular) + SEPARACION_ATRIBUTO + per.getDireccionParticular() + separador;
    	}
    	if(per.getDireccionLaboral() != null){
    		result = result + "* " + context.getString(R.string.label_direccion) + "-" + context.getString(R.string.hint_laboral) + SEPARACION_ATRIBUTO + per.getDireccionLaboral() + separador;
    	}
    	
    	masTVs = obtenerDireccionesIdPersona(context, per.getId());
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = (TipoValorDTO) it.next();
    		result = result + "* " + context.getString(R.string.label_direccion) + "-" + tv.getTipo() + SEPARACION_ATRIBUTO + tv.getValor() + separador;
    	}
    	
    	result = result + separador;

    	result = result + ENTER;
    	
    	
    	return result;
    	
    }


    
    
    private static List<PersonaDTO> obtenerPersonas(Activity context, List<CategoriaDTO> categoriasProtegidas){
    	PersonaDTO per = null;
    	List<PersonaDTO> result = new ArrayList<PersonaDTO>();
    	inicializarBD(context);
    	Cursor cur = mDBManager.fetchAllPersonas(categoriasProtegidas);
    	context.startManagingCursor(cur);
    	cur.moveToFirst();
    	while(!cur.isAfterLast()){
           per = cursorToPersonaDto(cur);
           result.add(per);
           cur.moveToNext();
    	}
    	cur.close();
    	context.stopManagingCursor(cur);
    	finalizarBD();
    	return result;
    }
    
    private static List<PersonaDTO> obtenerPersonas(Activity context){
    	PersonaDTO per = null;
    	List<PersonaDTO> result = new ArrayList<PersonaDTO>();
    	inicializarBD(context);
    	Cursor cur = mDBManager.fetchAllPersonas(categoriasProtegidas);
    	context.startManagingCursor(cur);
    	cur.moveToFirst();
    	while(!cur.isAfterLast()){
           per = cursorToPersonaDto(cur);
           result.add(per);
           cur.moveToNext();
    	}
    	cur.close();
    	context.stopManagingCursor(cur);
    	finalizarBD();
    	return result;
    }
    
    private static List<CategoriaDTO> obtenerCategorias(Activity context){
    	CategoriaDTO cat = null;
    	List<CategoriaDTO> result = new ArrayList<CategoriaDTO>();
    	inicializarBD(context);
    	Cursor cur = mDBManager.fetchAllCategoriasPorNombre(null);
    	context.startManagingCursor(cur);
    	cur.moveToFirst();
    	while(!cur.isAfterLast()){
           cat = cursorToCategoriaDto(cur);
           result.add(cat);
           cur.moveToNext();
    	}
    	cur.close();
    	context.stopManagingCursor(cur);
    	finalizarBD();
    	return result;
    }    

    private static List<CategoriaDTO> obtenerCategoriasPersonales(Activity context){
    	CategoriaDTO cat = null;
    	List<CategoriaDTO> result = new ArrayList<CategoriaDTO>();
    	inicializarBD(context);
    	Cursor cur = mDBManager.fetchAllCategoriasPersonalesPorNombre(null);
    	context.startManagingCursor(cur);
    	cur.moveToFirst();
    	while(!cur.isAfterLast()){
           cat = cursorToCategoriaDto(cur);
           result.add(cat);
           cur.moveToNext();
    	}
    	cur.close();
    	context.stopManagingCursor(cur);
    	finalizarBD();
    	return result;
    }     
    

    
    private static List<Long> obtenerPreferidos(Activity context){
    	Long id = null;
    	List<Long> result = new ArrayList<Long>();
    	inicializarBD(context);
    	Cursor cur = mDBManager.fetchAllPreferidos();
    	context.startManagingCursor(cur);
    	cur.moveToFirst();
    	while(!cur.isAfterLast()){
           id = cursorToPreferido(cur);
           result.add(id);
           cur.moveToNext();
    	}
    	cur.close();
    	context.stopManagingCursor(cur);
    	finalizarBD();
    	return result;
    }     
        
    
    public static String ENTER = "\n";
    public static final String PUNTO_COMA = ";";
    public static final String COMA = ",";
    private static String PIPE = " | ";
    private static String INICIO = "#I#";
    private static String FIN = "#F#";
    private static String CAMPO_NULO = "###";
    private static String PARTICULAR = "PARTICULAR";
    private static String LABORAL = "LABORAL";
    private static String OTRO = "OTRO";
    private static String MOVIL = "MOVIL";
    private static String SEPARACION_ATRIBUTO = ":";
    private static String HEAD_PERSONA = "$$PE";
    private static String HEAD_CATEGORIA = "$$C";
    private static String HEAD_CATEGORIA_PERSONAL = "$$CP";
    private static String HEAD_PREFERIDO = "$$PR";
    public static final int ACTIVITY_EJECUTAR_EXPORTAR_CONTACTOS = 19;
    public static final int ACTIVITY_EJECUTAR_IMPORTAR_CONTACTOS_CSV = 20;
	public static final int ACTIVITY_EJECUTAR_EXPORTAR_CONTACTOS_ESTETICO = 21;
    
    public static void mostrarMensajeDialog(Activity context, String message){
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setMessage(message)
    	       .setCancelable(true)
    	       .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	    dialog.toString();       				
    	           }
    	       });
    	builder.show(); 
    }
    
    private static File obtenerFileCSV(Activity context){
        // LEVANTAR TODOS LOS FILES QUE ESTAN EN LA CARPETA KN-CSVfiles
    	  File backup = null;
    	  boolean boolValue = true;
    	  Asociacion canStore = null;
    	  canStore = comprobarSDCard(context);
   		  boolValue = (Boolean)canStore.getKey();
   		  String msg = (String)canStore.getValue();
   		  if(boolValue){
   		 	  String path = obtenerPath(folderCSV);
   		      backup = new File(path + File.separator + fileCSV);
   		  }else{
  			  mensaje = msg;
  		  }
   		  return backup;
    }    
        
    public static void eliminarCategoriaPersonal(CategoriaDTO cat, Activity context){
		inicializarBD(context);
		mDBManager.eliminarCategoriaPersonal(cat.getId());
		finalizarBD();    	
    }
    
    
    public static void crearCategoriaPersonal(CategoriaDTO cat, String oldNameCat, boolean forImport, Activity context){
		inicializarBD(context);
		mDBManager.crearCategoriaPersonal(cat, oldNameCat, forImport);
		finalizarBD();
    }
    
    public static long crearPersona(PersonaDTO personaSeleccionada, boolean forImport, Activity context){
    	long id = -1;
    	inicializarBD(context);
    	id = mDBManager.createPersona(personaSeleccionada, forImport);
    	finalizarBD();
    	return id;
    }
    
    public static void eliminarTelefono(TipoValorDTO tv, Activity context){
        inicializarBD(context);
    	mDBManager.eliminarTelefono(tv.getId());
    	finalizarBD();   	
    }
    
    public static void eliminarEmail(TipoValorDTO tv, Activity context){
        inicializarBD(context);
    	mDBManager.eliminarEmail(tv.getId());
    	finalizarBD();   	
    }

    public static void eliminarDireccion(TipoValorDTO tv, Activity context){
        inicializarBD(context);
    	mDBManager.eliminarDireccion(tv.getId());
    	finalizarBD();   	
    }
    
    public static void crearTelefono(TipoValorDTO mTipoValor, Activity context){
		inicializarBD(context);
		mDBManager.createTipoValor(mTipoValor, ConstantsAdmin.TABLA_TELEFONOS);
		finalizarBD();
    }
    
    public static void crearEmail(TipoValorDTO mTipoValor, Activity context){
		inicializarBD(context);
		mDBManager.createTipoValor(mTipoValor, ConstantsAdmin.TABLA_EMAILS);
		finalizarBD();
    }
    
    public static void crearDireccion(TipoValorDTO mTipoValor, Activity context){
		inicializarBD(context);
		mDBManager.createTipoValor(mTipoValor, ConstantsAdmin.TABLA_DIRECCIONES);
		finalizarBD();
    }

    public static List<CategoriaDTO> obtenerCategoriasActivas(Activity context, String nombre){
    	Cursor cursor = null;
    	List<CategoriaDTO> categorias = new ArrayList<CategoriaDTO>();
	    inicializarBD(context);
	    cursor = mDBManager.fetchCategoriasActivasPorNombre(nombre);
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        categorias = categoriasCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }
	    finalizarBD();
	    return categorias;
    }
    
    public static List<CategoriaDTO> obtenerCategoriasActivasPersonales(Activity context){
    	Cursor cursor = null;
    	List<CategoriaDTO> categorias = new ArrayList<CategoriaDTO>();
	    inicializarBD(context);   
	    cursor = ConstantsAdmin.mDBManager.fetchCategoriasPersonalesActivasPorNombre(null);
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        categorias = ConstantsAdmin.categoriasCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }

	    finalizarBD();
	    return categorias;
    }
    
    public static List<CategoriaDTO> obtenerCategorias(Activity context, String nombre){
    	Cursor cursor = null;
    	List<CategoriaDTO> categorias = new ArrayList<CategoriaDTO>();
	    inicializarBD(context);
	    cursor = mDBManager.fetchAllCategoriasPorNombre(nombre);
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        categorias = categoriasCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }
	    finalizarBD();
	    return categorias;
    }
    
    public static void actualizarCategoria(CategoriaDTO catSelected, Activity context){
 		inicializarBD(context);
		mDBManager.actualizarCategoria(catSelected);
		finalizarBD();
    }
    
    public static void actualizarConfig(Activity context){
 		inicializarBD(context);
		mDBManager.actualizarConfig(config);
		finalizarBD();
    }
        
    
    public static void eliminarPreferido(Activity context, long idPer){
		inicializarBD(context);
		mDBManager.eliminarPreferido(idPer);
		finalizarBD();
    }
    
    public static void crearPreferido(Activity context, long idPer){   
		inicializarBD(context);
		mDBManager.crearPreferido(idPer);
		finalizarBD();
    }
    
    public static void eliminarPersona(Activity context, long perId){
		inicializarBD(context);
		mDBManager.eliminarPersona(perId);
		finalizarBD();
    }
    
    
    // CAMBIOS PARA AGREGAR CONTRASENIA EN LAS CATEGORIAS
    
    // TABLA: Contrasenia
    
    
    public static final String TABLA_CONTRASENIA = "contrasenia";
    public static final String KEY_CONTRASENIA = "contrasenia";
    public static final String KEY_CONTRASENIA_ACTIVA = "contraseniaActiva";
    public static final String KEY_MAIL1 = "mail1";
    public static final String KEY_MAIL2 = "mail2";
    
    
// TABLA: Categoria protegida
    
    
    public static final String TABLA_CATEGORIA_PROTEGIDA = "categoriaProtegida";
    
    
    public static void crearCategoriaProtegida(CategoriaDTO cat, Activity context){
		inicializarBD(context);
		mDBManager.crearCategoriaProtegida(cat);
		finalizarBD();
    }
    
    
    public static long crearContrasenia(ContraseniaDTO contrasenia, Activity context){
    	long id = -1;
		inicializarBD(context);
		id = mDBManager.crearContrasenia(contrasenia);
		finalizarBD();
		return id;
    }
    
    public static void almacenarContraseniaEnArchivo(Activity context){
    	Asociacion canStore = null;
    	Boolean boolValue = true;
    	String msg = null;
    	String body = null;
	
    	canStore = comprobarSDCard(context);
    	body = contrasenia.getContrasenia();
 		boolValue = (Boolean)canStore.getKey();
 		if(boolValue){
 			try {
 				almacenarArchivo(folderCSV, filePassword , body);
			} catch (Exception e) {
				
			}
 			msg = context.getString(R.string.mensaje_exito_almacenar_password);
 		}
 		if(msg != null){
 	 		mostrarMensajeDialog(context, msg);
 		}

   	
    }
    
    public static void eliminarCategoriaProtegida(CategoriaDTO cat, Activity context){
		inicializarBD(context);
		mDBManager.eliminarCategoriaProtegida(cat.getNombreReal());
		finalizarBD();    	
    }
    
    
    public static List<CategoriaDTO> obtenerCategoriasProtegidas(Activity context){
    	Cursor cursor = null;
    	List<CategoriaDTO> categorias = new ArrayList<CategoriaDTO>();
	    cursor = ConstantsAdmin.mDBManager.fetchAllCategoriasProtegidasPorNombre(null);
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        categorias = ConstantsAdmin.categoriasProtegidasCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }
	    return categorias;
    }
    
    public static ContraseniaDTO obtenerContrasenia(Activity context){
    	Cursor cursor = null;
    	ContraseniaDTO contrasenia = null;
	    cursor = ConstantsAdmin.mDBManager.fetchContrasenia();
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        contrasenia = ConstantsAdmin.contraseniaCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }

	    return contrasenia;
    }
    
    
    public static ConfigDTO obtenerConfiguracion(Activity context){
    	Cursor cursor = null;
    	ConfigDTO config = null;
    	inicializarBD(context);
	    cursor = ConstantsAdmin.mDBManager.fetchConfig();
	    if(cursor != null){
	    	cursor.moveToFirst();
	    	if(!cursor.isAfterLast()){
		        context.startManagingCursor(cursor);
		        config = ConstantsAdmin.configCursorToDtos(cursor);
		        cursor.close();
		        context.stopManagingCursor(cursor);
	    	}else{
		    	config = new ConfigDTO();
		    	config.setEstanDetallados(false);
		    	config.setListaExpandida(false);
		    	config.setMuestraPreferidos(false);
		    	config.setOrdenadoPorCategoria(true);
		    }

	    }
	    finalizarBD();
	    return config;
    }
    
	public static boolean estaProtegidaCategoria(String nombreCategoria){
		boolean result = false;
		CategoriaDTO cat = null;
		if(categoriasProtegidas != null){
			Iterator<CategoriaDTO> it = categoriasProtegidas.iterator();
			while(it.hasNext() && !result){
				cat = (CategoriaDTO) it.next();
				result = cat.getNombreReal().equals(nombreCategoria);
				
			}
		}
		return result;
		
	}
    
    
    public static ContraseniaDTO contraseniaCursorToDtos(Cursor cursor){
    	ContraseniaDTO contrasenia = null; 
    	
    	String pass = null;
    	long passId = 0;
    	int activa;
    	String mail = null;
    	
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
        	pass = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_CONTRASENIA));
        	mail = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_MAIL1));
        	passId = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
        	activa = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_CONTRASENIA_ACTIVA));
        	contrasenia = new ContraseniaDTO();
        	if(activa == 1){
        		contrasenia.setActiva(true);	
        	}else{
        		contrasenia.setActiva(false);
        	}
        	
        	contrasenia.setContrasenia(pass);
        	contrasenia.setId(passId);
        	contrasenia.setMail(mail);
        }
        
        return contrasenia;

    }  
    
    public static ConfigDTO configCursorToDtos(Cursor cursor){
    	ConfigDTO config = null; 
    	int id = -1;
    	int estanDetallados = 0;
    	int organizadosPorCateg = 0;
    	int estanExpandidos = 0;
    	int muestraPreferidos = 0;
        	
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
        	estanDetallados = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_ESTAN_DETALLADOS));
        	organizadosPorCateg = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_ORDENADO_POR_CATEGORIA));
        	estanExpandidos = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_LISTA_EXPANDIDA));
        	muestraPreferidos = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_MUESTRA_PREFERIDOS));
        	id = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
        	config = new ConfigDTO();
        	if(estanDetallados == 1){
        		config.setEstanDetallados(true);	
        	}
        	if(organizadosPorCateg == 1){
        		config.setOrdenadoPorCategoria(true);	
        	}
        	if(estanExpandidos == 1){
        		config.setListaExpandida(true);	
        	}
        	if(muestraPreferidos == 1){
        		config.setMuestraPreferidos(true);	
        	}
        	config.setId(id);
        }
        
        return config;

    }  

    
    
    public static List<CategoriaDTO> categoriasProtegidas = null;
    
    public static ContraseniaDTO contrasenia = null;
    public static ConfigDTO config = null;
   
    public static void cargarCategoriasProtegidas(Activity context){
    	categoriasProtegidas = obtenerCategoriasProtegidas(context);
    }
    
    public static void cargarContrasenia(Activity context){
    	contrasenia = obtenerContrasenia(context);
    	if(contrasenia == null){
    		contrasenia = new ContraseniaDTO();
    		
    	}
    	
    }
    
    public static void actualizarContrasenia(ContraseniaDTO pass, Activity context){
    	inicializarBD(context);
    	mDBManager.crearContrasenia(pass);
    	finalizarBD();
    }
    
    public static final int ACTIVITY_EJECUTAR_PROTECCION_CATEGORIA = 22;
    public static final int ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA = 23;
    public static final int ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA_DESDE_SPINNER = 24;
    
    private static String HEAD_CATEGORIA_PROTEGIDA = "$$PP";
    
    private static String HEAD_CONTRASENIA = "$$PS";
    
    private static String filePassword = "kncontacts.txt";
    
    public static String imageFolder = "Pictures";
	public static boolean cerrarMainActivity = false;
    
    public static final int ACTIVITY_EJECUTAR_SACAR_PHOTO = 25;

    
    public static String obtenerPathImagen(){
    	String result = null;
    	result = obtenerPath(folderCSV) + File.separator + imageFolder + File.separator;
    	return result;
    }
    
    public static void almacenarImagen(Activity context, String nombreDirectorio, String nombreArchivo, Bitmap bm) throws IOException, FileNotFoundException{
  	  	  String path = obtenerPath(nombreDirectorio);
	      OutputStream fOut = null;
	      File dir = new File(path);
	      dir.mkdirs();
	      
	      File file = new File(dir.getPath(), nombreArchivo);
	      if(!file.exists()){
	    	  file.createNewFile();
	      }
	      fOut = new FileOutputStream(file);
	      bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
	 //     bm.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
	      fOut.flush();
	      fOut.close();

	      MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
	  }


	public static void enviarMailContraseniaCategoriasProtegidas(Activity activity) {
		// TODO Auto-generated method stub
		   
		String emailaddress = contrasenia.getMail();
		String body = activity.getResources().getString(R.string.app_name) + ":" + contrasenia.getContrasenia();
		String subject = activity.getResources().getString(R.string.app_name);

		enviarMailGenerico(activity, emailaddress, body, subject);
	}


	public static void enviarMailGenerico(Activity activity, String emailaddress, String body, String subject) {
		// TODO Auto-generated method stub
		   
		String[] address = {emailaddress};
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); //This is the email intent
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, address); // adds the address to the intent
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);//the subject
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body); //adds the body of the mail
		 
		activity.startActivity(emailIntent);	
	}


	public static String recuperarInfoContacto(
			Activity activity, long id) {
		// TODO Auto-generated method stub
		
		String result = null;
		PersonaDTO per = obtenerPersonaId(activity, id);
		result = obtenerStringEsteticoPersonaParaEnviar(per, activity, ENTER);
		return result;
	}

	public static final int ACTIVITY_EJECUTAR_MENU_PERSONA = 26;

}
