package com.boxico.android.kn.contacts.persistencia;

import java.util.Iterator;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;

import com.boxico.android.kn.contacts.persistencia.dtos.*;
import com.boxico.android.kn.contacts.util.*;

public class DataBaseManager {
   
    
	 private DataBaseHelper mDbHelper;
	 private SQLiteDatabase mDb;
	 private Context mCtx;
	 private boolean isOpened = false;



	private static final DataBaseManager instanciaUnica = new DataBaseManager();
	 
// --Commented out by Inspection START (12/11/2018 12:44):
//	 public DataBaseManager(Context ctx) {
//	        this.mCtx = ctx;
//	 }
// --Commented out by Inspection STOP (12/11/2018 12:44)


	private DataBaseManager() {
	 	super();
	}

/*
	public boolean isOpened() {
		return isOpened;
	}
*//*
	public void setOpened(boolean opened) {
		isOpened = opened;
	}*/

	public static DataBaseManager getInstance(Context ctx) {
	 	instanciaUnica.setmCtx(ctx);
	 	return instanciaUnica;
	 }
/*
	public Context getmCtx() {
		return mCtx;
	}
*/
	private void setmCtx(Context mCtx) {
		this.mCtx = mCtx;
	}

     public void open() throws SQLException {
		if(!this.isOpened) {
			mDbHelper = new DataBaseHelper(mCtx);
			mDb = mDbHelper.getWritableDatabase();
			this.isOpened = true;
		}
	 }
     
     public void close() {
		if(this.isOpened) {
			mDbHelper.close();
			this.isOpened = false;
		}
     }
     
     public long createTipoValor(TipoValorDTO tipoVal, String nombreTabla) {
    	 long returnValue = tipoVal.getId();
         ContentValues initialValues = new ContentValues();
         String tipoTemp = tipoVal.getTipo();
         if(tipoTemp == null || tipoTemp.equals("")) {
         	tipoTemp = "type";
		 }
         initialValues.put(ConstantsAdmin.KEY_TIPO, tipoTemp);
         initialValues.put(ConstantsAdmin.KEY_VALOR, tipoVal.getValor());
         initialValues.put(ConstantsAdmin.KEY_ID_PERSONA, tipoVal.getIdPersona());
    	 if(tipoVal.getId() == -1 ){
			 returnValue = mDb.insert(nombreTabla, null, initialValues);
    	 }else{
			 mDb.update(nombreTabla, initialValues, ConstantsAdmin.KEY_ROWID + "=" + tipoVal.getId() , null);

    		// tipoVal.getId();
    	 }
    	 return returnValue;

	 }
     
     public void createTelefono(TipoValorDTO tipoVal) {
		 createTipoValor(tipoVal, ConstantsAdmin.TABLA_TELEFONOS);
	 }

     public void createEmail(TipoValorDTO tipoVal) {
		 createTipoValor(tipoVal, ConstantsAdmin.TABLA_EMAILS);
	 }

     public void createDireccion(TipoValorDTO tipoVal) {
		 createTipoValor(tipoVal, ConstantsAdmin.TABLA_DIRECCIONES);
	 }

	public void eliminarTipoValor(long id, String nombreTabla){
		mDb.delete(nombreTabla, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(id), null);
	}
     /*
     public void eliminarTelefono(long id){
		 mDb.delete(ConstantsAdmin.TABLA_TELEFONOS, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(id), null);
	 }
     
     public void eliminarEmail(long id){
		 mDb.delete(ConstantsAdmin.TABLA_EMAILS, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(id), null);
	 }

     public void eliminarDireccion(long id){
		 mDb.delete(ConstantsAdmin.TABLA_DIRECCIONES, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(id), null);
	 }
*/
     public long createPersona(PersonaDTO persona, boolean importando) {
    	 long returnValue;
         ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_APELLIDO, persona.getApellido());
         initialValues.put(ConstantsAdmin.KEY_NOMBRES, persona.getNombres());
         initialValues.put(ConstantsAdmin.KEY_FECHA_NACIMIENTO, persona.getFechaNacimiento());
         initialValues.put(ConstantsAdmin.KEY_CATEGORIA, persona.getCategoriaId());
     /*    initialValues.put(ConstantsAdmin.KEY_EMAIL_PARTICULAR, persona.getEmailParticular());
         initialValues.put(ConstantsAdmin.KEY_EMAIL_LABORAL, persona.getEmailLaboral());
         initialValues.put(ConstantsAdmin.KEY_EMAIL_OTRO, persona.getEmailOtro());
         initialValues.put(ConstantsAdmin.KEY_TEL_CELULAR, persona.getCelular());
         initialValues.put(ConstantsAdmin.KEY_TEL_LABORAL, persona.getTelLaboral());
         initialValues.put(ConstantsAdmin.KEY_TEL_PARTICULAR, persona.getTelParticular());
         initialValues.put(ConstantsAdmin.KEY_DIRECCION_PARTICULAR, persona.getDireccionParticular());
         initialValues.put(ConstantsAdmin.KEY_DIRECCION_LABORAL, persona.getDireccionLaboral());*/
		 initialValues.put(ConstantsAdmin.KEY_DESCRIPCION, persona.getDescripcion());
         initialValues.put(ConstantsAdmin.KEY_NOMBRE_CATEGORIA, persona.getCategoriaNombre());
         initialValues.put(ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, persona.getCategoriaNombreRelativo());
     //    initialValues.put(ConstantsAdmin.KEY_FOTO, persona.getFoto());
         initialValues.put(ConstantsAdmin.KEY_DATO_EXTRA, persona.getDatoExtra());
		 initialValues.put(ConstantsAdmin.KEY_ID_PERSONA_AGENDA, persona.getIdPersonaAgenda());

