package com.boxico.android.kn.contacts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.MenuItem;
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
import com.boxico.android.kn.contacts.persistencia.dtos.TipoValorDTO;
import com.boxico.android.kn.contacts.util.Asociacion;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;

public class ImportarContactoActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnItemSelectedListener {

	private ArrayAdapter<CategoriaDTO> mSpinnerAdapt = null;
	private CategoriaDTO mCategoriaSeleccionada = null;
	private Cursor people = null;
	private ImportarContactoActivity me = null;
	// --Commented out by Inspection (12/11/2018 12:34):boolean haciaAdelante = true;
//	private ArrayList<Cursor> allMyCursors = null;

	private TextView mPersonaEncontrada;
	private TextView mExtrasPersonaEncontrada;
//	private TextView mMailsPersonaEncontrada;
//	private TextView mDirsPersonaEncontrada;
	private TextView entryDatoExtra;
	private TextView entryDescripcion;

	private Spinner mSpinner = null;

	private String given = "";
	private String family = "";
	private int posPeople = 0;

	private Button botonAddContact = null;
	private ImageButton botonSkipContact = null;
	private ImageButton botonPrevContact = null;
	private Button botonAddAll = null;
//	private Button botonSkipAll = null;
	private Asociacion contactoActual = null;
	private PersonaDTO persona = null;
	private String contactId = null;

	private Bitmap photo = null;

	private final int PERSONA_ID_CURSOR = 1;
	private final int PERSONA_EXTRA_ID_CURSOR = 2;
	private final int PERSONA_EMAIL_CURSOR = 3;
	private final int PERSONA_PHONE_CURSOR = 4;
	private final int PERSONA_NOMBRE_APELLIDO_CURSOR = 5;
	private final int PERSONA_DIR_CURSOR = 6;

	private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
	private ArrayList<TipoValorDTO> nuevosTelefonos;
	private ArrayList<TipoValorDTO> nuevosMails;
	private ArrayList<TipoValorDTO> nuevasDirecciones;

	/*
    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }
*/
	private void cargarLoaders() {
		this.getSupportLoaderManager().initLoader(PERSONA_ID_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(PERSONA_EXTRA_ID_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(PERSONA_EMAIL_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(PERSONA_PHONE_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(PERSONA_DIR_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(PERSONA_NOMBRE_APELLIDO_CURSOR, null, this);
	}


	private void askForContactPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE},
						PERMISSIONS_REQUEST_READ_CONTACTS);

			} else {
				this.inicializarContactosAImportar();
			}
		} else {
			this.inicializarContactosAImportar();
		}
	}

