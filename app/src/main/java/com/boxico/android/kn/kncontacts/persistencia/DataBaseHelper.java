package com.boxico.android.kn.kncontacts.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.boxico.android.kn.kncontacts.util.ConstantsAdmin;

class DataBaseHelper extends SQLiteOpenHelper{
	
	 
	private static final String DATABASE_CREATE_PERSONA = "create table if not exists " + ConstantsAdmin.TABLA_PERSONA + 
            "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
            + ConstantsAdmin.KEY_APELLIDO + " text, "
            + ConstantsAdmin.KEY_NOMBRES + " text, "
            + ConstantsAdmin.KEY_FECHA_NACIMIENTO + " text, "
        /*    + ConstantsAdmin.KEY_TEL_PARTICULAR + " text, "
            + ConstantsAdmin.KEY_TEL_CELULAR + " text, "
            + ConstantsAdmin.KEY_TEL_LABORAL + " text, "
            + ConstantsAdmin.KEY_EMAIL_PARTICULAR + " text, "
            + ConstantsAdmin.KEY_EMAIL_LABORAL + " text, "
            + ConstantsAdmin.KEY_EMAIL_OTRO + " text, "*/
            + ConstantsAdmin.KEY_CATEGORIA + " integer not null, "
            + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " text not null, "
            + ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO + " text not null, "
            + ConstantsAdmin.KEY_DESCRIPCION + " text, "
         /*   + ConstantsAdmin.KEY_DIRECCION_LABORAL + " text, "
            + ConstantsAdmin.KEY_DIRECCION_PARTICULAR + " text, "*/
            + ConstantsAdmin.KEY_DATO_EXTRA + " text, "
            + ConstantsAdmin.KEY_ID_PERSONA_AGENDA + " text);";
	
  
    private static final String DATABASE_CREATE_CATEGORIAS = "create table if not exists " + ConstantsAdmin.TABLA_CATEGORIA + 
            "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
            + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " integer not null, "
            + ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA + " text not null default 'cambiar', "
            + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " text not null);";


 //   "' ADD COLUMN '" + ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA + "' TEXT NOT NULL DEFAULT 'cambiar'";
    
    private static final String DATABASE_CREATE_PREFERIDOS = "create table if not exists " + ConstantsAdmin.TABLA_PREFERIDOS + 
    "(" + ConstantsAdmin.KEY_ROWID +" integer not null);";
    
    private static final String DATABASE_CREATE_CATEGORIAS_PERSONALES = "create table if not exists " + ConstantsAdmin.TABLA_CATEGORIA_PERSONALES + 
    "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
    + ConstantsAdmin.KEY_CATEGORIA_ACTIVA + " integer not null, "
    + ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA + " text not null, "
    + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " text not null);";
    
    private static final String DATABASE_CREATE_TELEFONOS = "create table if not exists " + ConstantsAdmin.TABLA_TELEFONOS + 
    "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
    + ConstantsAdmin.KEY_VALOR + " text not null, "
    + ConstantsAdmin.KEY_TIPO + " text not null, "
    + ConstantsAdmin.KEY_ID_PERSONA + " text not null);";    

    private static final String DATABASE_CREATE_EMAILS = "create table if not exists " + ConstantsAdmin.TABLA_EMAILS + 
    "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
    + ConstantsAdmin.KEY_VALOR + " text not null, "
    + ConstantsAdmin.KEY_TIPO + " text not null, "
    + ConstantsAdmin.KEY_ID_PERSONA + " text not null);";  
    
    private static final String DATABASE_CREATE_DIRECCIONES = "create table if not exists " + ConstantsAdmin.TABLA_DIRECCIONES + 
    "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
    + ConstantsAdmin.KEY_VALOR + " text not null, "
    + ConstantsAdmin.KEY_TIPO + " text not null, "
    + ConstantsAdmin.KEY_ID_PERSONA + " text not null);";      
    
    // --Commented out by Inspection (13/11/2018 12:26):public static final String SIZE_PERSONAS = "select count(" + ConstantsAdmin.KEY_ROWID +") from " + ConstantsAdmin.TABLA_PERSONA + "  where " + ConstantsAdmin.KEY_ROWID + " > 0";
    
    public static final String SIZE_PREFERIDOS = "select count(" + ConstantsAdmin.KEY_ROWID +") from " + ConstantsAdmin.TABLA_PREFERIDOS + "  where " + ConstantsAdmin.KEY_ROWID + " > 0";
    
    
	public static final String SIZE_CATEGORIAS = "select count(" + ConstantsAdmin.KEY_ROWID + ") from " + ConstantsAdmin.TABLA_CATEGORIA + " where " + ConstantsAdmin.KEY_ROWID + " > 0" ;
    