      //   this.open();
         if(!importando){
        	 if(persona.getId() == -1 ){
        		 returnValue= mDb.insert(ConstantsAdmin.TABLA_PERSONA, null, initialValues);
        	 }else{
        		 mDb.update(ConstantsAdmin.TABLA_PERSONA, initialValues, ConstantsAdmin.KEY_ROWID + "=" + persona.getId() , null);
        		 returnValue = persona.getId();
        	 }       	 
         }else{
        	 initialValues.put(ConstantsAdmin.KEY_ROWID, persona.getId());
        	 returnValue= mDb.insert(ConstantsAdmin.TABLA_PERSONA, null, initialValues);
         }
	//	 this.close();

         return returnValue;
         
     }
     
     public void eliminarPersona(long idPersona){
     	 mDb.delete(ConstantsAdmin.TABLA_PREFERIDOS, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(idPersona), null);
		 mDb.delete(ConstantsAdmin.TABLA_PERSONA, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(idPersona), null);

	 }
     
     public void eliminarPreferido(long idPersona){
		 mDb.delete(ConstantsAdmin.TABLA_PREFERIDOS, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(idPersona), null);
	 }

     public void eliminarCategoriaPersonal(long idCat){
		 mDb.delete(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(idCat), null);
	 }
    
/*     
     public long crearPaciente(long idPersona, String obraSocial){
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_NOMBRE_OS, obraSocial);
         initialValues.put(ConstantsAdmin.KEY_ROWID_PERSONA, idPersona);
         
         return mDb.insert(ConstantsAdmin.TABLA_PACIENTES, null, initialValues);
     }

 */    
     
/*     public long crearProveedor(long idPersona, String direccion, String empresa){
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_DIRECCION, direccion);
         initialValues.put(ConstantsAdmin.KEY_ROWID_PERSONA, idPersona);
         initialValues.put(ConstantsAdmin.KEY_EMPRESA, empresa);
         
         return mDb.insert(ConstantsAdmin.TABLA_PROVEEDORES, null, initialValues);
     }

     public long crearCliente(long idPersona, String direccion, String empresa){
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_DIRECCION, direccion);
         initialValues.put(ConstantsAdmin.KEY_ROWID_PERSONA, idPersona);
         initialValues.put(ConstantsAdmin.KEY_EMPRESA, empresa);
         
         return mDb.insert(ConstantsAdmin.TABLA_CLIENTES, null, initialValues);
     }
    
    */ 
     
     public String queryParaCategoriaProtegidas(boolean noActivaCategoria, List<CategoriaDTO> catProt){
    	 StringBuilder result = new StringBuilder();
    	 CategoriaDTO cat;
    	 result.append(" (1 = 1) ");
    	 Iterator<CategoriaDTO> it;

    	 if(noActivaCategoria && catProt != null){
			 it = catProt.iterator();
			 while(it.hasNext()){
				 cat = it.next();
				 result.append(" AND (").append(ConstantsAdmin.KEY_NOMBRE_CATEGORIA).append(" <> '").append(cat.getNombreReal()).append("') ");
			 }

		 }
    	 return result.toString();
     }
     
     public Cursor fetchAllPersonaPorApellidoONombreODatosCategoriaMultiSeleccion(boolean noActivaContrasenia, String param, List<String> categorias, List<CategoriaDTO> categoriasProtegidas) {
    	 String sortOrder = ConstantsAdmin.KEY_APELLIDO + " COLLATE LOCALIZED ASC";
    	 Cursor result = null;
    	 StringBuilder consultaPorCategoria = new StringBuilder(" (1 = 2) ");
    	 Iterator<String> catSelect;
    	 String catTemp;

    	 String selection = this.queryParaCategoriaProtegidas(noActivaContrasenia, categoriasProtegidas);
    	 /*
    	 String categProteg = " (1 = 1) ";
    	 if(noActivaContraseña){
    		 categProteg = categProteg + this.queryParaCategoriaProtegidas(categoriasProtegidas);
    	 }
    	 */
    	 
    	 String consulta = selection + " AND (" + ConstantsAdmin.KEY_APELLIDO + " LIKE '%" + param +"%' OR " + ConstantsAdmin.KEY_NOMBRES + " LIKE '%" + param + "%' OR " + ConstantsAdmin.KEY_DATO_EXTRA + " LIKE '%" + param + "%' OR " + ConstantsAdmin.KEY_DESCRIPCION + " LIKE '%" + param + "%')";
    	 String[] atr = new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, ConstantsAdmin.KEY_DESCRIPCION};
    	 try{
    		 if(categorias == null || categorias.size() == 0){// Categoria 0 - incluye todos
		    	 if(param != null && !param.equals("")){
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, consulta , null, null, null, sortOrder);
		    	 }else{
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, selection, null, null, null, sortOrder);
		    	 }
    		 }else{// Categoria distinta de 0, es selectivo
    			 catSelect = categorias.iterator();
    			 while(catSelect.hasNext()){
    				 catTemp = catSelect.next();
    				 consultaPorCategoria.append(" OR (").append(ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO).append("='").append(catTemp).append("')");
    			 }
		    	 if(param != null && !param.equals("")){
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, "(" + consulta + ") AND (" + consultaPorCategoria + ")", null, null, null, sortOrder);
		    	 }else{
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, selection + " AND " + consultaPorCategoria, null, null, null, sortOrder);
		    	 }
    			 
    		 }
    	 }catch (SQLiteException e) {
			e.getMessage();
		}
    	 return result;
    	 
     }

