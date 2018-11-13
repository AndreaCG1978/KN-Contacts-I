package com.boxico.android.kn.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.contacts.util.Asociacion;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;

public class ImportarContactoActivity extends Activity {
	
	private ArrayAdapter<CategoriaDTO> mSpinnerAdapt = null;
	private CategoriaDTO mCategoriaSeleccionada = null;
	private Cursor people = null;
	private ImportarContactoActivity me = null;
	// --Commented out by Inspection (12/11/2018 12:34):boolean haciaAdelante = true;
	private ArrayList<Cursor> allMyCursors = null;
	
	private TextView mPersonaEncontrada;
	private TextView mTipoPersonaEncontrada;
	private TextView entryDatoExtra;
	private TextView entryDescripcion;
	
	private Spinner mSpinner = null;

	private String given = "";
	private String family = "";
	private int posPeople = 0;

	private Button botonAddContact = null;
	private Button botonSkipContact = null;
	private Button botonPrevContact = null;
	private Button botonAddAll = null;
	private Button botonSkipAll = null;
	private Asociacion contactoActual = null;
	private PersonaDTO persona = null;
	private String contactId = null;
	

    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }
 
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.menu_importar_contactos));
        try {
        	allMyCursors = new ArrayList<>();
            this.setContentView(R.layout.import_contact);
            this.registrarWidgets();
            
            me = this;
            this.configurarSpinner();
            this.configurarBotones();
            
		} catch (Exception e) {
		// TODO: handle exception
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorInicioAplicacion));
		}

	}
	
	private void buscarSiguienteContacto() {
    	boolean encontrado = false;
    	String contactIdTemp;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	
    	while(!encontrado && people.moveToNext()) {
    		posPeople = people.getPosition();
    		contactIdTemp  = people.getString(people.getColumnIndex(ContactsContract.Contacts._ID));
   	       	contactoActual = this.obtenerNombreYApellidoDeContactoDeAgenda(contactIdTemp);
   	       	if(contactoActual != null){
   	   	       	if(contactId == null || !contactId.equals(contactIdTemp)){
   	     	       //	if(!given.equals(contactoActual.getKey()) && !family.equals(contactoActual.getValue())){
     	       		contactId = contactIdTemp;
     	       		given = (String)contactoActual.getKey();
     	       		family = (String)contactoActual.getValue();
     	       		if(given == null){
     	       			given = "";
     	       		}
     	       		if(family == null){
     	       			family = "";
     	       		}

     	       		if(!family.equals("") || !given.equals("")){
   	  	        	   persona = ConstantsAdmin.obtenerPersonaConNombreYApellido(given,family,this, mDBManager);
   	  	        	   if(persona == null){
   	  	        		   persona = new PersonaDTO();   
   	  	        	   }
   	  			       persona.setApellido(family);
   	  			       persona.setNombres(given);
   	  		   		   if(persona.getNombres() != null && (persona.getApellido() == null || persona.getApellido().equals(""))){
   	  		   			   persona.setApellido(persona.getNombres());
   	  		   			   persona.setNombres(null);
   	  				   }
   	  		   		   encontrado = true;
   	  		   	       mTipoPersonaEncontrada.setText("");
   	  		   		   this.mostrarDatosPersonaEncontrada();
   	     	   	    }
   	     	    	   
   	     	    }
   	       		
   	       	}
    	}
    	if(posPeople < people.getCount() - 1){
    		botonSkipContact.setEnabled(true);
    		botonSkipContact.setTextColor(getResources().getColor(R.color.color_azul));
    	}else{
    		botonSkipContact.setEnabled(false);
    		botonSkipContact.setTextColor(getResources().getColor(R.color.color_azul_oscuro));
    	}
    	if(posPeople > 0){
    		botonPrevContact.setEnabled(true);
    		botonPrevContact.setTextColor(getResources().getColor(R.color.color_azul));
    	}
	}


	private void buscarAnteriorContacto() {
    	given = null;
    	family = null;
    	boolean encontrado = false;
    	String contactIdTemp;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
       	while(!encontrado && people.moveToPrevious()) {
       	   posPeople = people.getPosition();
	       contactIdTemp  = people.getString(people.getColumnIndex(ContactsContract.Contacts._ID));
   	       contactoActual = this.obtenerNombreYApellidoDeContactoDeAgenda(contactIdTemp);
   	       if(contactoActual != null){
	           if(contactId == null || !contactId.equals(contactIdTemp)){
	        	   contactId = contactIdTemp;
	   //	       if(!given.equals(contactoActual.getKey()) && !family.equals(contactoActual.getValue())){
	   		       given = (String)contactoActual.getKey();
	   	   	       family = (String)contactoActual.getValue();
	   	   	       if(given == null){
	   	   	    	   given = "";
	   	   	       }
	   	   	       if(family == null){
	   	   	    	   family = "";
	   	   	       }
	   	   	       if((!family.equals("") || given != null && !given.equals("")) && (family != null || given != null)){
	
		        	   persona = ConstantsAdmin.obtenerPersonaConNombreYApellido(given,family,this, mDBManager);
		        	   if(persona == null){
		        		   persona = new PersonaDTO();   
		        	   }
				       persona.setApellido(family);
				       persona.setNombres(given);
			   		   if(persona.getNombres() != null && (persona.getApellido() == null || persona.getApellido().equals(""))){
			   			   persona.setApellido(persona.getNombres());
			   			   persona.setNombres(null);
					   }
		   			   encontrado = true;
		   			   mTipoPersonaEncontrada.setText("");
		   		   	   this.mostrarDatosPersonaEncontrada();
	
	   	   	       }
	   	       }
   	       }
    	}
    	if(posPeople == 0){
    		botonPrevContact.setEnabled(false);
    		botonPrevContact.setTextColor(getResources().getColor(R.color.color_azul_oscuro));
    	}else{
    		botonPrevContact.setEnabled(true);
    		botonPrevContact.setTextColor(getResources().getColor(R.color.color_azul));
    	}
    	if(posPeople < people.getCount()){
    		botonSkipContact.setEnabled(true);
    		botonSkipContact.setTextColor(getResources().getColor(R.color.color_azul));
    	}
	
	}
	
	
	
	private void mostrarDatosPersonaEncontrada(){
		String text;
    	if(persona != null){
    		mPersonaEncontrada.setText("");
    		if(persona.getNombres() != null && !persona.getNombres().equals("")){
    			mPersonaEncontrada.setText(persona.getApellido() + ", " + persona.getNombres());
    		}else{
    			mPersonaEncontrada.setText(persona.getApellido());	
    		}
    		mPersonaEncontrada.setText(mPersonaEncontrada.getText());
    		if(persona.getId() == -1){
    			this.seleccionarPrimerCategoria();
    			text = getResources().getString(R.string.label_nuevo_contacto);
    			botonAddContact.setText(getResources().getString(R.string.label_agregar));
    			entryDatoExtra.setText("");
    			entryDescripcion.setText("");
    			
    		}else{
    			this.seleccionarCategoriaContactoExistente();
    			text = getResources().getString(R.string.label_contacto_existente)+ persona.getCategoriaNombreRelativo() + ")";
    			botonAddContact.setText(getResources().getString(R.string.label_actualizar));
    			entryDatoExtra.setText(persona.getDatoExtra());
    			entryDescripcion.setText(persona.getDescripcion());
    		}
   // 		tipoPersona = people.getString(people.getColumnIndex(ContactsContract.Data.));
    		mTipoPersonaEncontrada.setText(text);
    	}
		
	}

	private void seleccionarCategoriaContactoExistente() {
		boolean encontrado = false;
	    int pos = 0;
	    CategoriaDTO cat;
	    int total = mSpinnerAdapt.getCount();
	    while(!encontrado && pos < total){
	    	
	    	cat = mSpinnerAdapt.getItem(pos);
	    	if(cat.getNombreReal().equals(persona.getCategoriaNombre())){
	    		encontrado = true;
	    		mCategoriaSeleccionada = cat;
	    	}
	    	pos++;
	    }
	    if(encontrado){
	    	mSpinner.setSelection(pos - 1);
	    }

		
	}


	private void  configurarBotones(){
		botonAddContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	addContact();
            }
        });
		botonAddAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	addAll();
            }
        });			
		botonSkipContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	skipContact();
            }
        });		
		botonPrevContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	prevContact();
            }
        });		

		botonSkipAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	skipAll();
            }
        });		

		botonPrevContact.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
            	prevContact();
            	return false;
            }
        });		
		
		botonSkipContact.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
            	skipContact();
            	return false;
            }
        });		

		botonPrevContact.setEnabled(false);
		botonSkipContact.setEnabled(false);
	
		
    	ImageButton categorias = this.findViewById(R.id.imagenCategorias);
    	categorias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openListadoCategoria();
				
			}
		});

	}
	
    
	
	private void skipAll() {
		this.finish();
		
	}


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent); 
    	ejecutarOnActivityResult(requestCode);
    }
    
    private void ejecutarOnActivityResult(int requestCode){
    	try {
        	if(requestCode == ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS){
            	this.configurarSpinner();    
        	}
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, e.getMessage());
		}

    	
    }
	
	
	private void skipContact() {
		this.buscarSiguienteContacto();
	}

	private void prevContact() {
		this.buscarAnteriorContacto();
	}
	
	private void addAll(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(getResources().getString(R.string.mensaje_importar_todo_1) + mCategoriaSeleccionada.getNombreRelativo() + getResources().getString(R.string.mensaje_importar_todo_2))
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
	   	        	   Long[] params = new Long[1];
	   		    	   params[0] = 1L;
	   		    	   dialog.cancel();    	        	   
    	        	   new ImportContactTask().execute(params);    	  

    	           }
    	       })
    	       .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	builder.show();
		
	}
	
    private class ImportContactTask extends AsyncTask<Long, Integer, Integer>{
    	ProgressDialog dialog = null;
        @Override
        protected Integer doInBackground(Long... params) {

            try {
                publishProgress(1);
                addAllPrivado();

            } catch (Exception e) {
            	ConstantsAdmin.mensaje = me.getString(R.string.errorImportarContacto);
            }
            return 0;

        }

        protected void onProgressUpdate(Integer... progress) {
        	dialog = ProgressDialog.show(me, "", 
                    me.getString(R.string.mensaje_importando_contactos), false);
        }

        @Override
        protected void onPostExecute(Integer result) {
        	try{
        		dialog.cancel();
		    	finish();
        	}catch (Exception e) {
				e.getMessage();
			}
       		ConstantsAdmin.mostrarMensajeDialog(me, ConstantsAdmin.mensaje);
    		ConstantsAdmin.mensaje = null;
        }
    }

	
	
	private void addAllPrivado() {
	/*	this.obtenerContactoCapturado(true);
		ConstantsAdmin.mDBManager.createPersona(persona, false);*/
		String given;
		String family;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		try {
			people.moveToFirst();
			while(people.moveToNext()) {
			       contactId  = people.getString(people.getColumnIndex(ContactsContract.Contacts._ID));
		   	       contactoActual = this.obtenerNombreYApellidoDeContactoDeAgenda(contactId);
		   	       if(contactoActual != null){
				       given = (String)contactoActual.getKey();
			   	       family = (String)contactoActual.getValue();

			   	       if(family != null && !family.equals("") || given != null && !given.equals("")){
			        	   persona = ConstantsAdmin.obtenerPersonaConNombreYApellido(given,family,this, mDBManager);
			        	   if(persona == null){
			        		   persona = new PersonaDTO();   
			        	   }
					       persona.setApellido(family);
					       persona.setNombres(given);
				   		   if(persona.getNombres() != null && persona.getApellido() == null){
				   			   persona.setApellido(persona.getNombres());
				   			   persona.setNombres(null);
						   }
				   		   this.obtenerContactoCapturado(true);
			    	   	   mDBManager.createPersona(persona, false);

			   	       }
		   	       }
		    		
		    	}
				ConstantsAdmin.resetPersonasOrganizadas();
			
		} catch (Exception e) {
			e.getMessage();
		}
		
	}


	private void addContact() {
		long id;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		this.obtenerContactoCapturado(false);
		id = mDBManager.createPersona(persona, false);
		if(id != -1){
			ConstantsAdmin.resetPersonasOrganizadas();
			persona.setId(id);
			this.mostrarDatosPersonaEncontrada();
			mPersonaEncontrada.setText(mPersonaEncontrada.getText() + " #");
		}
	//	this.buscarSiguienteContacto();
		
		
	}

	

    private void openListadoCategoria(){
        Intent i = new Intent(this, ListadoCategoriaActivity.class);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS);
    }
    
    
	private void registrarWidgets(){
		mSpinner = this.findViewById(R.id.spinnerCategorias_alta_persona);
		mPersonaEncontrada = this.findViewById(R.id.textPersonaEncontrada);
		mTipoPersonaEncontrada = this.findViewById(R.id.textTipoPersonaEncontrada);
		entryDatoExtra = this.findViewById(R.id.entryDatoExtra);
		entryDescripcion = this.findViewById(R.id.entryDescripcion);
		botonAddAll = this.findViewById(R.id.botonAddAll);
		botonAddContact = this.findViewById(R.id.botonAddContact);
		botonSkipAll = this.findViewById(R.id.botonSkipAll);
		botonSkipContact = this.findViewById(R.id.botonSkipContact);
		botonPrevContact = this.findViewById(R.id.botonPrevContact);

	}
	
	
	private void seleccionarPrimerCategoria(){
		mCategoriaSeleccionada = this.mSpinnerAdapt.getItem(0);
		mSpinner.setSelection(0);
	}

    private void configurarSpinner(){
      List<CategoriaDTO> categorias;
      List<CategoriaDTO> categoriasPersonales;
      DataBaseManager mDBManager = DataBaseManager.getInstance(this);
      categorias = ConstantsAdmin.obtenerCategoriasActivas(this, null, mDBManager);
      categoriasPersonales = ConstantsAdmin.obtenerCategoriasActivasPersonales(this, mDBManager);
      categorias.addAll(categoriasPersonales);
      this.cargarNombreRelativoCategorias(categorias);
      this.crearSpinnerCategorias(categorias);
    }

  	private void cargarNombreRelativoCategorias(List<CategoriaDTO> categorias){
  		Iterator<CategoriaDTO> it = categorias.iterator();
  		CategoriaDTO catTemp;
  		String nombreRelativo;
  		while(it.hasNext()){
  			catTemp = it.next();
  			nombreRelativo = ConstantsAdmin.obtenerNombreCategoria(catTemp.getNombreReal(), this);
  			if(nombreRelativo == null){
  				nombreRelativo = catTemp.getNombreReal();
  			}
  			catTemp.setNombreRelativo(nombreRelativo);
  		}
  		Collections.sort(categorias);
  	}

  	
    
    private void crearSpinnerCategorias(List<CategoriaDTO> categorias){
	    Spinner spinner = findViewById(R.id.spinnerCategorias_alta_persona);
	    this.mSpinnerAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
	    this.seleccionarPrimerCategoria();
	    spinner.setAdapter(this.mSpinnerAdapt); 
	    OnItemSelectedListener spinnerListener = new seleccionSpinnerListener();
	    spinner.setOnItemSelectedListener(spinnerListener);
	}
    
	private void mostrarDatosPorCategoria(){
    	String text;
    	if(mCategoriaSeleccionada != null){
	    	EditText textViewEntry = this.findViewById(R.id.entryDatoExtra);
	    	TextView textViewLabel = this.findViewById(R.id.label_datoExtra);
	    	text = this.obtenerEtiquetaDatoExtra(mCategoriaSeleccionada.getNombreReal());
	    	if(text == null){
	    		text = mCategoriaSeleccionada.getTipoDatoExtra();
	    	}
	    	textViewEntry.setHint(text.toUpperCase());
	    	textViewLabel.setText(text.toUpperCase());
    	}
	}

    private String obtenerEtiquetaDatoExtra(String nombreEtiqueta){
    	String result = null;
		switch (nombreEtiqueta) {
			case ConstantsAdmin.CATEGORIA_AMIGOS:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_CLIENTES:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_COLEGAS:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_COMPANIEROS:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_CONOCIDOS:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_FAMILIARES:
				result = getString(R.string.hint_parentesco);
				break;
			case ConstantsAdmin.CATEGORIA_JEFES:
				result = getString(R.string.hint_empresa);
				break;
			case ConstantsAdmin.CATEGORIA_MEDICO:
				result = getString(R.string.hint_especialidad);
				break;
			case ConstantsAdmin.CATEGORIA_OTROS:
				result = getString(R.string.hint_rol);
				break;
			case ConstantsAdmin.CATEGORIA_PACIENTES:
				result = getString(R.string.hint_obraSocial);
				break;
			case ConstantsAdmin.CATEGORIA_PROFESORES:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_PROVEEDORES:
				result = getString(R.string.hint_empresa);
				break;
			case ConstantsAdmin.CATEGORIA_SOCIOS:
				result = getString(R.string.hint_empresa);
				break;
			case ConstantsAdmin.CATEGORIA_VECINOS:
				result = getString(R.string.hint_zona);
				break;
		}
    	
    	
    	return result;
    }
    
    
    
    class seleccionSpinnerListener implements OnItemSelectedListener {


        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	mCategoriaSeleccionada = (CategoriaDTO) parent.getSelectedItem();
        	mostrarDatosPorCategoria();
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    
    protected void onResume() {
        super.onResume();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        if(ConstantsAdmin.mainActivity == null){
        	Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        	startActivity(LaunchIntent);
        	this.finish();
        	ConstantsAdmin.cerrarMainActivity = true;

        }else{
        	ConstantsAdmin.inicializarBD(mDBManager);
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            if(people == null){
            	people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,
            		   null//if you don't want to google contacts also,
            		   ,null, sortOrder);
            }

            if(people != null){
    	    	startManagingCursor(people);
    	    	this.buscarSiguienteContacto();
        	}else{
        		ConstantsAdmin.mostrarMensajeDialog(this, getResources().getString(R.string.mensaje_no_hay_contactos));
        		this.finish();
        	}

        }

    }
    
    protected void onPause() {
    	super.onPause();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        this.resetAllMyCursors();
        people = null;
        ConstantsAdmin.finalizarBD(mDBManager);

    }

    
            
    private void resetAllMyCursors(){
    	Cursor cur;
		for (Cursor allMyCursor : allMyCursors) {
			cur = allMyCursor;
			cur.close();
			this.stopManagingCursor(cur);
		}
    	allMyCursors = new ArrayList<>();
    }

    
    private void obtenerContactoCapturado(boolean desdeAgregarTodos){
    	boolean tieneTelefonos;
    	String[] projectionPhone = new String[]{
    			Phone.NUMBER,
    			Phone.TYPE
    	};
    	                                      
    	String[] projectionMail = new String[]{
    			Email.DATA,
    			Email.TYPE
    	};   	

    	try{
	//	   String hasPhone = people.getString(people.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
    	//   String hasPhone = people.getString(people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));
    	   Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
    	   super.startManagingCursor(phones);
    	   tieneTelefonos = phones.getCount() > 0;
    	   phones.close();
    	   stopManagingCursor(phones);
		   if(mCategoriaSeleccionada == null){
			   this.seleccionarPrimerCategoria();
		   }
		   if(!desdeAgregarTodos || (persona.getId() == -1)){// SIGNIFICA QUE ES UN AGREGADO O ACTUALIZACION SIMPLE DE UN CONTACTO
		   // O SI ES DESDE ADD ALL SOLO CONFIGURO CATEGORIA SI EL CONTACTO ES NUEVO
			   persona.setCategoriaId(mCategoriaSeleccionada.getId());
		       persona.setCategoriaNombre(mCategoriaSeleccionada.getNombreReal());
		       persona.setCategoriaNombreRelativo(mCategoriaSeleccionada.getNombreRelativo());
			   persona.setDatoExtra(entryDatoExtra.getText().toString());
			   persona.setDescripcion(entryDescripcion.getText().toString());
		   }

	       if (tieneTelefonos){
	    	   this.importarTelDeContacto(projectionPhone, persona, contactId);
	       }
	       this.importarMailDeContacto(projectionMail, persona, contactId);
			
		} catch (Exception e) {
			e.getMessage();
		} 

    }
    
    
    private Asociacion obtenerNombreYApellidoDeContactoDeAgenda(String contactId){
    	Asociacion asoc = null;
    	Cursor nameCur;
    	String given;
    	String family;
        String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + "=" + contactId;
        String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
        nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, null);
        if(nameCur!=null){
        	super.startManagingCursor(nameCur);
	        if (nameCur.moveToNext()) {
	            given = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
	            family = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
	            asoc = new Asociacion(given, family);
	        }
	        nameCur.close();
            stopManagingCursor(nameCur);

        }

    	return asoc;
    }
    
    private void importarMailDeContacto(String[] projectionMail, PersonaDTO per, String contactId){
    	String emailAddress;
    	int emailType;
    	try {
            Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projectionMail, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null); 
            if(emails != null){
    	        super.startManagingCursor(emails);
    	        while (emails.moveToNext()) { 
    	           // Tis would allow you get several email addresses 
    	           emailAddress = emails.getString(emails.getColumnIndex(Email.DATA)); 
    	           emailType = emails.getInt(emails.getColumnIndex(Email.TYPE)); 
    	
    	           if(!emailAddress.equals("")){
    	        		switch (emailType) {
    					case Email.TYPE_HOME :
    						per.setEmailParticular(emailAddress);
    						break;
    					case Email.TYPE_WORK:
    						per.setEmailLaboral(emailAddress);
    						break;
    					case Email.TYPE_OTHER :
    						per.setEmailOtro(emailAddress);
    						break;
    					default:
    						break;
    					}
    	           }
    	        } 
    	        emails.close();
    	        this.stopManagingCursor(emails);
            }
			
		} catch (Exception e) {
			e.getMessage();
		}

    	
    }
    
    
    
    
    private void importarTelDeContacto(String[] projectionPhone, PersonaDTO per, String contactId){
    	String phoneNumber;
    	int phoneType;
    	try {
    		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
            if(phones != null){
    	        super.startManagingCursor(phones);
    	        while (phones.moveToNext()) 
    	        {
    	        	phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    	        	phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
    	        	
    	        	if(!phoneNumber.equals("")){
    	        		switch (phoneType) {
    					case Phone.TYPE_HOME :
    						per.setTelParticular(phoneNumber);
    						break;
    					case Phone.TYPE_MOBILE :
    						per.setCelular(phoneNumber);
    						break;
    					case Phone.TYPE_WORK :
    						per.setTelLaboral(phoneNumber);
    						break;
    					case Phone.TYPE_WORK_MOBILE :
    						per.setTelLaboral(phoneNumber);
    						break;
    					default:
    						break;
    					}
    	        		
    	        	}
    	        }
    	        phones.close();
    	        this.stopManagingCursor(phones);
            }
		} catch (Exception e) {
			e.getMessage();
		}
        
    	
    }
    

    
    
}