	private void inicializarContactosAImportar() {
		this.cargarLoaders();

		//	DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if (ConstantsAdmin.mainActivity == null) {
			Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
			startActivity(LaunchIntent);
			this.finish();
			ConstantsAdmin.cerrarMainActivity = true;

		} else {

			//String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
			//   String sortOrder = ConstantsAdmin.sortOrderForContacts;
			if (getPeople() != null) {
				//ConstantsAdmin.inicializarBD(mDBManager);
				//	startManagingCursor(people);
				this.buscarSiguienteContacto();
				//ConstantsAdmin.finalizarBD(mDBManager);
			} else {
				ConstantsAdmin.mostrarMensajeDialog(this, getResources().getString(R.string.mensaje_no_hay_contactos));
				this.finish();
			}

		}


	}

	private Cursor getPeople() {
		if (people == null) {
			String sortOrder = ConstantsAdmin.sortOrderForContacts;
			people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
					null//if you don't want to google contacts also,
					, null, sortOrder);
		}
		return people;
	}

	/*

	private Cursor getPeopleById(String idContact){
		Cursor cur = null;
		String selection = ConstantsAdmin.querySelectionContactsById + idContact;
	//	String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + "=" + idContact;
		String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		cur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, selection, whereNameParams, null);

		return cur;
	}
*/
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {
		if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				this.inicializarContactosAImportar();
			} else {
				this.finish();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.menu_importar_contactos));
		try {

			// 	allMyCursors = new ArrayList<>();
			this.setContentView(R.layout.import_contact);
			this.registrarWidgets();
			me = this;
			this.configurarSpinner();
			this.configurarBotones();
			this.askForContactPermission();
			getActionBar().setDisplayHomeAsUpEnabled(true);
			this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.menu_importar_contactos).toUpperCase());
		} catch (Exception e) {
			// TODO: handle exception
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorInicioAplicacion));
		}

	}

	private void mostrarFoto(String cId){
		InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(this.getContentResolver(),
				ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(cId)));
		TextView tv = this.findViewById(R.id.textPersonaEncontrada);
		BitmapDrawable iconBig = null;
		if (inputStream != null) {
			photo = BitmapFactory.decodeStream(inputStream);
			iconBig = new BitmapDrawable(getResources(), photo);
			tv.setCompoundDrawablesRelativeWithIntrinsicBounds(iconBig, null, null, null);
		}else{
			tv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
			photo= null;
		}
	}

	private void buscarSiguienteContacto() {
		boolean encontrado = false;
		String contactIdTemp;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);

		while (!encontrado && getPeople().moveToNext()) {
			posPeople = getPeople().getPosition();
			contactIdTemp = getPeople().getString(getPeople().getColumnIndex(ContactsContract.Contacts._ID));
			contactoActual = this.obtenerNombreYApellidoDeContactoDeAgenda(contactIdTemp);
			this.mostrarFoto(contactIdTemp);
			if (contactoActual != null) {
				if (contactId == null || !contactId.equals(contactIdTemp)) {
					//	if(!given.equals(contactoActual.getKey()) && !family.equals(contactoActual.getValue())){
					contactId = contactIdTemp;
					given = (String) contactoActual.getKey();
					family = (String) contactoActual.getValue();
					if (given == null) {
						given = "";
					}
					if (family == null) {
						family = "";
					}

					if (!family.equals("") || !given.equals("")) {
						persona = ConstantsAdmin.obtenerPersonaConNombreYApellido(given, family);
						if (persona == null) {
							persona = new PersonaDTO();
						}
						persona.setApellido(family);
						persona.setNombres(given);
					/*	if (persona.getNombres() != null && (persona.getApellido() == null || persona.getApellido().equals(""))) {
							persona.setApellido(persona.getNombres());
							persona.setNombres(null);
						}*/
						encontrado = true;
						this.mostrarDatosPersonaEncontrada();
					}

				}

			}


		}

		ConstantsAdmin.finalizarBD(mDBManager);
	/*	if (posPeople < getPeople().getCount() - 1) {
			botonSkipContact.setEnabled(true);
			botonSkipContact.setTextColor(getResources().getColor(R.color.color_blanco));
		} else {
			botonSkipContact.setEnabled(false);
			botonSkipContact.setTextColor(getResources().getColor(R.color.color_gris));
		}*/
	/*	if (posPeople > 0) {
			botonPrevContact.setEnabled(true);
			botonPrevContact.setTextColor(getResources().getColor(R.color.color_blanco));
		}*/
	}


	private void buscarAnteriorContacto() {
		given = null;
		family = null;
		boolean encontrado = false;
		String contactIdTemp;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);

		while (!encontrado && getPeople().moveToPrevious()) {
			posPeople = getPeople().getPosition();
			contactIdTemp = getPeople().getString(getPeople().getColumnIndex(ContactsContract.Contacts._ID));
			contactoActual = this.obtenerNombreYApellidoDeContactoDeAgenda(contactIdTemp);
			this.mostrarFoto(contactIdTemp);
			if (contactoActual != null) {
				if (contactId == null || !contactId.equals(contactIdTemp)) {
					contactId = contactIdTemp;
					//	       if(!given.equals(contactoActual.getKey()) && !family.equals(contactoActual.getValue())){
					given = (String) contactoActual.getKey();
					family = (String) contactoActual.getValue();
					if (given == null) {
						given = "";
					}
					if (family == null) {
						family = "";
					}
					if (!family.equals("") || !given.equals("")) {

						persona = ConstantsAdmin.obtenerPersonaConNombreYApellido(given, family);
						if (persona == null) {
							persona = new PersonaDTO();
						}
						persona.setApellido(family);
						persona.setNombres(given);
					/*	if (persona.getNombres() != null && (persona.getApellido() == null || persona.getApellido().equals(""))) {
							persona.setApellido(persona.getNombres());
							persona.setNombres(null);
						}*/
						encontrado = true;

						this.mostrarDatosPersonaEncontrada();

					}
				}
			}
		}

		ConstantsAdmin.finalizarBD(mDBManager);
	/*	if (posPeople == 0) {
			botonPrevContact.setEnabled(false);
			botonPrevContact.setTextColor(getResources().getColor(R.color.color_gris));
		} else {
			botonPrevContact.setEnabled(true);
			botonPrevContact.setTextColor(getResources().getColor(R.color.color_blanco));
		}*/
		/*if (posPeople < getPeople().getCount()) {
			botonSkipContact.setEnabled(true);
			botonSkipContact.setTextColor(getResources().getColor(R.color.color_blanco));
		}*/

	}


	private void mostrarDatosPersonaEncontrada() {
		String text = "";
		if (persona != null) {
			mPersonaEncontrada.setText("");
			if(persona.getApellido() != null && !persona.getApellido().equals("")){
				if (persona.getNombres() != null && !persona.getNombres().equals("")) {
					//mPersonaEncontrada.setText(persona.getApellido() + ", " + persona.getNombres());
					text = persona.getApellido() + ", " + persona.getNombres();
				} else {
					text = persona.getApellido();
					//mPersonaEncontrada.setText(persona.getApellido());
				}
			}else{
				text = persona.getNombres();
			}

			if (persona.getId() == -1) {
				this.seleccionarPrimerCategoria();
				text = text + System.getProperty("line.separator") + "(" + getResources().getString(R.string.label_nuevo_contacto) + ")";
				botonAddContact.setText(getResources().getString(R.string.label_agregar));
				entryDatoExtra.setText("");
				entryDescripcion.setText("");

			} else {
				this.seleccionarCategoriaContactoExistente();
				text = text + System.getProperty("line.separator") + getResources().getString(R.string.label_contacto_existente) + persona.getCategoriaNombreRelativo() + ")";
				botonAddContact.setText(getResources().getString(R.string.label_actualizar));
				entryDatoExtra.setText(persona.getDatoExtra());
				entryDescripcion.setText(persona.getDescripcion());

			}
			mPersonaEncontrada.setText(text);

			String tels = this.obtenerTelDeContacto(contactId);
			String mails = this.obtenerMailDeContacto(contactId);
			String dirs = this.obtenerDirsDeContacto(contactId);
			String total = "";
			if(tels != null){
				total = total + tels + System.getProperty("line.separator");
			}

			if(mails != null){
				total = total + System.getProperty("line.separator") + mails + System.getProperty("line.separator");
			}

			if(dirs != null){
				total = total + System.getProperty("line.separator") + dirs + System.getProperty("line.separator");
			}

			if(!total.equals("")){
				mExtrasPersonaEncontrada.setVisibility(View.VISIBLE);
				mExtrasPersonaEncontrada.setText(total);
			}else {
				mExtrasPersonaEncontrada.setVisibility(View.GONE);
			}
			mExtrasPersonaEncontrada.invalidate();
			((View) mExtrasPersonaEncontrada.getParent()).invalidate();


			//mPersonaEncontrada.setText(mPersonaEncontrada.getText());
			//mTelsPersonaEncontrada.setText(text);
		}

	}

	private void seleccionarCategoriaContactoExistente() {
		boolean encontrado = false;
		int pos = 0;
		CategoriaDTO cat;
		int total = mSpinnerAdapt.getCount();
		while (!encontrado && pos < total) {

			cat = mSpinnerAdapt.getItem(pos);
			if (cat.getNombreReal().equals(persona.getCategoriaNombre())) {
				encontrado = true;
				mCategoriaSeleccionada = cat;
			}
			pos++;
		}
		if (encontrado) {
			mSpinner.setSelection(pos - 1);
		}


	}


	private void configurarBotones() {
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

		/*botonSkipAll.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				skipAll();
			}
		});
*/
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

	//	botonPrevContact.setEnabled(false);
	//	botonSkipContact.setEnabled(false);


		ImageButton categorias = this.findViewById(R.id.imagenCategorias);
		categorias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openListadoCategoria();

			}
		});

	}