/*
     public Cursor fetchPorApellidoEnCategoria(String primeraLetra, String categoria, List<CategoriaDTO> categoriasProtegidas) {
    	 String sortOrder = ConstantsAdmin.KEY_APELLIDO + " COLLATE LOCALIZED ASC";
    	 Cursor result = null;
    	 String consultaPorCategoria = " (1 = 2) ";
    	 String categProteg = " (1 = 1) ";
    	 if(!ConstantsAdmin.contrasenia.isActiva()){
    		 categProteg = categProteg + this.queryParaCategoriaProtegidas(categoriasProtegidas);
    	 }

//    	 LIKE '[^a-zA-Z]%'
    	 String consulta = categProteg + " AND " + ConstantsAdmin.KEY_APELLIDO + " LIKE '" + primeraLetra +"%' ";
    	 String[] atr = new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, ConstantsAdmin.KEY_DESCRIPCION};
    	 try{
    		 if(categoria == null || categoria.equals("")){// Categoria 0 - incluye todos
		    	 if(primeraLetra != null && !primeraLetra.equals("")){
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, consulta , null, null, null, sortOrder);
		    	 }else{
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, categProteg, null, null, null, sortOrder);
		    	 }
    		 }else{// Categoria distinta de 0, es selectivo
   				 consultaPorCategoria = consultaPorCategoria + " OR (" +  ConstantsAdmin.KEY_NOMBRE_CATEGORIA + "='" + categoria + "')";
		    	 if(primeraLetra != null && !primeraLetra.equals("")){
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, "(" + consulta + ") AND (" + consultaPorCategoria + ")", null, null, null, sortOrder);
		    	 }else{
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, categProteg + " AND " + consultaPorCategoria, null, null, null, sortOrder);
		    	 }

    		 }
    	 }catch (SQLiteException e) {
			e.getMessage();
		}
    	 return result;

     }
*/
     /*
     public long tablaPersonasSize(){
//    	 Cursor cur = null;
    	 long result = 0;
    	 SQLiteStatement s = mDb.compileStatement(DataBaseHelper.SIZE_PERSONAS);
    	 result =
    		 s.simpleQueryForLong();
    	 return result;
     }
     */
     
     public long tablaPreferidosSize(){
//    	 Cursor cur = null;
    	 long result;
    	 SQLiteStatement s = mDb.compileStatement(DataBaseHelper.SIZE_PREFERIDOS);
    	 result = s.simpleQueryForLong();
    	 return result;
     }
     
     public void crearCategoria(CategoriaDTO categoria, boolean importando){
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_NOMBRE_CATEGORIA, categoria.getNombreReal());

         initialValues.put(ConstantsAdmin.KEY_CATEGORIA_ACTIVA, categoria.getActiva());
         initialValues.put(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA, categoria.getTipoDatoExtra());
      //   long result = -1;
	//	 this.open();
         if(!importando){
        	 mDb.insert(ConstantsAdmin.TABLA_CATEGORIA, null, initialValues);
         }else{
        	 initialValues.put(ConstantsAdmin.KEY_ROWID, categoria.getId());
        	 mDb.insert(ConstantsAdmin.TABLA_CATEGORIA, null, initialValues);
         }
    //     this.close();
	 }
     
     public void crearPreferido(long preferidoId){
     	ContentValues initialValues = new ContentValues();
        initialValues.put(ConstantsAdmin.KEY_ROWID, preferidoId);
		mDb.insert(ConstantsAdmin.TABLA_PREFERIDOS, null, initialValues);
	 }
     
     
     public void crearCategoriaPersonal(CategoriaDTO categoria, String oldCat, boolean importando){
    //	 long returnValue = -1;
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_NOMBRE_CATEGORIA, categoria.getNombreReal());
         initialValues.put(ConstantsAdmin.KEY_CATEGORIA_ACTIVA, categoria.getActiva());
         initialValues.put(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA, categoria.getTipoDatoExtra());
         // 	 this.open();
		 if(!importando){
			 if(categoria.getId() == 0 ){
				 mDb.insert(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, null, initialValues);
			 }else{
				 mDb.update(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, initialValues, ConstantsAdmin.KEY_ROWID + "=" + categoria.getId() , null);
				 this.actualizarContactosPorCambioCategoriaPersonal(categoria, oldCat);
			//	 categoria.getId();
			 }
		 }else{
			 initialValues.put(ConstantsAdmin.KEY_ROWID, categoria.getId());
			 mDb.insert(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, null, initialValues);
		 }
	//	 this.close();

	 }
     
     private void actualizarContactosPorCambioCategoriaPersonal(CategoriaDTO categoria, String oldCat){
     	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_NOMBRE_CATEGORIA, categoria.getNombreReal());
         initialValues.put(ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, categoria.getNombreReal());
         mDb.update(ConstantsAdmin.TABLA_PERSONA, initialValues, ConstantsAdmin.KEY_NOMBRE_CATEGORIA + "='" + oldCat +"'" , null);
     }
     
     public void actualizarCategoria(CategoriaDTO categoria){
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_CATEGORIA_ACTIVA, categoria.getActiva());
         initialValues.put(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA, categoria.getTipoDatoExtra());
		 mDb.update(ConstantsAdmin.TABLA_CATEGORIA, initialValues, ConstantsAdmin.KEY_ROWID + " = " + categoria.getId(), null);
	 }

     public void actualizarConfig(ConfigDTO config){
    	 ContentValues initialValues = new ContentValues();
    	 if(!config.isEstanDetallados()){
    		 initialValues.put(ConstantsAdmin.KEY_ESTAN_DETALLADOS, 0);	 
    	 }else{
    		 initialValues.put(ConstantsAdmin.KEY_ESTAN_DETALLADOS, 1);	 
    	 }
    	 if(!config.isListaExpandida()){
    		 initialValues.put(ConstantsAdmin.KEY_LISTA_EXPANDIDA, 0);	 
    	 }else{
    		 initialValues.put(ConstantsAdmin.KEY_LISTA_EXPANDIDA, 1);	 
    	 }
    	 if(!config.isMuestraPreferidos()){
    		 initialValues.put(ConstantsAdmin.KEY_MUESTRA_PREFERIDOS, 0);	 
    	 }else{
    		 initialValues.put(ConstantsAdmin.KEY_MUESTRA_PREFERIDOS, 1);	 
    	 }         
    	 if(!config.isOrdenadoPorCategoria()){
    		 initialValues.put(ConstantsAdmin.KEY_ORDENADO_POR_CATEGORIA, 0);	 
    	 }else{
    		 initialValues.put(ConstantsAdmin.KEY_ORDENADO_POR_CATEGORIA, 1);	 
    	 } 
    	// long id = -1;

         if(config.getId() == -1 ){
             initialValues.put(ConstantsAdmin.KEY_ROWID, 1);
             mDb.insert(ConstantsAdmin.TABLA_CONFIGURACION, null, initialValues);
         }else{
             mDb.update(ConstantsAdmin.TABLA_CONFIGURACION, initialValues, ConstantsAdmin.KEY_ROWID + " = " + config.getId(), null);
         }


	 }
     
     public void actualizarCategoriaPersonal(CategoriaDTO categoria){
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_CATEGORIA_ACTIVA, categoria.getActiva());
         initialValues.put(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA, categoria.getTipoDatoExtra());
		 mDb.update(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, initialValues, ConstantsAdmin.KEY_ROWID + " = " + categoria.getId(), null);
	 }
     