	public static final String SIZE_CONTRASENIA = "select count(" + ConstantsAdmin.KEY_ROWID + ") from " + ConstantsAdmin.TABLA_CONTRASENIA + " where " + ConstantsAdmin.KEY_ROWID + " > 0" ;

	
	public static final String ACTUALIZAR_TABLA_CATEGORIAS = "ALTER TABLE '" + ConstantsAdmin.TABLA_CATEGORIA + "' ADD COLUMN '" + ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA + "' TEXT NOT NULL DEFAULT 'cambiar'";
	
	public static final String ACTUALIZAR_TABLA_CONTRASENIA = "ALTER TABLE '" + ConstantsAdmin.TABLA_CONTRASENIA + "' ADD COLUMN '" + ConstantsAdmin.KEY_CONTRASENIA_ACTIVA + "' integer NOT NULL DEFAULT 0";

    public static final String ACTUALIZAR_TABLA_PERSONA = "ALTER TABLE '" + ConstantsAdmin.TABLA_PERSONA + "' ADD COLUMN '" + ConstantsAdmin.KEY_ID_PERSONA_AGENDA + "' TEXT";

    private static final String DATABASE_CREATE_CONFIG = "create table if not exists " + ConstantsAdmin.TABLA_CONFIGURACION  + 
    "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
    + ConstantsAdmin.KEY_ORDENADO_POR_CATEGORIA + " integer, "
    + ConstantsAdmin.KEY_ESTAN_DETALLADOS + " integer, "
    + ConstantsAdmin.KEY_LISTA_EXPANDIDA + " integer, "
    + ConstantsAdmin.KEY_MUESTRA_PREFERIDOS + " integer);";      

	
	public DataBaseHelper(Context context) {
         super(context, ConstantsAdmin.DATABASE_NAME, null, ConstantsAdmin.DATABASE_VERSION);
    }

	 @Override
     public void onCreate(SQLiteDatabase db) {
         db.execSQL(DATABASE_CREATE_PERSONA);
         db.execSQL(DATABASE_CREATE_CATEGORIAS);
         db.execSQL(DATABASE_CREATE_CATEGORIAS_PERSONALES);
         db.execSQL(DATABASE_CREATE_PREFERIDOS);
         db.execSQL(DATABASE_CREATE_DIRECCIONES);
         db.execSQL(DATABASE_CREATE_TELEFONOS);
         db.execSQL(DATABASE_CREATE_EMAILS);
         db.execSQL(DATABASE_CREATE_CONTRASENIAS);
         db.execSQL(DATABASE_CREATE_CATEGORIA_PROTEGIDA);
         db.execSQL(DATABASE_CREATE_CONFIG);

     }

     public void deleteAll(SQLiteDatabase db) {
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_PERSONA + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_CATEGORIA + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_CATEGORIA_PERSONALES + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_PREFERIDOS + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_DIRECCIONES + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_TELEFONOS + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_EMAILS + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_CONTRASENIA + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_CATEGORIA_PROTEGIDA + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
         db.execSQL("DELETE FROM " + ConstantsAdmin.TABLA_CONFIGURACION + " WHERE " + ConstantsAdmin.KEY_ROWID + " > -1");
                  
         onCreate(db);
     }
	 
     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         Log.w(ConstantsAdmin.TAG, "Upgrading database from version " + oldVersion + " to "
                 + newVersion + ", which will destroy all old data");
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_PERSONA);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_CATEGORIA);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_CATEGORIA_PERSONALES);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_PREFERIDOS);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_DIRECCIONES);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_TELEFONOS);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_EMAILS);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_CONTRASENIA);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_CATEGORIA_PROTEGIDA);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLA_CONFIGURACION);

         onCreate(db);
     }
     
     // CAMBIOS PARA AGREGAR CONTRASENIA EN LAS CATEGORIAS
     
     private static final String DATABASE_CREATE_CONTRASENIAS = "create table if not exists " + ConstantsAdmin.TABLA_CONTRASENIA + 
     "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
     + ConstantsAdmin.KEY_CONTRASENIA + " text not null, "
     + ConstantsAdmin.KEY_MAIL1 + " text, "
     + ConstantsAdmin.KEY_CONTRASENIA_ACTIVA + " integer not null default 1, "
     + ConstantsAdmin.KEY_MAIL2 + " text);";




     private static final String DATABASE_CREATE_CATEGORIA_PROTEGIDA = "create table if not exists " + ConstantsAdmin.TABLA_CATEGORIA_PROTEGIDA + 
     "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
     + ConstantsAdmin.KEY_NOMBRE_CATEGORIA + " text not null);";
     
     
     
	 	

}