/*
	private void skipAll() {
		this.finish();

	}
*/

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		ejecutarOnActivityResult(requestCode);
	}

	private void ejecutarOnActivityResult(int requestCode) {
		try {
			if (requestCode == ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS) {
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

	private void addAll() {
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

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		CursorLoader cl = null;
		switch (id) {
			case PERSONA_ID_CURSOR:
				cl = mDBManager.cursorLoaderPersonaId("0", this, getContentResolver());
				ConstantsAdmin.cursorPersona = cl;
				break; // optional
			case PERSONA_EXTRA_ID_CURSOR:
				cl = mDBManager.cursorLoaderPersonaExtraId("0", this, getContentResolver());
				ConstantsAdmin.cursorPersonaExtra = cl;
				break; // optional
			case PERSONA_EMAIL_CURSOR:
				cl = mDBManager.cursorLoaderEmailPersona("0", this, getContentResolver());
				ConstantsAdmin.cursorEmailPersona = cl;
				break; // optional
			case PERSONA_DIR_CURSOR:
				cl = mDBManager.cursorLoaderDirPersona("0", this, getContentResolver());
				ConstantsAdmin.cursorDirsPersona = cl;
				break;
			case PERSONA_PHONE_CURSOR:
				cl = mDBManager.cursorLoaderPhonePersona("0", this, getContentResolver());
				ConstantsAdmin.cursorPhonePersona = cl;
				break; // optional
			case PERSONA_NOMBRE_APELLIDO_CURSOR:
				cl = mDBManager.cursorLoaderNombreApellidoPersona(null, null, this);
				ConstantsAdmin.cursorPersonaByNombreYApellido = cl;
				break; // optional
			default: // Optional
		}
		return cl;
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {

	}

	private class ImportContactTask extends AsyncTask<Long, Integer, Integer> {
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
			dialog.cancel();
			finish();
		/*	ConstantsAdmin.mostrarMensajeDialog(me, ConstantsAdmin.mensaje);
			ConstantsAdmin.mensaje = null;*/
		}
	}


	private void addAllPrivado() {
	/*	this.obtenerContactoCapturado(true);
		ConstantsAdmin.mDBManager.createPersona(persona, false);*/
		String given;
		String family;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);
		getPeople().moveToFirst();
		while (getPeople().moveToNext()) {
			contactId = getPeople().getString(getPeople().getColumnIndex(ContactsContract.Contacts._ID));
			contactoActual = this.obtenerNombreYApellidoDeContactoDeAgenda(contactId);
			if (contactoActual != null) {
				given = (String) contactoActual.getKey();
				family = (String) contactoActual.getValue();

				if (family != null && !family.equals("") || given != null && !given.equals("")) {
					persona = ConstantsAdmin.obtenerPersonaConNombreYApellido(given, family);
					if (persona == null) {
						persona = new PersonaDTO();
					}
					persona.setApellido(family);
					persona.setNombres(given);
				/*	if (persona.getNombres() != null && persona.getApellido() == null) {
						persona.setApellido(persona.getNombres());
						persona.setNombres(null);
					}*/

					this.obtenerContactoCapturado(true);
					long idPersona = mDBManager.createPersona(persona, false);
                    this.guardarNuevosTelefonos(mDBManager, idPersona);
                    this.guardarNuevosMails(mDBManager, idPersona);
                    this.guardarNuevasDirecciones(mDBManager, idPersona);
					// Cargo foto

					InputStream inputStream = null;
					Bitmap photoTemp = null;
					inputStream = ContactsContract.Contacts.openContactPhotoInputStream(this.getContentResolver(),
							ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contactId)));
					if (inputStream != null) {
						photoTemp = BitmapFactory.decodeStream(inputStream);
						try {
							ConstantsAdmin.almacenarImagen(this, ConstantsAdmin.folderCSV + File.separator + ConstantsAdmin.imageFolder, "." + String.valueOf(idPersona) + ".jpg", photoTemp);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}


				}
			}

		}
		ConstantsAdmin.resetPersonasOrganizadas();
		ConstantsAdmin.finalizarBD(mDBManager);


	}


	private void addContact() {
		long id;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);
		this.obtenerContactoCapturado(false);
		id = mDBManager.createPersona(persona, false);
		if (id != -1) {
            try {
            	if(photo != null) {
					ConstantsAdmin.almacenarImagen(this, ConstantsAdmin.folderCSV + File.separator + ConstantsAdmin.imageFolder, "." + String.valueOf(id) + ".jpg", photo);
				}
            } catch (IOException e) {
                e.printStackTrace();
            }
            ConstantsAdmin.resetPersonasOrganizadas();
			persona.setId(id);
			this.guardarNuevosTelefonos(mDBManager, id);
			this.guardarNuevosMails(mDBManager, id);
			this.guardarNuevasDirecciones(mDBManager, id);
			this.mostrarDatosPersonaEncontrada();
			mPersonaEncontrada.setText(mPersonaEncontrada.getText() + " #");
		}
		ConstantsAdmin.finalizarBD(mDBManager);
		//	this.buscarSiguienteContacto();


	}

	private void guardarNuevosTelefonos(DataBaseManager mDBManager, long id){
		//this.filtrarTelefonosExistentes(mDBManager, id);
		ConstantsAdmin.crearTelefonos(nuevosTelefonos, id, mDBManager);
	}

	private void guardarNuevosMails(DataBaseManager mDBManager, long id){
		//this.filtrarTelefonosExistentes(mDBManager, id);
		ConstantsAdmin.crearEmails(nuevosMails, id, mDBManager);
	}

	private void guardarNuevasDirecciones(DataBaseManager mDBManager, long id){
		//this.filtrarTelefonosExistentes(mDBManager, id);
		ConstantsAdmin.crearDirecciones(nuevasDirecciones, id, mDBManager);
	}


	private void openListadoCategoria() {
		Intent i = new Intent(this, ListadoCategoriaActivity.class);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS);
	}


	private void registrarWidgets() {
		mSpinner = this.findViewById(R.id.spinnerCategorias_alta_persona);
		mPersonaEncontrada = this.findViewById(R.id.textPersonaEncontrada);
		mExtrasPersonaEncontrada = this.findViewById(R.id.textExtrasPersonaEncontrada);
	//	mMailsPersonaEncontrada = this.findViewById(R.id.textMailsPersonaEncontrada);
	//	mDirsPersonaEncontrada = this.findViewById(R.id.textDirsPersonaEncontrada);
		entryDatoExtra = this.findViewById(R.id.entryDatoExtra);
		entryDescripcion = this.findViewById(R.id.entryDescripcion);
		botonAddAll = this.findViewById(R.id.botonAddAll);
		botonAddContact = this.findViewById(R.id.botonAddContact);
	//	botonSkipAll = this.findViewById(R.id.botonSkipAll);
		botonSkipContact = this.findViewById(R.id.botonSkipContact);
		botonPrevContact = this.findViewById(R.id.botonPrevContact);

	}


	private void seleccionarPrimerCategoria() {
		mCategoriaSeleccionada = this.mSpinnerAdapt.getItem(0);
		mSpinner.setSelection(0);
	}

	private void configurarSpinner() {
		List<CategoriaDTO> categorias;
		List<CategoriaDTO> categoriasPersonales;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		categorias = ConstantsAdmin.obtenerCategoriasActivas(this, null, mDBManager);
		categoriasPersonales = ConstantsAdmin.obtenerCategoriasActivasPersonales(this, mDBManager);
		categorias.addAll(categoriasPersonales);
		this.cargarNombreRelativoCategorias(categorias);
		this.crearSpinnerCategorias(categorias);
	}

	private void cargarNombreRelativoCategorias(List<CategoriaDTO> categorias) {
		Iterator<CategoriaDTO> it = categorias.iterator();
		CategoriaDTO catTemp;
		String nombreRelativo;
		while (it.hasNext()) {
			catTemp = it.next();
			nombreRelativo = ConstantsAdmin.obtenerNombreCategoria(catTemp.getNombreReal(), this);
			if (nombreRelativo == null) {
				nombreRelativo = catTemp.getNombreReal();
			}
			catTemp.setNombreRelativo(nombreRelativo);
		}
		Collections.sort(categorias);
	}


	private void crearSpinnerCategorias(List<CategoriaDTO> categorias) {
		Spinner spinner = findViewById(R.id.spinnerCategorias_alta_persona);
		this.mSpinnerAdapt = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, categorias);
		this.seleccionarPrimerCategoria();
		spinner.setAdapter(this.mSpinnerAdapt);
		//	OnItemSelectedListener spinnerListener = new seleccionSpinnerListener();
		spinner.setOnItemSelectedListener(this);
	}

	private void mostrarDatosPorCategoria() {
		String text;
		if (mCategoriaSeleccionada != null) {
			EditText textViewEntry = this.findViewById(R.id.entryDatoExtra);
			TextView textViewLabel = this.findViewById(R.id.label_datoExtra);
			text = this.obtenerEtiquetaDatoExtra(mCategoriaSeleccionada.getNombreReal());
			if (text == null) {
				text = mCategoriaSeleccionada.getTipoDatoExtra();
			}
			textViewEntry.setHint(text.toUpperCase());
			textViewLabel.setText(text.toUpperCase());
		}
	}

	private String obtenerEtiquetaDatoExtra(String nombreEtiqueta) {
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
			/*case ConstantsAdmin.CATEGORIA_COMPANIEROS:
				result = getString(R.string.hint_lugarOActividad);
				break;*/
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

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
		mCategoriaSeleccionada = (CategoriaDTO) parent.getSelectedItem();
		mostrarDatosPorCategoria();
		//mSpinnerAdapt.notifyDataSetChanged();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
/*

	class seleccionSpinnerListener implements OnItemSelectedListener {


		public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
			mCategoriaSeleccionada = (CategoriaDTO) parent.getSelectedItem();
			mostrarDatosPorCategoria();
			//mSpinnerAdapt.notifyDataSetChanged();
		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	}
*/

	protected void onPause() {
		super.onPause();
		/*DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        this.resetAllMyCursors();*/
		people = null;
		/* ConstantsAdmin.finalizarBD(mDBManager);*/

	}

    
  /*
    private void resetAllMyCursors(){
    	Cursor cur;
		for (Cursor allMyCursor : allMyCursors) {
			cur = allMyCursor;
			cur.close();
			this.stopManagingCursor(cur);
		}
    	allMyCursors = new ArrayList<>();
    }


*/


	private void obtenerContactoCapturado(boolean desdeAgregarTodos) {

		if (mCategoriaSeleccionada == null) {
			this.seleccionarPrimerCategoria();
		}
		if (!desdeAgregarTodos || (persona.getId() == -1)) {// SIGNIFICA QUE ES UN AGREGADO O ACTUALIZACION SIMPLE DE UN CONTACTO
			// O SI ES DESDE ADD ALL SOLO CONFIGURO CATEGORIA SI EL CONTACTO ES NUEVO
			persona.setCategoriaId(mCategoriaSeleccionada.getId());
			persona.setCategoriaNombre(mCategoriaSeleccionada.getNombreReal());
			persona.setCategoriaNombreRelativo(mCategoriaSeleccionada.getNombreRelativo());
			persona.setDatoExtra(entryDatoExtra.getText().toString());
			persona.setDescripcion(entryDescripcion.getText().toString());
		}
		persona.setIdPersonaAgenda(contactId);

//        if (tieneTelefonos) {
		nuevosTelefonos = ConstantsAdmin.importarTelDeContacto(contactId, this.getResources());
		nuevosMails = ConstantsAdmin.importarMailDeContacto(contactId, this.getResources());
		nuevasDirecciones = ConstantsAdmin.importarDirDeContacto(contactId, this.getResources());


	}


	private Asociacion obtenerNombreYApellidoDeContactoDeAgenda(String cId) {
		Asociacion asoc = null;
		Cursor nameCur;
		CursorLoader nameCurLoader;

		String given;
		String family;

		nameCurLoader = ConstantsAdmin.cursorPersonaExtra;
		nameCurLoader.setSelection(ConstantsAdmin.querySelectionContactsById + cId);
		nameCurLoader.reset();
		nameCur = nameCurLoader.loadInBackground();

		//	nameCur = this.getPeopleById(contactId);

		if (nameCur != null) {
			//	super.startManagingCursor(nameCur);
			if (nameCur.moveToNext()) {
				given = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
				family = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
				asoc = new Asociacion(given, family);
			}
			nameCur.close();
			//  stopManagingCursor(nameCur);

		}

		return asoc;
	}

	/*

	private ArrayList<TipoValorDTO> importarMailDeContacto(PersonaDTO per, String contactId) {


		String email;
		int mailType;
		String label = null;
		CursorLoader nameCurLoader;
		ArrayList<TipoValorDTO> nuevosMails = new ArrayList<>();

		//	String[] projectionPhone = ConstantsAdmin.projectionPhone;
		//DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		nameCurLoader = ConstantsAdmin.cursorEmailPersona;
		nameCurLoader.setSelection(ConstantsAdmin.querySelectionEmailContactsById + contactId);
		nameCurLoader.reset();
		Cursor mails = nameCurLoader.loadInBackground();
	//    		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
		if(mails != null){
//  	        super.startManagingCursor(phones);
			while (mails.moveToNext())
			{
				email = mails.getString(mails.getColumnIndex(Email.DATA));
				mailType = mails.getInt(mails.getColumnIndex(Email.TYPE));
				int lblIndex = mails.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL);
				if(mailType == Email.TYPE_CUSTOM){
					label = mails.getString(lblIndex);
				}

				if(!email.equals("")){
					switch (mailType) {
						case Email.TYPE_HOME :
							if(per.getEmailParticular() == null || per.getEmailParticular().equals("")){
								per.setEmailParticular(email);
							}else{
								if(!per.getEmailParticular().equalsIgnoreCase(email)){
									ConstantsAdmin.crearNuevoEmail(email, mailType, label, mails, nuevosMails, this.getResources());
								}
							}

							break;
						case Email.TYPE_OTHER :
							if(per.getEmailOtro() == null || per.getEmailOtro().equals("")){
								per.setEmailOtro(email);
							}else{
								if(!per.getEmailOtro().equalsIgnoreCase(email)) {
									ConstantsAdmin.crearNuevoEmail(email, mailType, label, mails, nuevosMails, this.getResources());
								}
							}
							break;
						case Email.TYPE_WORK :
							if(per.getEmailLaboral() == null || per.getEmailLaboral().equals("")){
								per.setEmailLaboral(email);
							}else{
								if(!per.getEmailLaboral().equalsIgnoreCase(email)) {
									ConstantsAdmin.crearNuevoEmail(email, mailType, label, mails, nuevosMails, this.getResources());
								}
							}
							break;

						default:
							ConstantsAdmin.crearNuevoEmail(email, mailType, label, mails, nuevosMails, this.getResources());
							//	per.set(phoneNumber);
							break;
					}

				}
			}
			mails.close();
//	        this.stopManagingCursor(phones);
		}
		return nuevosMails;
	}

	private ArrayList<TipoValorDTO> importarDirDeContacto(PersonaDTO per, String contactId) {

		String dir;
		String label = null;
		int dirType;
		CursorLoader nameCurLoader;
		ArrayList<TipoValorDTO> nuevasDirs = new ArrayList<>();

		//	String[] projectionPhone = ConstantsAdmin.projectionPhone;
		//DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		nameCurLoader = ConstantsAdmin.cursorDirsPersona;
		nameCurLoader.setSelection(ConstantsAdmin.querySelectionDirsContactsById + contactId);
		nameCurLoader.reset();
		Cursor dirs = nameCurLoader.loadInBackground();
		//    		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
		if(dirs != null){
//  	        super.startManagingCursor(phones);
			while (dirs.moveToNext())
			{
				dir = dirs.getString(dirs.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
				dirType = dirs.getInt(dirs.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
				int lblIndex = dirs.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL);
				if(dirType == Email.TYPE_CUSTOM){
					label = dirs.getString(lblIndex);
				}
				if(!dir.equals("")){
					switch (dirType) {
						case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME :
							if(per.getDireccionParticular() == null || per.getDireccionParticular().equals("")){
								per.setDireccionParticular(dir);
							}else{
								if(!per.getDireccionParticular().equalsIgnoreCase(dir)){
									ConstantsAdmin.crearNuevaDireccion(dir, dirType, label, dirs, nuevasDirs, this.getResources());
								}
							}

							break;
						case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK :
							if(per.getDireccionLaboral() == null || per.getDireccionLaboral().equals("")){
								per.setDireccionLaboral(dir);
							}else{
								if(!per.getDireccionLaboral().equalsIgnoreCase(dir)) {
									ConstantsAdmin.crearNuevaDireccion(dir, dirType, label, dirs, nuevasDirs, this.getResources());
								}
							}
							break;

						default:
							ConstantsAdmin.crearNuevaDireccion(dir, dirType, label, dirs, nuevasDirs, this.getResources());
							//	per.set(phoneNumber);
							break;
					}

				}
			}
			dirs.close();
//	        this.stopManagingCursor(phones);
		}
		return nuevasDirs;

*/

/*

		String address;
		int addressType;
		Cursor dirsCursor;
		CursorLoader nameCurLoader;

		//	DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		nameCurLoader = ConstantsAdmin.cursorDirsPersona;
		nameCurLoader.setSelection(ConstantsAdmin.querySelectionDirsContactsById + contactId);
		nameCurLoader.reset();
		dirsCursor = nameCurLoader.loadInBackground();
		//	Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projectionMail, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
		if (dirsCursor != null) {
			//    super.startManagingCursor(emails);
			while (dirsCursor.moveToNext()) {
				// Tis would allow you get several email addresses
				address = dirsCursor.getString(dirsCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
				addressType = dirsCursor.getInt(dirsCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

				if (!address.equals("")) {
					switch (addressType) {
						case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
							per.setDireccionParticular(address);
							break;
						case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
							per.setDireccionLaboral(address);
							break;
						default:
							break;
					}
				}
			}
			dirsCursor.close();
			//      this.stopManagingCursor(emails);
		}
		return null;
		}
*/




	private String obtenerMailDeContacto(String contactId) {
		String emailAddress;
		int emailType;
		Cursor emails;
		CursorLoader nameCurLoader;
		String mails = null;
		String label = null;

				//	DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		nameCurLoader = ConstantsAdmin.cursorEmailPersona;
		nameCurLoader.setSelection(ConstantsAdmin.querySelectionEmailContactsById + contactId);
		nameCurLoader.reset();
		emails = nameCurLoader.loadInBackground();


		//	Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projectionMail, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
		if (emails != null && emails.getCount() > 0) {
			//    super.startManagingCursor(emails);
			mails = "░" + this.getString(R.string.label_emails) + System.getProperty("line.separator");
			while (emails.moveToNext()) {
				// Tis would allow you get several email addresses
				emailAddress = emails.getString(emails.getColumnIndex(Email.DATA));
				emailType = emails.getInt(emails.getColumnIndex(Email.TYPE));

				int lblIndex = emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL);
				if(emailType == Email.TYPE_CUSTOM){
					label = emails.getString(lblIndex);
					if(label == null){
						label = this.getResources().getString(R.string.label_mail);
					}
				}else {
					label = this.getResources().getString(ContactsContract.CommonDataKinds.Email.getTypeLabelResource(emailType));
				}
				/*
				String label = this.getResources().getString(ContactsContract.CommonDataKinds.Email.getTypeLabelResource(emailType));
				if (label.equalsIgnoreCase("Custom")){
					label = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
				}*/
				if (!emailAddress.equals("")) {
					mails = mails + System.getProperty("line.separator") + "¤ " + label.toUpperCase() + "» " + emailAddress;
				}
				/*
				if (!emailAddress.equals("")) {
					switch (emailType) {
						case Email.TYPE_HOME:
							mails = mails + "\n" + Email.TYPE_HOME + ": " + emailAddress;
							break;
						case Email.TYPE_WORK:
							mails = mails + "\n" + Email.TYPE_WORK + ": " + emailAddress;
							break;
						case Email.TYPE_OTHER:
							mails = mails + "\n" + Email.TYPE_OTHER + ": " + emailAddress;
							break;
						default:
							break;
					}
				}*/
			}
			emails.close();
			//      this.stopManagingCursor(emails);
		}
		return mails;


	}



	private String obtenerTelDeContacto(String contactId) {
		String phoneNumber;
		int phoneType;
		CursorLoader nameCurLoader;

		String tels = null;
		String label = null;

				//	String[] projectionPhone = ConstantsAdmin.projectionPhone;
		//DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		nameCurLoader = ConstantsAdmin.cursorPhonePersona;
		nameCurLoader.setSelection(ConstantsAdmin.querySelectionPhoneContactsById + contactId);
		nameCurLoader.reset();
		Cursor phones = nameCurLoader.loadInBackground();

//    		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
		if (phones != null && phones.getCount() > 0) {
//  	        super.startManagingCursor(phones);
			tels = "░" + this.getString(R.string.label_telefonos) + System.getProperty("line.separator");
            while (phones.moveToNext()) {
				phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				int lblIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);
				if(phoneType == Phone.TYPE_CUSTOM){
					label = phones.getString(lblIndex);
				}else {
					label = this.getResources().getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(phoneType));
				}
			/*	if (label.equalsIgnoreCase("Custom")){
					label = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
				}*/

				if (!phoneNumber.equals("")) {
					tels = tels + System.getProperty("line.separator") + "¤ " + label.toUpperCase() + "» " + phoneNumber;
					/*switch (phoneType) {
						case Phone.TYPE_HOME:
							tels = tels + "\n" + Phone.TYPE_HOME + ": " + phoneNumber;
							break;
						case Phone.TYPE_MOBILE:
							tels = tels + "\n" + Phone.TYPE_MOBILE + ": " + phoneNumber;
							break;
						case Phone.TYPE_WORK:
							tels = tels + "\n" + Phone.TYPE_WORK + ": " + phoneNumber;
							break;
						case Phone.TYPE_WORK_MOBILE:
							tels = tels + "\n" + Phone.TYPE_WORK_MOBILE + ": " + phoneNumber;
							break;
						default:
							break;
					}*/

				}
			}
			phones.close();