// --Commented out by Inspection START (12/11/2018 12:34):
//     public void borrarDatosFicticios(){
//    	 mDb.delete(ConstantsAdmin.TABLA_PERSONA, null, null);
//    	 mDb.delete(ConstantsAdmin.TABLA_CATEGORIA, null, null);
//     }
// --Commented out by Inspection STOP (12/11/2018 12:34)

     public void createBD(){
    	 mDbHelper.onCreate(mDb);
     }
     
     private long tablaContraseniaSize(){
	   	 long result = 0;
	   	 SQLiteStatement s = mDb.compileStatement(DataBaseHelper.SIZE_CONTRASENIA);
		 result = s.simpleQueryForLong();
		 return result;
     }

     public long tablaCategoriaSize(){
   	 	long result = 0;
		SQLiteStatement s = mDb.compileStatement(DataBaseHelper.SIZE_CATEGORIAS);
		result = s.simpleQueryForLong();
		return result;
     }
     
     public boolean actualizarTablaCategoria(){
    	boolean result = false;
    	try{

			if(tablaCategoriaSize() > 0){
				mDb.execSQL(DataBaseHelper.ACTUALIZAR_TABLA_CATEGORIAS);
				result = true;
			}

		}catch(Exception e){

		}

		return result;
     }

	public boolean actualizarTablaPersona(){
		boolean result = false;
		try{
			mDb.execSQL(DataBaseHelper.ACTUALIZAR_TABLA_PERSONA);
			result = true;
		}catch(Exception e){

		}
		return result;
	}

	public void actualizarTablaContrasenia(){
		//	boolean result = false;
		try {
			if(tablaContraseniaSize() > 0){
				mDb.execSQL(DataBaseHelper.ACTUALIZAR_TABLA_CONTRASENIA);
				//	result = true;
			}
		} catch (Exception e) {
			e.getMessage();
		}

	}

     public void upgradeDB(){
    	 mDbHelper.onUpgrade(mDb, 1, 2);
     }
     
     public void deleteAll(){
    	 mDbHelper.deleteAll(mDb);
     }

	public CursorLoader cursorLoaderPreferidos(boolean noActivaContrasenia, List<CategoriaDTO> categoriasProtegidas, Context context) {
/*		String categProteg = " (1 = 1) ";
		if(noActivaContraseña){
			categProteg = categProteg + this.queryParaCategoriaProtegidas(categoriasProtegidas);
		}
		*/

		String selection = this.queryParaCategoriaProtegidas(noActivaContrasenia, categoriasProtegidas);

		String query = "SELECT * FROM " + ConstantsAdmin.TABLA_PERSONA + " a INNER JOIN " + ConstantsAdmin.TABLA_PREFERIDOS + " b ON a._id = b._id WHERE " + selection;


		return new CursorLoader( context, null, null, query, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.rawQuery(getSelection(), null);
				}
				return c;
			}
		};

	}


/*

	public Cursor fetchAllPreferidos(List<CategoriaDTO> categoriasProtegidas){
    	 Cursor result = null;
    	
       	 try{
        	 String categProteg = " (1 = 1) ";
        	 if(!ConstantsAdmin.contrasenia.isActiva()){
        		 categProteg = categProteg + this.queryParaCategoriaProtegidas(categoriasProtegidas);
        	 }
    	//	 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,
    	//			 ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO}, ConstantsAdmin.KEY_NOMBRE_CATEGORIA + "= '" + categoria.getNombreReal() + "'", null, null, null, sortOrder);

    		 String MY_QUERY = "SELECT * FROM " + ConstantsAdmin.TABLA_PERSONA + " a INNER JOIN " + ConstantsAdmin.TABLA_PREFERIDOS + " b ON a._id = b._id WHERE " + categProteg;

    	//	 result = mDb.rawQuery(MY_QUERY, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,
    	//			 ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO});
    		 result = mDb.rawQuery(MY_QUERY, null);	 
    	 }catch (SQLiteException e) {
			e.getMessage();
    	 }
    	 return result;
     }
     */

     /*
     public Cursor fetchAllPreferidos(){
    	 Cursor result = null;
    	
       	 try{

    	//	 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,
    	//			 ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO}, ConstantsAdmin.KEY_NOMBRE_CATEGORIA + "= '" + categoria.getNombreReal() + "'", null, null, null, sortOrder);

    		 String MY_QUERY = "SELECT * FROM " + ConstantsAdmin.TABLA_PERSONA + " a INNER JOIN " + ConstantsAdmin.TABLA_PREFERIDOS + " b ON a._id = b._id ";

    	//	 result = mDb.rawQuery(MY_QUERY, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,
    	//			 ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO});
    		 result = mDb.rawQuery(MY_QUERY, null);	 
    	 }catch (SQLiteException e) {
			e.getMessage();
    	 }
    	 return result;
     }
*/

     /*
     public Cursor fetchAllPersonaPorApellidoONombre(String param, CategoriaDTO categoria) {
    	 String sortOrder = ConstantsAdmin.KEY_APELLIDO + " COLLATE LOCALIZED ASC";
    	 Cursor result = null;
    	 try{
    		 if(categoria == null || categoria.getId() == 0){// Categoria 0 - incluye todos
		    	 if(param != null && !param.equals("")){
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,
		                 ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO}, ConstantsAdmin.KEY_APELLIDO + " LIKE '%" + param +"%' OR " + ConstantsAdmin.KEY_NOMBRES + " LIKE '%" + param + "%'", null, null, null, sortOrder);
		    	 }else{
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,
		    				 ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO}, null, null, null, null, sortOrder);
		    	 }
    		 }else{// Categoria distinta de 0, es selectivo
		    	 if(param != null && !param.equals("")){
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,
		    				 ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO}, "(" + ConstantsAdmin.KEY_APELLIDO + " LIKE '%" + param +"%' OR " + ConstantsAdmin.KEY_NOMBRES + " LIKE '%" + param + "%') AND (" + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + "='" + categoria.getNombreReal() + "')", null, null, null, sortOrder);
		    	 }else{
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,
		    				 ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO}, ConstantsAdmin.KEY_NOMBRE_CATEGORIA + "= '" + categoria.getNombreReal() + "'", null, null, null, sortOrder);
		    	 }

    		 }
    	 }catch (SQLiteException e) {
			e.getMessage();
		}
    	 return result;

     }
*/

     /*
     public Cursor fetchAllPersonaPorApellidoONombreODatosCategoria(String param, CategoriaDTO categoria) {
    	 String sortOrder = ConstantsAdmin.KEY_APELLIDO + " COLLATE LOCALIZED ASC";
    	 Cursor result = null;
    	 String consulta = ConstantsAdmin.KEY_APELLIDO + " LIKE '%" + param +"%' OR " + ConstantsAdmin.KEY_NOMBRES + " LIKE '%" + param + "%' OR " + ConstantsAdmin.KEY_DATO_EXTRA + " LIKE '%" + param + "%' OR " + ConstantsAdmin.KEY_DESCRIPCION + " LIKE '%" + param + "%'";
    	 String[] atr = new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_APELLIDO,ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_DATO_EXTRA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO, ConstantsAdmin.KEY_DESCRIPCION};
    	 try{
    		 if(categoria == null || categoria.getId() == 0){// Categoria 0 - incluye todos
		    	 if(param != null && !param.equals("")){
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, consulta , null, null, null, sortOrder);
		    	 }else{
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, null, null, null, null, sortOrder);
		    	 }
    		 }else{// Categoria distinta de 0, es selectivo
		    	 if(param != null && !param.equals("")){
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, "(" + consulta + ") AND (" + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + "='" + categoria.getNombreReal() + "')", null, null, null, sortOrder);
		    	 }else{
		    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, atr, ConstantsAdmin.KEY_NOMBRE_CATEGORIA + "= '" + categoria.getNombreReal() + "'", null, null, null, sortOrder);
		    	 }

    		 }
    	 }catch (SQLiteException e) {
			e.getMessage();
		}
    	 return result;

     }*/
/*
     public Cursor fetchAllPersonas() {
    	 String sortOrder = ConstantsAdmin.KEY_APELLIDO + " COLLATE LOCALIZED ASC";
    	 Cursor result = null;
    	 try{

    		 result = mDb.query(ConstantsAdmin.TABLA_PERSONA, null, null, null, null, null, sortOrder);
    	 }catch (SQLiteException e) {
			e.getMessage();
		}
    	return result;
    	 
     }
    */





     
     public Cursor fetchAllCategoriasPorNombre(String paramNombre) {
    	 Cursor result;
    	 if(paramNombre != null && !paramNombre.equals("")){
    		 result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " LIKE '%" + paramNombre + "%'", null, null, null, null);
    	 }else{
    		 result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, null, null, null, null, null);
    	 }
         return result;
     }
     /*
     public Cursor fetchAllCategoriasPersonalesPorNombre(String paramNombre) {
    	 Cursor result;
    	 if(paramNombre != null && !paramNombre.equals("")){
    		 result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " LIKE '%" + paramNombre + "%'", null, null, null, null);
    	 }else{
    		 result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, null, null, null, null, null);
    	 }
         return result;
     }
*/
     /*
     public Cursor fetchCategoriasActivasPorNombre(String paramNombre) {
    	 Cursor result;
    	 if(paramNombre != null && !paramNombre.equals("")){
    		 result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA},"(" + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " LIKE '%" + paramNombre + "%') AND (" + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " = 1)", null, null, null, null);
    	 }else{
    		 result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, "(" + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " = 1)", null, null, null, null);
    	 }
         return result;
     }
*/
     /*
	public Cursor fetchCategoriasPersonalesActivasPorNombre(String paramNombre) {
		Cursor result;
		if(paramNombre != null && !paramNombre.equals("")){
			result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA},"(" + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " LIKE '%" + paramNombre + "%') AND (" + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " = 1)", null, null, null, null);
		}else{
			result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, "(" + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " = 1)", null, null, null, null);
		}
		return result;
	}
*/

	public CursorLoader cursorLoaderCategoriasPersonalesActivasPorNombre(String paramNombre, Context context) {
		String selection = null;
		if(paramNombre != null && !paramNombre.equals("")){
			// result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA},"(" + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " LIKE '%" + paramNombre + "%') AND (" + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " = 1)", null, null, null, null);
			selection  = " LIKE '%" + paramNombre + "%') AND (" + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " = 1)";
		}else{
			selection  = "(" + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " = 1)";
		}

		return new CursorLoader( context, null, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				}
				return c;
			}
		};

	}

	public CursorLoader cursorLoaderCategoriasPersonalesPorNombre(String paramNombre, Context context) {
		String selection = null;
		if(paramNombre != null && !paramNombre.equals("")){
			// result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA},"(" + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " LIKE '%" + paramNombre + "%') AND (" + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " = 1)", null, null, null, null);
			selection  = " LIKE '%" + paramNombre + "%') ";
		}

		return new CursorLoader( context, null, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
					if (c != null) {
						c.moveToFirst();
					}
				}
				return c;
			}
		};

	}



	public CursorLoader cursorLoaderCategoriasActivasPorNombre(String paramNombre, Context context) {
		String selection = null;
		selection = ConstantsAdmin.querySelectionCategoriaActivaByName(paramNombre);

		return new CursorLoader( context, null, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query( ConstantsAdmin.TABLA_CATEGORIA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				}
				return c;
			}
		};

	}

	public CursorLoader cursorLoaderCategoriasPorNombre(String paramNombre, Context context) {
		String selection = null;
		if(paramNombre != null && !paramNombre.equals("")){
			// result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA},"(" + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " LIKE '%" + paramNombre + "%') AND (" + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " = 1)", null, null, null, null);
			selection  = ConstantsAdmin.querySelectionCategoriaByName(paramNombre);
		}

		return new CursorLoader( context, null, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA, ConstantsAdmin.KEY_CATEGORIA_ACTIVA, ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA}, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query( ConstantsAdmin.TABLA_CATEGORIA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				}
				return c;
			}
		};

	}



	public CursorLoader cursorLoaderPersonas(boolean noActivaContrasenia, List<CategoriaDTO> categoriasProtegidas, Context context) {
		String sortOrder = ConstantsAdmin.KEY_APELLIDO + " COLLATE LOCALIZED ASC";
//		String selection = " (1 = 1) ";
//		if(noActivaContraseña){
		String selection = this.queryParaCategoriaProtegidas(noActivaContrasenia, categoriasProtegidas);
//		}


		return new CursorLoader( context, null, null, selection, null, sortOrder)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(ConstantsAdmin.TABLA_PERSONA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				}
				return c;
			}
		};

	}