//	        this.stopManagingCursor(phones);
		}
		return tels;

	}


	private String obtenerDirsDeContacto(String contactId) {
		String dir;
		String city;
		int dirType;
		CursorLoader curLoader;

		String dirs = null;
		String label = null;



		//	String[] projectionPhone = ConstantsAdmin.projectionPhone;
		//DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		curLoader = ConstantsAdmin.cursorDirsPersona;
		curLoader.setSelection(ConstantsAdmin.querySelectionDirsContactsById + contactId);
		curLoader.reset();
		Cursor dirsCursor = curLoader.loadInBackground();

//    		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
		if (dirsCursor != null && dirsCursor.getCount() > 0) {
//  	        super.startManagingCursor(phones);
			dirs = "░" + this.getString(R.string.label_direcciones) + System.getProperty("line.separator");
			while (dirsCursor.moveToNext()) {
				dir = dirsCursor.getString(dirsCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
				city = dirsCursor.getString(dirsCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
				dirType = dirsCursor.getInt(dirsCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
				int lblIndex = dirsCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL);
				if(dirType == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM){
					label = dirsCursor.getString(lblIndex);
				}else {
					label = this.getResources().getString(ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabelResource(dirType));
				}


			/*	this.getResources().getString(ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabelResource(dirType));
				if (label.equalsIgnoreCase("Custom")){
					label = dirsCursor.getString(dirsCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL));
				}*/
				if (!dir.equals("")) {
                    if(city != null && !city.equals("")){
                        dir = dir + " " + city;
                    }
                    dirs = dirs + System.getProperty("line.separator") + "¤ " + label.toUpperCase() + "» " + dir;

				}


				/*
				if (!dir.equals("")) {
					switch (dirType) {
						case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
							dirs = dirs + "\n" + Phone.TYPE_HOME + ": " + dir;
							break;
						case Phone.TYPE_MOBILE:
							dirs = dirs + "\n" + Phone.TYPE_MOBILE + ": " + dir;
							break;
						case Phone.TYPE_WORK:
							dirs = dirs + "\n" + Phone.TYPE_WORK + ": " + dir;
							break;
						case Phone.TYPE_WORK_MOBILE:
							dirs = dirs + "\n" + Phone.TYPE_WORK_MOBILE + ": " + dir;
							break;
						default:
							break;
					}
					dir = dir + city;

				}*/
			}
			dirsCursor.close();
//	        this.stopManagingCursor(phones);
		}
		return dirs;

	}

    /*
    
    private ArrayList<TipoValorDTO> importarTelDeContacto(PersonaDTO per, String contactId){
    	String phoneNumber;
    	int phoneType;
		CursorLoader nameCurLoader;
		ArrayList<TipoValorDTO> nuevosTels = new ArrayList<>();


    //	String[] projectionPhone = ConstantsAdmin.projectionPhone;
        //DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        nameCurLoader = ConstantsAdmin.cursorPhonePersona;
        nameCurLoader.setSelection(ConstantsAdmin.querySelectionPhoneContactsById + contactId);
        nameCurLoader.reset();
        Cursor phones = nameCurLoader.loadInBackground();
		TipoValorDTO nuevoTipo = null;
		String label = null;

//    		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
        if(phones != null){
//  	        super.startManagingCursor(phones);
            while (phones.moveToNext())
            {
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				int lblIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);
                if(phoneType == Phone.TYPE_CUSTOM){
					label = phones.getString(lblIndex);
				}
			//	label = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.));

                if(!phoneNumber.equals("")){
                    switch (phoneType) {
                    case Phone.TYPE_HOME :
                    	if(per.getTelParticular() == null || per.getTelParticular().equals("")){
							per.setTelParticular(phoneNumber);
						}else{
                    		if(!per.getTelParticular().equalsIgnoreCase(phoneNumber)){
                    			ConstantsAdmin.crearNuevoTel(phoneNumber, phoneType, label, phones, nuevosTels, this.getResources());
                    		}
						}

                        break;
                    case Phone.TYPE_MOBILE :
                    	if(per.getCelular() == null || per.getCelular().equals("")){
							per.setCelular(phoneNumber);
						}else{
							if(!per.getCelular().equalsIgnoreCase(phoneNumber)) {
								ConstantsAdmin.crearNuevoTel(phoneNumber, phoneType, label, phones, nuevosTels, this.getResources());
							}
                    	}
                        break;
                    case Phone.TYPE_WORK :
						if(per.getTelLaboral() == null || per.getTelLaboral().equals("")){
							per.setTelLaboral(phoneNumber);
						}else{
							if(!per.getTelLaboral().equalsIgnoreCase(phoneNumber)) {
								ConstantsAdmin.crearNuevoTel(phoneNumber, phoneType, label, phones, nuevosTels, this.getResources());
							}
						}
                        break;
                    case Phone.TYPE_WORK_MOBILE :
						if(per.getTelLaboral() == null || per.getTelLaboral().equals("")){
							per.setTelLaboral(phoneNumber);
						}else{
							ConstantsAdmin.crearNuevoTel(phoneNumber, phoneType, label,  phones, nuevosTels, this.getResources());
						}
						break;
                    default:
                    	ConstantsAdmin.crearNuevoTel(phoneNumber, phoneType, label, phones, nuevosTels, this.getResources());
					//	per.set(phoneNumber);
                        break;
                    }

                }
            }
            phones.close();
//	        this.stopManagingCursor(phones);
        }
        return nuevosTels;


    	
    }
    */

/*

    private void crearNuevoTel(String phoneNumber, int phoneType, String labelTemp, Cursor phones, ArrayList nuevosTels){
		TipoValorDTO nuevoTipo = new TipoValorDTO();
		nuevoTipo.setValor(phoneNumber);
		String label = this.getResources().getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(phoneType));
		if (phoneType == Phone.TYPE_CUSTOM){
			label = labelTemp;
		}
		nuevoTipo.setTipo(label);
		nuevosTels.add(nuevoTipo);

	}

	private void crearNuevoEmail(String mail, int mailType, String labelTemp, Cursor mails, ArrayList nuevosMails){
		TipoValorDTO nuevoTipo = new TipoValorDTO();
		nuevoTipo.setValor(mail);

		String label = this.getResources().getString(ContactsContract.CommonDataKinds.Email.getTypeLabelResource(mailType));
		if (mailType == Email.TYPE_CUSTOM){
			label = labelTemp;
		}

		nuevoTipo.setTipo(label);
		nuevosMails.add(nuevoTipo);
	}

	private void crearNuevaDireccion(String dir, int dirType, String labelTemp, Cursor dirs, ArrayList nuevasDirs){
		TipoValorDTO nuevoTipo = new TipoValorDTO();
		nuevoTipo.setValor(dir);

        String label = this.getResources().getString(ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabelResource(dirType));
        if (dirType == Email.TYPE_CUSTOM){
            label = labelTemp;
        }

		nuevoTipo.setTipo(label);
		nuevasDirs.add(nuevoTipo);
	}
    
*/
    
    
}