/*
	public Cursor fetchAllPersonas(List<CategoriaDTO> categoriasProtegidas) {
		String sortOrder = ConstantsAdmin.KEY_APELLIDO + " COLLATE LOCALIZED ASC";
		Cursor result = null;
		try{
			String categProteg = " (1 = 1) ";
			if(ConstantsAdmin.contrasenia != null && !ConstantsAdmin.contrasenia.isActiva()){
				categProteg = categProteg + this.queryParaCategoriaProtegidas(categoriasProtegidas);
			}

			result = mDb.query(ConstantsAdmin.TABLA_PERSONA, null, categProteg, null, null, null, sortOrder);
		}catch (SQLiteException e) {
			e.getMessage();
		}
		return result;

	}
*/


     /*
     private Cursor fetchPersonaString(String column, Object value) throws SQLException {
         Cursor mCursor = null;
    	 try{
    		 mCursor =
             mDb.query(true, ConstantsAdmin.TABLA_PERSONA, null, column + "= '" + value + "'" , null,
                     null, null, null, null);
    		 if (mCursor != null) {
    			 mCursor.moveToFirst();
    		 }
    	 }catch (Exception e) {
			// TODO: handle exception
    		 e.getMessage();
		}
         return mCursor;
     }
     
     */

     /*
     private Cursor fetchPersonaString(String column1, String column2, Object value1, Object value2) throws SQLException {
         Cursor mCursor = null;
    	 try{
    		 String valor;
    		 if(value1 != null && value2 != null && !"".equals(value1) && !"".equals(value2)){
    			 valor = "TRIM(UPPER(" + column1 + ")) = TRIM(UPPER('" + value1 + "')) AND TRIM(UPPER(" + column2 +")) = TRIM(UPPER('" + value2 + "'))";
    		 }else  if(value1 != null && !"".equals(value1)){
       			 valor = "TRIM(UPPER(" + column2 + ")) = TRIM(UPPER('" + value1 + "')) AND (" + column1 + " IS NULL)";
    		 }else{
       			 valor = "TRIM(UPPER(" + column2 +")) = TRIM(UPPER('" + value2 + "')) AND (" + column1 + " IS NULL)";
    		 }
    		 mCursor =
             mDb.query(true, ConstantsAdmin.TABLA_PERSONA, null, valor, null,
                     null, null, null, null);
    		 if (mCursor != null) {
    			 mCursor.moveToFirst();
    		 }
    	 }catch (Exception e) {
			// TODO: handle exception
    		 e.getMessage();
		}
         return mCursor;
     }
*/
    public CursorLoader cursorLoaderNombreApellidoPersona (String nombre, String apellido, Context context) {

        String selection = ConstantsAdmin.querySelectionNombreYApellidoPersona(nombre, apellido);

        return new CursorLoader( context, null, null, selection, null, null)
        {
            @Override
            public Cursor loadInBackground()
            {
                // You better know how to get your database.
                // You can use any query that returns a cursor.
                Cursor c = null;
                if(mDb.isOpen()){
                    c = mDb.query(true, ConstantsAdmin.TABLA_PERSONA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
                    if (c != null) {
                        c.moveToFirst();
                    }
                }
                return c;
            }
        };

    }


	public CursorLoader cursorLoaderIdAgenda(String idAgenda, Context context) {

		String selection = ConstantsAdmin.querySelectionByIdAgenda(idAgenda);

		return new CursorLoader( context, null, null, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(true, ConstantsAdmin.TABLA_PERSONA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
					if (c != null) {
						c.moveToFirst();
					}
				}
				return c;
			}
		};

	}

	private CursorLoader cursorLoaderPersonasPorCampo(String column, Object value, Context context) {
		String selection = ConstantsAdmin.querySelectionColumnByValue(column, value);

		return new CursorLoader( context, null, null, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(ConstantsAdmin.TABLA_PERSONA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
					if (c != null) {
						c.moveToFirst();
					}
				}
				return c;
			}
		};

	}


	public CursorLoader cursorLoaderPersonaId(String contactId, Context context, ContentResolver mDbContentResolver) {
		String selection = ConstantsAdmin.querySelectionContactsPhoneById + contactId;
		final ContentResolver cr = mDbContentResolver;
		return new CursorLoader( context, null, null, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				//c = mDb.query(ConstantsAdmin.TABLA_PERSONA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				return cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, getProjection() , getSelection() ,null, null);
			}
		};

	}

	public CursorLoader cursorLoaderEmailPersona (String contactId, Context context, ContentResolver mDbContentResolver) {
		final ContentResolver cr = mDbContentResolver;
		String selection = ConstantsAdmin.querySelectionEmailContactsById + contactId;
		//Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projectionMail, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);

		//String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		String[] projectionMail = ConstantsAdmin.projectionMail;

		return new CursorLoader( context, ContactsContract.CommonDataKinds.Email.CONTENT_URI, projectionMail, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				//c = mDb.query(ConstantsAdmin.TABLA_PERSONA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				return cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, getProjection() , getSelection() ,getSelectionArgs(), null);
			}
		};

	}

	public CursorLoader cursorLoaderPhonePersona (String contactId, Context context, ContentResolver mDbContentResolver) {

	//	Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
		final ContentResolver cr = mDbContentResolver;
		String selection = ConstantsAdmin.querySelectionPhoneContactsById + contactId;
	//	String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		String[] projectionPhone = ConstantsAdmin.projectionPhone;

		return new CursorLoader( context, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				//c = mDb.query(ConstantsAdmin.TABLA_PERSONA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				return cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, getProjection() , getSelection() ,getSelectionArgs(), null);
			}
		};

	}

	public CursorLoader cursorLoaderDirPersona (String contactId, Context context, ContentResolver mDbContentResolver) {

		//	Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
		final ContentResolver cr = mDbContentResolver;
		String selection = ConstantsAdmin.querySelectionDirsContactsById + contactId;
		//	String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		String[] projectionDirs = ConstantsAdmin.projectionDirs;

		return new CursorLoader( context, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, projectionDirs, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				//c = mDb.query(ConstantsAdmin.TABLA_PERSONA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				return cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, getProjection() , getSelection() ,getSelectionArgs(), null);
			}
		};

	}



    public CursorLoader cursorLoaderPersonaExtraId(String contactId, Context context, ContentResolver mDbContentResolver) {
		final ContentResolver cr = mDbContentResolver;
		//String selection = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + "=" + contactId;
		String selection = ConstantsAdmin.querySelectionContactsById + contactId;
		//String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		//nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, null);
		//	String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + "=" + idContact;
		String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };


		return new CursorLoader( context, null, null, selection, whereNameParams, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				//c = mDb.query(ConstantsAdmin.TABLA_PERSONA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				return cr.query(ContactsContract.Data.CONTENT_URI, getProjection() , getSelection() ,getSelectionArgs(), null);
			}
		};

	}



	private Cursor fetchPersonaNumber(String column, Object value) throws SQLException {
		Cursor mCursor = null;
		mCursor =
				mDb.query(true, ConstantsAdmin.TABLA_PERSONA, null, column + "= '" + value + "'" , null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}



     private Cursor fetchCategoriaPersonalNumber(String column, Object value) throws SQLException {
         Cursor mCursor = null;
		 mCursor =
		 mDb.query(true, ConstantsAdmin.TABLA_CATEGORIA_PERSONALES, null, column + "= '" + value + "'" , null,
				 null, null, null, null);
		 if (mCursor != null) {
			 mCursor.moveToFirst();
		 }

         return mCursor;
     }


     public Cursor fetchPersonaPorId(long id){
    	 return this.fetchPersonaNumber(ConstantsAdmin.KEY_ROWID, id);

     }

	public CursorLoader cursorLoaderPersonaPorId(long id, Context context){
		return this.cursorLoaderPersonasPorCampo(ConstantsAdmin.KEY_ROWID, id, context);

	}

	public CursorLoader cursorLoaderPreferidoPorId(long id, Context context){
		return this.cursorLoaderPreferidoPorCampo(ConstantsAdmin.KEY_ROWID, id, context);

	}


	private CursorLoader cursorLoaderPreferidoPorCampo(String column, Object value, Context context) {
		String selection = ConstantsAdmin.querySelectionColumnByValue(column, value);

		return new CursorLoader( context, null, null, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(ConstantsAdmin.TABLA_PREFERIDOS, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
					if(c != null){
						c.moveToFirst();
					}
				}
				return c;
			}
		};

	}
/*
     public Cursor fetchPreferidoPorId(long id){
    //	 return this.fetchPersonaNumber(ConstantsAdmin.KEY_ROWID, id);
	     Cursor mCursor = null;
		 try{
			 mCursor =
	         mDb.query(true, ConstantsAdmin.TABLA_PREFERIDOS, null, ConstantsAdmin.KEY_ROWID + "= '" + id + "'" , null,
	                 null, null, null, null);
			 if (mCursor != null) {
				 mCursor.moveToFirst();
			 }
		 }catch (Exception e) {
			// TODO: handle exception
			 e.getMessage();
		 }
	     return mCursor;
  	 
     }*/
/*
     public Cursor fetchTelefonoPorIdPersona(long id){
    	 return fetchTipoValorPorIdPersona(id,ConstantsAdmin.TABLA_TELEFONOS);
     }


     */
     /*
     public Cursor fetchEmailPorIdPersona(long id){
    	 return fetchTipoValorPorIdPersona(id,ConstantsAdmin.TABLA_EMAILS);
     }

     */
/*
     public Cursor fetchDireccionPorIdPersona(long id){
    	 return fetchTipoValorPorIdPersona(id,ConstantsAdmin.TABLA_DIRECCIONES);
     }

*/
     public Cursor fetchTipoValorPorIdPersona(long id, String tipo, String nombreTabla){
    	    //	 return this.fetchPersonaNumber(ConstantsAdmin.KEY_ROWID, id);
	     Cursor mCursor;
	     String selection = ConstantsAdmin.KEY_ID_PERSONA + "= '" + id + "' AND " + ConstantsAdmin.KEY_TIPO + " = '" + tipo + "'";
		 mCursor =
         mDb.query(true, nombreTabla, null, selection, null,
                 null, null, null, null);
		 if (mCursor != null) {
			 mCursor.moveToFirst();
		 }

	     return mCursor;
    	  	 
     }

	public Cursor fetchTipoValorPorIdPersona(long id, String nombreTabla){
		//	 return this.fetchPersonaNumber(ConstantsAdmin.KEY_ROWID, id);
		Cursor mCursor;
		String selection = ConstantsAdmin.KEY_ID_PERSONA + "= '" + id + "'";
		mCursor =
				mDb.query(true, nombreTabla, null, selection, null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;

	}
     

     
     public Cursor fetchCategoriaPersonalPorId(long id){
    	 return this.fetchCategoriaPersonalNumber(ConstantsAdmin.KEY_ROWID, id);
  	 
     }
     /*
     public Cursor fetchPersonaPorApellido(String ap){
    	 return this.fetchPersonaString(ConstantsAdmin.KEY_APELLIDO, ap);
  	 
     }
     */

     /*
     public Cursor fetchPersonaPorNombre(String nom){
    	 return this.fetchPersonaString(ConstantsAdmin.KEY_NOMBRES, nom);
  	 
     }*/
     /*
     public Cursor fetchPersonaPorNombreYApellido(String nom, String apellido){
    	 return this.fetchPersonaString(ConstantsAdmin.KEY_NOMBRES, ConstantsAdmin.KEY_APELLIDO, nom, apellido);
  	 
     }
     */
     // CAMBIOS PARA AGREGAR CONTRASENIA EN LAS CATEGORIAS
     /*
     public Cursor fetchAllCategoriasProtegidasPorNombre(String paramNombre) {
    	 Cursor result;
    	 if(paramNombre != null && !paramNombre.equals("")){
    		 result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA_PROTEGIDA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA}, ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " LIKE '%" + paramNombre + "%'", null, null, null, null);
    	 }else{
    		 result = mDb.query(ConstantsAdmin.TABLA_CATEGORIA_PROTEGIDA, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA}, null, null, null, null, null);
    	 }
         return result;
     }*/

	public CursorLoader cursorLoaderCategoriasProtegidas(Context context) {
/*		String selection = null;
		if(paramNombre != null && !paramNombre.equals("")){
			selection  = ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " LIKE '%" + paramNombre + "%'";
		}
*/
		return new CursorLoader( context, null, new String[] {ConstantsAdmin.KEY_ROWID, ConstantsAdmin.KEY_NOMBRE_CATEGORIA}, null, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(ConstantsAdmin.TABLA_CATEGORIA_PROTEGIDA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
				}
				return c;
			}
		};

	}



    public CursorLoader cursorLoaderContrasenia(Context context) {

        return new CursorLoader( context, null, null, null, null, null)
        {
            @Override
            public Cursor loadInBackground()
            {
                // You better know how to get your database.
                // You can use any query that returns a cursor.
                Cursor c = null;
                if(mDb.isOpen()){
                    c = mDb.query(ConstantsAdmin.TABLA_CONTRASENIA, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
                }
                return c;
            }
        };

    }


     /*
     public Cursor fetchContrasenia() {
    	 Cursor result = null;
    	 try{

    		 result = mDb.query(ConstantsAdmin.TABLA_CONTRASENIA, null, null, null, null, null, null);
    	 }catch (SQLiteException e) {
			e.getMessage();
		}
    	return result;
    	 
     }
*/


     public CursorLoader cursorLoaderConfiguracion(Context context){

             return new CursorLoader( context, null, null, null, null, null)
             {
                 @Override
                 public Cursor loadInBackground()
                 {
                     // You better know how to get your database.
                     // You can use any query that returns a cursor.
                     Cursor c = null;
                     if(mDb.isOpen()){
                         c = mDb.query(ConstantsAdmin.TABLA_CONFIGURACION, null, null, null, null, null, null);
                     }
                     return c;
                 }
             };

     }

/*
     public Cursor fetchConfig() {
    	 Cursor result = null;
    	 try{

    		 result = mDb.query(ConstantsAdmin.TABLA_CONFIGURACION, null, null, null, null, null, null);
    	 }catch (SQLiteException e) {
			e.getMessage();
		}
    	return result;
    	 
     }
     */
     public void crearCategoriaProtegida(CategoriaDTO categoria){
    	 //long returnValue = -1;
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_NOMBRE_CATEGORIA, categoria.getNombreReal());
     //    	this.open();
		 mDb.insert(ConstantsAdmin.TABLA_CATEGORIA_PROTEGIDA, null, initialValues);
     //    	this.close();

	 }
     
     
     public long crearContrasenia(ContraseniaDTO contrasenia){
    	 long id = -1;
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_CONTRASENIA, contrasenia.getContrasenia());
         if(contrasenia.isActiva()){
        	 initialValues.put(ConstantsAdmin.KEY_CONTRASENIA_ACTIVA, 1);
         }else{
        	 initialValues.put(ConstantsAdmin.KEY_CONTRASENIA_ACTIVA, 0);
         }
         initialValues.put(ConstantsAdmin.KEY_MAIL1, contrasenia.getMail());
		 if(contrasenia.getId() == -1 ){
			 id = mDb.insert(ConstantsAdmin.TABLA_CONTRASENIA, null, initialValues);
		 }else{
			 id = mDb.update(ConstantsAdmin.TABLA_CONTRASENIA, initialValues, ConstantsAdmin.KEY_ROWID + "=" + contrasenia.getId() , null);
		 }

         return id;
     }
     
     
     public void eliminarCategoriaProtegida(String nombreCategoria){
		 mDb.delete(ConstantsAdmin.TABLA_CATEGORIA_PROTEGIDA, ConstantsAdmin.KEY_NOMBRE_CATEGORIA + "='" + nombreCategoria + "'", null);
	 }
     
     

}
