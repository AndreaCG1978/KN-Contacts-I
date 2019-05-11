package com.boxico.android.kn.kncontacts;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxico.android.kn.kncontacts.persistencia.DataBaseManager;
import com.boxico.android.kn.kncontacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.kncontacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.kncontacts.persistencia.dtos.TipoValorDTO;
import com.boxico.android.kn.kncontacts.util.ConstantsAdmin;
import com.boxico.android.kn.kncontacts.util.KNSimpleCustomAdapter;

@SuppressWarnings("ALL")
public class AltaPersonaActivity extends Activity  {
	
	private ArrayAdapter<CategoriaDTO> mSpinnerAdapt = null;
	private CategoriaDTO mCategoriaSeleccionada = null;
	private PersonaDTO mPersonaSeleccionada = null;
	
	//private ArrayList<Cursor> allMyCursors = null;
	
//	private TextView mDateDisplay;
	private Button mPickDate;
	private CheckBox mCheckFechaNac;
	private int mYear;
	private int mMonth;
	private int mDay;
	private boolean mMostrarDatosPersonalesBoolean = false;
	private boolean mMostrarTelefonosBoolean = false;
	private boolean mMostrarEmailsBoolean = false;
	private boolean mMostrarDireccionesBoolean = false;
	private boolean mMostrarDatosPorCategoriaBoolean = false;
    // --Commented out by Inspection (12/11/2018 12:49):Drawable shapeLight = null;
	// --Commented out by Inspection (12/11/2018 12:44):Drawable shapeDark = null;
	// --Commented out by Inspection (12/11/2018 12:32):private TextView mDescripcionLabel = null;
	private TextView mNombContact = null;
	private Spinner mSpinner = null;

	private Button botonGuardar = null;

	private EditText mEntryApellido = null;
	private EditText mEntryNombre = null;
	private EditText mEntryDatoExtra = null;
	private EditText mEntryDescripcion = null;

    private EditText mEntryNuevoTipoTel = null;
    private EditText mEntryNuevoTipoMail = null;
    private EditText mEntryNuevoTipoDir = null;
    private EditText mEntryNuevoValTel = null;
    private EditText mEntryNuevoValMail = null;
    private EditText mEntryNuevoValDir = null;

	private ImageButton botonAddTel = null;
	private ImageButton botonAddMail = null;
	private ImageButton botonAddDir = null;
	private ImageButton configCategory = null;
	
	private List<TipoValorDTO> telefonos = new ArrayList<>();
	private List<TipoValorDTO> mails = new ArrayList<>();
	private List<TipoValorDTO> direcciones = new ArrayList<>();


	ArrayList<HashMap<String,Object>> telefonosData = null;
	ArrayList<HashMap<String,Object>> mailsData = null;
	ArrayList<HashMap<String,Object>> direccionesData = null;

	KNSimpleCustomAdapter telefonosAdapter = null;
	KNSimpleCustomAdapter mailsAdapter = null;
	KNSimpleCustomAdapter direccionesAdapter = null;


	private static final String VALOR = "VALOR";
	private static final String TIPO = "TIPO";
    private static final String ID_CAMPO = "idCampo";
	private static final String ID_TIPO_VALOR = "ID_TIPO_VALOR";
	private static final String ID_PERSONA = "ID_PERSONA";

	
	private TextView sinDatos = null;
	
	private boolean cambioCategoriaFlag = false;
	private boolean vieneDesdeDetalle = false;
	private boolean terminoCargaListado = true;
	private boolean inicioActivity = false;

	private ListView mailsList = null;
	private ListView telefonosList = null;
	private ListView direccionesList = null;

	public boolean isTerminoCargaListado() {

		return terminoCargaListado;
	}

    public ListView getTelefonosList() {

	//	return this.findViewById(R.id.listaTelefonosAlta);
		return telefonosList;
    }

	public ListView getMailsList() {
		//return this.findViewById(R.id.listaMailsAlta);
		return mailsList;
	}

	public ListView getDireccionesList() {

		//return this.findViewById(R.id.listaDireccionesAlta);
		return direccionesList;
	}

    public void setTerminoCargaListado(boolean terminoCargaListado) {
		this.terminoCargaListado = terminoCargaListado;
	}

	public boolean ismMostrarTelefonosBoolean() {
        return mMostrarTelefonosBoolean;
    }

    public void setmMostrarTelefonosBoolean(boolean mMostrarTelefonosBoolean) {
        this.mMostrarTelefonosBoolean = mMostrarTelefonosBoolean;
    }

    public boolean ismMostrarEmailsBoolean() {
        return mMostrarEmailsBoolean;
    }

    public void setmMostrarEmailsBoolean(boolean mMostrarEmailsBoolean) {
        this.mMostrarEmailsBoolean = mMostrarEmailsBoolean;
    }

    public boolean ismMostrarDireccionesBoolean() {
        return mMostrarDireccionesBoolean;
    }

    public void setmMostrarDireccionesBoolean(boolean mMostrarDireccionesBoolean) {
        this.mMostrarDireccionesBoolean = mMostrarDireccionesBoolean;
    }

    public void realzarBotonGuardar(){

        botonGuardar.setTextColor(Color.WHITE);
	}


	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS,0, R.string.menu_listar_categoria);
        item.setIcon(R.drawable.categoria_menu_icon);
        return true;

    }
/*
    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }

*/


	public void eliminarItemListView(ArrayList<HashMap<String,Object>> data, int position){
		data.remove(position);
		this.resetAdapter(data, false);

	}
    
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS:
	    	this.openListadoCategoria();
	    	return true;
	    }
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void openListadoCategoria(){
        Intent i = new Intent(this, ListadoCategoriaActivity.class);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS);
    }
    

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    //	this.resetAllMyCursors();
		if(mPersonaSeleccionada.getId() != -1){
			this.cargarPersonaDto(String.valueOf(mPersonaSeleccionada.getId()));				
		}
    	switch (requestCode) {
		case ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS:
			mSpinner.setSelection(0);
			this.configurarSpinner();
			break;
		case ConstantsAdmin.ACTIVITY_EJECUTAR_EDICION_TELEFONO:
			this.cargarTelefonos();
	        //telefonosList.setAdapter(obtenerAdapter(telefonos, telefonosData));
			this.resetAdapterTelefonos(null, true);
			break;
		case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_TELEFONO:
			this.cargarTelefonos();
	        //telefonosList.setAdapter(obtenerAdapter(telefonos, telefonosData));
			this.resetAdapterTelefonos(null, true);
			break;	
		case ConstantsAdmin.ACTIVITY_EJECUTAR_EDICION_EMAIL:
			this.cargarEmails();
	        //mailsList.setAdapter(obtenerAdapter(mails, mailsData));
			this.resetAdapterMails(null, true);
			break;
		case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_EMAIL:
			this.cargarEmails();
	        //mailsList.setAdapter(obtenerAdapter(mails, mailsData));
			this.resetAdapterMails(null, true);
			break;	
		case ConstantsAdmin.ACTIVITY_EJECUTAR_EDICION_DIRECCION:
			this.cargarDirecciones();
	        //direccionesList.setAdapter(obtenerAdapter(direcciones, direccionesData));
			this.resetAdapterDirecciones(null, true);
			break;
		case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_DIRECCION:
			this.cargarDirecciones();
		//	direccionesList.setAdapter(obtenerAdapter(direcciones, direccionesData));
			this.resetAdapterDirecciones(null, true);
			break;			
		default:
			this.configurarSpinner();
			break;
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
        try {

        	ConstantsAdmin.telefonosARegistrar = new ArrayList<>();
        	ConstantsAdmin.mailsARegistrar = new ArrayList<>();
        	ConstantsAdmin.direccionesARegistrar = new ArrayList<>();
        //	allMyCursors = new ArrayList<>();
            this.setContentView(R.layout.alta_persona);
            this.registrarWidgets();
			this.configurarBotonGuardar();
			this.configurarBotonGuardarYSalir();
            this.guardarPersonaSeleccionada(this.getIntent());
            this.configurarListaTelefonos();
            this.configurarListaDirecciones();
            this.configurarListaEmails();
            this.configurarDatePicker();
            this.configurarSpinner();
            mMostrarDatosPersonalesBoolean = true;
            this.configurarMasOMenosDatosPersonales();
            this.configurarMasOMenosTelefonos();
            this.configurarMasOMenosEmails();
            this.configurarMasOMenosDirecciones();
            this.configurarMasOMenosDatosPorCategoria();

            this.configurarBotonesAgregarTipoValor();
            if(mPersonaSeleccionada.getId() == 0 || mPersonaSeleccionada.getId() == -1){
                this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.menu_agregar_persona).toUpperCase());
            }else{
                this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.menu_editar_persona).toUpperCase());
            }
			getActionBar().setDisplayHomeAsUpEnabled(true);

            
		} catch (Exception e) {
		// TODO: handle exception
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorInicioAplicacion));
		}

	}
	
	private void  configurarBotonesAgregarTipoValor(){
		botonAddTel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaTelefono();
            }
        });
		botonAddMail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaMail();
            }
        });			
		botonAddDir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaDireccion();
            }
        });
		configCategory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openListadoCategoria();
			}
		});
	}




	public View getViewByPosition(int position, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

		if (position < firstListItemPosition || position > lastListItemPosition ) {
			return listView.getAdapter().getView(position, null, listView);
		} else {
			final int childIndex = position - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

    private void habilitarListaTelefono(boolean b, int color){
    	LinearLayout ly = null;
		EditText eTemp = null;
		Button btn = null;
		if(b) {
            getTelefonosList().setVisibility(View.VISIBLE);
		}else{
            getTelefonosList().setVisibility(View.GONE);
		}

		/*

		int size = telefonosList.getAdapter().getCount();
		for (int j = 0; j < size; j++) {
			ly = (LinearLayout)this.getViewByPosition(j, telefonosList);
			eTemp = ly.findViewById(R.id.rowValor);
			eTemp.setEnabled(b);
			eTemp.setTextColor(color);
			btn = ly.findViewById(R.id.removeButton);
			btn.setEnabled(b);
		}*/

	}

    private void habilitarListaMail(boolean b, int color){
        LinearLayout ly = null;
        EditText eTemp = null;
        Button btn = null;
		if(b) {
			getMailsList().setVisibility(View.VISIBLE);
		}else{
			getMailsList().setVisibility(View.GONE);
		}

	}

    private void habilitarListaDireccion(boolean b, int color){
        LinearLayout ly = null;
        EditText eTemp = null;
        Button btn = null;
		if(b) {
			getDireccionesList().setVisibility(View.VISIBLE);
		}else{
			getDireccionesList().setVisibility(View.GONE);
		}

	}



    private void openAltaTelefono(){
	//	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	if(mEntryNuevoValTel.getVisibility() == View.VISIBLE){
			mEntryNuevoTipoTel.setVisibility(View.GONE);
			mEntryNuevoValTel.setVisibility(View.GONE);
			mEntryNuevoTipoTel.getText().clear();
			mEntryNuevoTipoTel.append("");
			mEntryNuevoValTel.getText().clear();
			mEntryNuevoValTel.append("");
		//	botonAddTel.setTextColor(Color.WHITE);

			opacarBotonGuardar();
	//		imm.hideSoftInputFromWindow(mEntryNuevoTipoTel.getWindowToken(), 0);
			this.habilitarListaTelefono(true, Color.WHITE);
			ConstantsAdmin.hideKeyboard(this);
		}else {
			this.habilitarListaTelefono(false, Color.LTGRAY);
			mEntryNuevoTipoTel.setVisibility(View.VISIBLE);
			mEntryNuevoValTel.setVisibility(View.VISIBLE);
			realzarBotonGuardar();
			mEntryNuevoTipoTel.getText().clear();
			mEntryNuevoTipoTel.append(getResources().getText(R.string.hint_tipo));
		//	botonAddTel.setTextColor(Color.LTGRAY);

			//mEntryNuevoTipoTel.setText(getResources().getText(R.string.hint_tipo));
			mEntryNuevoTipoTel.setSelectAllOnFocus(true);
			mEntryNuevoTipoTel.requestFocus();
			//imm.showSoftInput(mEntryNuevoTipoTel, InputMethodManager.SHOW_IMPLICIT);
			ConstantsAdmin.showKeyboard(this, mEntryNuevoTipoTel);
		}


		sinDatos.setText("");

    }


    private void openAltaMail(){
	//	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(mEntryNuevoValMail.getVisibility() == View.VISIBLE){
            mEntryNuevoTipoMail.setVisibility(View.GONE);
            mEntryNuevoValMail.setVisibility(View.GONE);
         //   mEntryNuevoTipoMail.setText("");
			mEntryNuevoTipoMail.getText().clear();
			mEntryNuevoTipoMail.append("");


			mEntryNuevoValMail.getText().clear();
			mEntryNuevoValMail.append("");
	//		botonAddMail.setTextColor(Color.WHITE);
           // mEntryNuevoValMail.setText("");
            opacarBotonGuardar();
		//	imm.hideSoftInputFromWindow(mEntryNuevoTipoMail.getWindowToken(), 0);
            this.habilitarListaMail(true, Color.WHITE);
			ConstantsAdmin.hideKeyboard(this);
        }else {
			this.habilitarListaMail(false, Color.LTGRAY);
            mEntryNuevoTipoMail.setVisibility(View.VISIBLE);
            mEntryNuevoValMail.setVisibility(View.VISIBLE);
            realzarBotonGuardar();
      //      mEntryNuevoTipoMail.setText(getResources().getText(R.string.hint_tipo));

			mEntryNuevoTipoMail.getText().clear();
			mEntryNuevoTipoMail.append(getResources().getText(R.string.hint_tipo));
		//	botonAddMail.setTextColor(Color.LTGRAY);

            mEntryNuevoTipoMail.setSelectAllOnFocus(true);
            mEntryNuevoTipoMail.requestFocus();
            ConstantsAdmin.showKeyboard(this, mEntryNuevoTipoMail);
            //imm.showSoftInput(mEntryNuevoTipoMail, InputMethodManager.SHOW_IMPLICIT);

        }
        sinDatos.setText("");
    }

    private void openAltaDireccion(){
	//	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(mEntryNuevoValDir.getVisibility() == View.VISIBLE){
            mEntryNuevoTipoDir.setVisibility(View.GONE);
            mEntryNuevoValDir.setVisibility(View.GONE);
        //    mEntryNuevoTipoDir.setText("");

			mEntryNuevoTipoDir.getText().clear();
			mEntryNuevoTipoDir.append("");
	//		botonAddDir.setTextColor(Color.WHITE);
          //  mEntryNuevoValDir.setText("");

			mEntryNuevoValDir.getText().clear();
			mEntryNuevoValDir.append("");

            opacarBotonGuardar();
            //imm.hideSoftInputFromWindow(mEntryNuevoTipoDir.getWindowToken(), 0);
            this.habilitarListaDireccion(true, Color.WHITE);
			ConstantsAdmin.hideKeyboard(this);
        }else {
			this.habilitarListaDireccion(false, Color.LTGRAY);
            mEntryNuevoTipoDir.setVisibility(View.VISIBLE);
            mEntryNuevoValDir.setVisibility(View.VISIBLE);
            realzarBotonGuardar();
           // mEntryNuevoTipoDir.setText(getResources().getText(R.string.hint_tipo));

			mEntryNuevoTipoDir.getText().clear();
			mEntryNuevoTipoDir.append(getResources().getText(R.string.hint_tipo));

		//	botonAddDir.setTextColor(Color.LTGRAY);
            mEntryNuevoTipoDir.setSelectAllOnFocus(true);
            mEntryNuevoTipoDir.requestFocus();
            ConstantsAdmin.showKeyboard(this, mEntryNuevoTipoDir);
            //imm.showSoftInput(mEntryNuevoTipoDir, InputMethodManager.SHOW_IMPLICIT);

        }
        sinDatos.setText("");

    }

    private void configurarListaTelefonos(){
		this.resetAdapterTelefonos(null, true);
		getTelefonosList().setDividerHeight(25);
    }

    private void configurarListaEmails(){
//		KNSimpleCustomAdapter adapter = obtenerAdapter(mails, mailsData);
  //      mailsList.setAdapter(adapter);
		this.resetAdapterMails(null, true);
        getMailsList().setDividerHeight(25);
    }

    
    private void configurarListaDirecciones(){
		//KNSimpleCustomAdapter adapter = obtenerAdapter(direcciones, direccionesData);
        //direccionesList.setAdapter(adapter);
		this.resetAdapterDirecciones(null, true);
        getDireccionesList().setDividerHeight(25);
    }

    private HashMap<Integer,String> cargarDatosTelefonosEnLista() {
       	String val = null;
        HashMap<String, Object> hmData = null;
        int i = 0;
        Long idTipoVal = null;
        LinearLayout ll = null;
        EditText etxt = null;
		HashMap<Integer,String> datosEnLista =  new HashMap<>();
        if (telefonosData != null) {
            for (HashMap hmTemp : telefonosData) {
                hmData = hmTemp;
                val = (String) hmData.get(VALOR);
                idTipoVal = (Long) hmData.get(ID_TIPO_VALOR);
                //datosEnLista.add(hm);
				datosEnLista.put(idTipoVal.intValue(),val);
	            ll = (LinearLayout) getTelefonosList().getAdapter().getView(i, null,getTelefonosList());
                etxt = ll.findViewById(R.id.rowValor);
                etxt.setTag(idTipoVal.intValue());
                i++;
            }
        }
        return datosEnLista;
    }

	private HashMap<Integer,String> cargarDatosMailsEnLista() {
		String val = null;
		HashMap<String, Object> hmData = null;
		int i = 0;
		LinearLayout ll = null;
		EditText etxt = null;
		Long idTipoVal = null;
		HashMap<Integer,String> datosEnLista =  new HashMap<>();
		if (mailsData != null) {
			for (HashMap hmTemp : mailsData) {
				hmData = hmTemp;
				val = (String) hmData.get(VALOR);

				idTipoVal = (Long) hmData.get(ID_TIPO_VALOR);
				//datosEnLista.add(hm);
				datosEnLista.put(idTipoVal.intValue(),val);
				ll = (LinearLayout) getMailsList().getAdapter().getView(i, null,getMailsList());
				etxt = ll.findViewById(R.id.rowValor);
				etxt.setTag(idTipoVal.intValue());

			/*
				datosEnLista.put(i,val);
				ll = (LinearLayout) getMailsList().getAdapter().getView(i, null,getMailsList());
				etxt = ll.findViewById(R.id.rowValor);
				etxt.setTag(i);*/
				i++;
			}
		}
		return datosEnLista;
	}

	private HashMap<Integer,String> cargarDatosDireccionesEnLista() {
		String val = null;
		HashMap<String, Object> hmData = null;
		int i = 0;
		LinearLayout ll = null;
		EditText etxt = null;
		Long idTipoVal = null;
		HashMap<Integer,String> datosEnLista =  new HashMap<>();
		if (direccionesData != null) {
			for (HashMap hmTemp : direccionesData) {
				hmData = hmTemp;
				val = (String) hmData.get(VALOR);
				idTipoVal = (Long) hmData.get(ID_TIPO_VALOR);
				//datosEnLista.add(hm);
				datosEnLista.put(idTipoVal.intValue(),val);
				ll = (LinearLayout) getDireccionesList().getAdapter().getView(i, null,getDireccionesList());
				etxt = ll.findViewById(R.id.rowValor);
				etxt.setTag(idTipoVal.intValue());
				/*
				datosEnLista.put(i,val);
				ll = (LinearLayout) getDireccionesList().getAdapter().getView(i, null,getDireccionesList());
				etxt = ll.findViewById(R.id.rowValor);
				etxt.setTag(i); */
				i++;
			}
		}
		return datosEnLista;
	}


	public void resetAdapterTelefonos(ArrayList<HashMap<String,Object>> listdata, boolean limpiarDatos){
		if(listdata == null){
			if(telefonosData == null){
				telefonosData = new ArrayList<>();
			}else if(limpiarDatos) {
				telefonosData.clear();
			}
			HashMap<String, Object> hm = null;

			TipoValorDTO tv;
			if(telefonos != null){
				for (TipoValorDTO aLista : telefonos) {
					tv = aLista;
					hm = new HashMap<>();
					hm.put(TIPO, tv.getTipo());
					hm.put(VALOR, tv.getValor());
					hm.put(ID_TIPO_VALOR, tv.getId());
					hm.put(ID_PERSONA, tv.getIdPersona());
					telefonosData.add(hm);

				}
			}
		}else{
			telefonosData = listdata;
		}


		String[] from = {TIPO, VALOR};
		int[] to={R.id.rowTipo,R.id.rowValor};
		if(telefonosAdapter == null) {
			telefonosAdapter = new KNSimpleCustomAdapter(this, telefonosData, R.layout.row_item_alta, from, to);
		}else {
			telefonosAdapter.setData(telefonosData);
		}
		getTelefonosList().setAdapter(telefonosAdapter);
		telefonosAdapter.notifyDataSetChanged();
	}

	private void resetAdapterDirecciones(ArrayList<HashMap<String,Object>> listdata, boolean limpiarDatos){
		if(listdata == null){
			if(direccionesData == null){
				direccionesData = new ArrayList<>();
			}else if(limpiarDatos) {
				direccionesData.clear();
			}
			HashMap<String, Object> hm = null;

			TipoValorDTO tv;
			if(direcciones != null){
				for (TipoValorDTO aLista : direcciones) {
					tv = aLista;
					hm = new HashMap<>();
					hm.put(TIPO, tv.getTipo());
					hm.put(VALOR, tv.getValor());
					hm.put(ID_TIPO_VALOR, tv.getId());
					hm.put(ID_PERSONA, tv.getIdPersona());
					direccionesData.add(hm);

				}
			}
		}else{
			direccionesData = listdata;
		}


		String[] from = {TIPO, VALOR};
		int[] to={R.id.rowTipo,R.id.rowValor};
		if(direccionesAdapter == null) {
			direccionesAdapter = new KNSimpleCustomAdapter(this, direccionesData, R.layout.row_item_alta, from, to);
		}else {
			direccionesAdapter.setData(direccionesData);
		}
		getDireccionesList().setAdapter(direccionesAdapter);
		direccionesAdapter.notifyDataSetChanged();
	}

	private void resetAdapterMails(ArrayList<HashMap<String,Object>> listdata, boolean limpiarDatos){
		if(listdata == null){
			if(mailsData == null){
				mailsData = new ArrayList<>();
			}else if(limpiarDatos) {
				mailsData.clear();
			}
			HashMap<String, Object> hm = null;

			TipoValorDTO tv;
			if(mails != null){
				for (TipoValorDTO aLista : mails) {
					tv = aLista;
					hm = new HashMap<>();
					hm.put(TIPO, tv.getTipo());
					hm.put(VALOR, tv.getValor());
					hm.put(ID_TIPO_VALOR, tv.getId());
					hm.put(ID_PERSONA, tv.getIdPersona());
					mailsData.add(hm);

				}
			}
		}else{
			mailsData = listdata;
		}


		String[] from = {TIPO, VALOR};
		int[] to={R.id.rowTipo,R.id.rowValor};
		if(mailsAdapter == null) {
			mailsAdapter = new KNSimpleCustomAdapter(this, mailsData, R.layout.row_item_alta, from, to);
		}else {
			mailsAdapter.setData(mailsData);
		}
		getMailsList().setAdapter(mailsAdapter);
		mailsAdapter.notifyDataSetChanged();
	}


	public void resetAdapter(ArrayList<HashMap<String,Object>> data, boolean limpiarDatos){
    	if(this.ismMostrarTelefonosBoolean()){
    		this.resetAdapterTelefonos(data, limpiarDatos);
		}else if(this.ismMostrarEmailsBoolean()){
    		this.resetAdapterMails(data, limpiarDatos);
		}else if(this.ismMostrarDireccionesBoolean()){
    		this.resetAdapterDirecciones(data, limpiarDatos);
		}

	}

	private void registrarWidgets(){
	//	mDateDisplay = this.findViewById(R.id.labelDateDisplay);
		mPickDate = this.findViewById(R.id.buttonPickDate);
		mCheckFechaNac = this.findViewById(R.id.checkFechaNacimiento);
     //   Resources res = getResources();
	//	mDescripcionLabel = this.findViewById(R.id.label_descripcion);

		mSpinner = this.findViewById(R.id.spinnerCategorias_alta_persona);
		mEntryApellido = this.findViewById(R.id.entryApellido);
		mEntryNombre = this.findViewById(R.id.entryNombre);
		mEntryDatoExtra = this.findViewById(R.id.entryDatoExtra);
		mEntryDescripcion = this.findViewById(R.id.entryDescripcion);
		mNombContact = this.findViewById(R.id.nombre_contacto);

        mEntryNuevoTipoTel = this.findViewById(R.id.nuevoTipoTel);
        mEntryNuevoTipoMail = this.findViewById(R.id.nuevoTipoMail);
        mEntryNuevoTipoDir = this.findViewById(R.id.nuevoTipoDir);

        mEntryNuevoValTel = this.findViewById(R.id.nuevoValorTel);
        mEntryNuevoValMail = this.findViewById(R.id.nuevoValorMail);
        mEntryNuevoValDir = this.findViewById(R.id.nuevoValorDir);

        telefonosList = this.findViewById(R.id.listaTelefonosAlta);
		mailsList = this.findViewById(R.id.listaMailsAlta);
		direccionesList = this.findViewById(R.id.listaDireccionesAlta);

		sinDatos = this.findViewById(R.id.label_sinDatos);
		botonAddTel = this.findViewById(R.id.addTelefono);
		botonAddMail = this.findViewById(R.id.addMail);
		botonAddDir = this.findViewById(R.id.addDir);
		configCategory = this.findViewById(R.id.configCategory);

        mEntryApellido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                realzarBotonGuardar();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEntryNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                realzarBotonGuardar();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEntryDatoExtra.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                realzarBotonGuardar();
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });

        mEntryDescripcion.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                realzarBotonGuardar();
            }



            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });

        mCheckFechaNac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realzarBotonGuardar();
            }
        });





    }
	
	private void cargarPersonaDto(String idPerString){
		int idPer = Integer.valueOf(idPerString);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mPersonaSeleccionada = ConstantsAdmin.obtenerPersonaId(idPer, mDBManager);
	}
	
	private void cargarEntriesConPersonaDto(){
    	if(mPersonaSeleccionada.getApellido() != null && !mPersonaSeleccionada.getApellido().equals("")){
	//		mEntryApellido.setText(mPersonaSeleccionada.getApellido());
			mEntryApellido.getText().clear();
			mEntryApellido.append(mPersonaSeleccionada.getApellido());
		}


		if(mPersonaSeleccionada.getNombres() != null && !mPersonaSeleccionada.getNombres().equals("")){
	//		mEntryNombre.setText(mPersonaSeleccionada.getNombres());
			mEntryNombre.getText().clear();
			mEntryNombre.append(mPersonaSeleccionada.getNombres());
		}

	//mEntryDatoExtra.setText(mPersonaSeleccionada.getDatoExtra());

		mEntryDatoExtra.getText().clear();
		if(mPersonaSeleccionada.getDatoExtra() != null) {
			mEntryDatoExtra.append(mPersonaSeleccionada.getDatoExtra());
		}

//		mEntryDescripcion.setText(mPersonaSeleccionada.getDescripcion());
		mEntryDescripcion.getText().clear();
		if(mPersonaSeleccionada.getDescripcion() != null) {
			mEntryDescripcion.append(mPersonaSeleccionada.getDescripcion());
		}


		this.cargarTelefonos();
		this.cargarEmails();
		this.cargarDirecciones();
		this.cargarEntryFechaNacimiento();
	}
	/*
	private void cargarTiposTelefono(){
		tiposTelefono.add(this.getString(R.string.hint_particular).toUpperCase());
		tiposTelefono.add(this.getString(R.string.hint_telMovil).toUpperCase());
		tiposTelefono.add(this.getString(R.string.hint_laboral).toUpperCase());
	}
	
	private void cargarTiposEmail(){
		tiposEmail.add(this.getString(R.string.hint_particular).toUpperCase());
		tiposEmail.add(this.getString(R.string.hint_laboral).toUpperCase());
		tiposEmail.add(this.getString(R.string.hint_otro).toUpperCase());
	}
	
	private void cargarTiposDireccion(){
		tiposDireccion.add(this.getString(R.string.hint_particular).toUpperCase());
		tiposDireccion.add(this.getString(R.string.hint_laboral).toUpperCase());
	}*/
	
	private void cargarTelefonos(){
		// ACA DEBERIA RECUPERAR LOS TELEFONOS EXTRAS
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		telefonos = ConstantsAdmin.obtenerTelefonosIdPersona( mPersonaSeleccionada.getId(), mDBManager);

	}

	
	private void cargarEmails(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mails = ConstantsAdmin.obtenerEmailsIdPersona( mPersonaSeleccionada.getId(), mDBManager);

	}
	
	
	private void cargarDirecciones(){
        DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        direcciones = ConstantsAdmin.obtenerDireccionesIdPersona(mPersonaSeleccionada.getId(), mDBManager);

	}
	

	private void cargarMasElementos(List<TipoValorDTO> listaOrigen, List<TipoValorDTO> listaDestino){
		Iterator<TipoValorDTO> it = listaOrigen.iterator();
		TipoValorDTO tv;
		while(it.hasNext()){
			tv = it.next();
			listaDestino.add(tv);
		//	tipos.add(tv.getTipo());
			
		}
	}


	private TipoValorDTO cargarTipoValor(String tipo, String valor, String idPer){
		TipoValorDTO tv;
		tv = new TipoValorDTO();
		tv.setId(0);
		tv.setIdPersona(idPer);
		tv.setTipo(tipo);
		tv.setValor(valor);
		return tv;
	}

	private void cargarEntryFechaNacimiento(){
		
		if(mPersonaSeleccionada.getFechaNacimiento()!= null && !mPersonaSeleccionada.getFechaNacimiento().equals("")){
		//	mDateDisplay.setVisibility(View.VISIBLE);
		//	mDateDisplay.setText(formatFechaToView(mPersonaSeleccionada.getFechaNacimiento()));
			mPickDate.setEnabled(true);
			mPickDate.setText(formatFechaToView(mPersonaSeleccionada.getFechaNacimiento()));
			mCheckFechaNac.setChecked(true);
		}else{
	//		mDateDisplay.setVisibility(View.GONE);
			mPickDate.setEnabled(false);
			mPickDate.setText(getResources().getString(R.string.label_fechaNacimiento));
			mCheckFechaNac.setChecked(false);
			
		}
		
	}
	
    private String formatFechaToView(String text){
    	String result = null;
    	if(text != null){
	    	Locale locale = this.getResources().getConfiguration().locale;
	    	String lang = locale.getLanguage();
	    	String temp;
			int mes;
	    	String[] array = text.split(ConstantsAdmin.SEPARADOR_FECHA);
	    	temp = array[1];
	    	mes = Integer.valueOf(temp);
	    	
	    	if(lang.equals(Locale.ENGLISH.toString())){
	    		result = textToFechaIngles(array[0], mes, array[2]);
	    	}else if(lang.equals(ConstantsAdmin.LANG_ESPANOL)){
	    		result = textToFechaEspanol(array[0], mes, array[2]);
	    	}else{
	    		result = textToFechaGenerica(array[0], mes, array[2]);
	    	}
	    	
	    }
    	return result;
    }
    
    private String textToFechaGenerica(String diaText, int mesInt, String anioText){
    	return diaText + "/" + String.valueOf(mesInt) + "/" + anioText;
    }
    
    
    private String textToFechaEspanol(String diaText, int mesInt, String anioText){
    	return diaText + " de " + obtenerMes(mesInt) + " de " + anioText;	
    }
    
    private String textToFechaIngles(String diaText, int mesInt, String anioText){
    	return obtenerMes(mesInt) + " " + diaText + ", " + anioText;	
    }

    
    private String obtenerMes(int mesInt){
    	String mes = null;
    	switch (mesInt) {
		case 1:
			mes = getString(R.string.smesEne); 
			break;
		case 2:
			mes = getString(R.string.smesFeb); 
			break;
		case 3:
			mes = getString(R.string.smesMar); 
			break;
		case 4:
			mes = getString(R.string.smesAbr); 
			break;
		case 5:
			mes = getString(R.string.smesMay); 
			break;
		case 6:
			mes = getString(R.string.smesJun); 
			break;
		case 7:
			mes = getString(R.string.smesJul); 
			break;
		case 8:
			mes = getString(R.string.smesAgo); 
			break;
		case 9:
			mes = getString(R.string.smesSep); 
			break;
		case 10:
			mes = getString(R.string.smesOct); 
			break;
		case 11:
			mes = getString(R.string.smesNov); 
			break;
		case 12:
			mes = getString(R.string.smesDic); 
			break;

		default:
			break;
		}

    	return mes;
    	
    }

	
	private void guardarPersonaSeleccionada(Intent intent){
		String idPerString;
	//	this.cargarTiposTelefono();
	//	this.cargarTiposEmail();
	//	this.cargarTiposDireccion();
		if(intent.hasExtra(ConstantsAdmin.PERSONA_SELECCIONADA)){
			idPerString = (String)intent.getExtras().get(ConstantsAdmin.PERSONA_SELECCIONADA);
			this.cargarPersonaDto(idPerString);
			this.cargarEntriesConPersonaDto();
			mNombContact.setVisibility(View.VISIBLE);
			String nombC = "";
			if(mPersonaSeleccionada.getApellido() != null){
                nombC = mPersonaSeleccionada.getApellido().toUpperCase();
            }
			if(mPersonaSeleccionada.getNombres() != null){
				nombC = nombC + " " + mPersonaSeleccionada.getNombres();
			}
			mNombContact.setText(nombC);
			vieneDesdeDetalle  = true;
		}else{
			mPersonaSeleccionada = new PersonaDTO();
			cambioCategoriaFlag = true;
			mNombContact.setVisibility(View.GONE);
			this.cargarEntryFechaNacimiento();
			vieneDesdeDetalle = false;
		}
 
	}
	
	private void cargarVariablesDeFechaNacimiento(String fechaTemp){
		String[] temp;
		if(fechaTemp != null && !fechaTemp.equals("")){
			temp = fechaTemp.split(ConstantsAdmin.SEPARADOR_FECHA);
			mDay = Integer.valueOf(temp[0]);
			mMonth = Integer.valueOf(temp[1]);
			mYear = Integer.valueOf(temp[2]);
		}
	}
	
	private void configurarBotonGuardarYSalir(){
		Button boton = this.findViewById(R.id.butttonRegistrarYSalir);
		boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(validarEntradaDeDatos()){
                    registrarPersona();
                    setResult(RESULT_OK);
                   	finish();

                    
            	}else{
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_persona_incompleta, Toast.LENGTH_LONG);
            		t.show();
            		
            	}
            }
        });
	}
	
	
	
	private void configurarBotonGuardar(){
		botonGuardar = this.findViewById(R.id.butttonRegistrarPersonaFinal);
		botonGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(validarEntradaDeDatos()){
                    registrarPersona();
	           		Toast t = Toast.makeText(getApplicationContext(), R.string.label_guardar_parcial, Toast.LENGTH_LONG);
            		t.show();
            		mostrarTodo();
             
                    
            	}else{
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_persona_incompleta, Toast.LENGTH_LONG);
            		t.show();
            		
            	}
            }
        });
	}
	
	private boolean validarEntradaDeDatos(){
		boolean estaOk;
		String apellidoText = mEntryApellido.getText().toString();
		String nombreText = mEntryNombre.getText().toString();
		estaOk = !apellidoText.equals("") || !nombreText.equals("");
		return estaOk;
	}
	
	private void registrarPersona(){
		try {
			String catTemp = null;
			String catRelativoTemp = null;
			if(mPersonaSeleccionada == null){
				mPersonaSeleccionada = new PersonaDTO();
			}
			mPersonaSeleccionada.setApellido(mEntryApellido.getText().toString());
			mPersonaSeleccionada.setNombres(mEntryNombre.getText().toString());
			mPersonaSeleccionada.setDatoExtra(mEntryDatoExtra.getText().toString());
			mPersonaSeleccionada.setDescripcion(mEntryDescripcion.getText().toString());

			if(mCheckFechaNac.isChecked()){
				String fechaNacimiento = String.valueOf(mDay) + ConstantsAdmin.SEPARADOR_FECHA + String.valueOf(mMonth) + ConstantsAdmin.SEPARADOR_FECHA + String.valueOf(mYear);
				mPersonaSeleccionada.setFechaNacimiento(fechaNacimiento);
			}else{
				mPersonaSeleccionada.setFechaNacimiento(null);
			}
			if(mCategoriaSeleccionada == null){
				this.seleccionarPrimerCategoria();
			}
			if(cambioCategoriaFlag){
				catTemp = Normalizer.normalize(mCategoriaSeleccionada.getNombreReal(), Normalizer.Form.NFC);
				catRelativoTemp = Normalizer.normalize(mCategoriaSeleccionada.getNombreRelativo(), Normalizer.Form.NFC);
				mPersonaSeleccionada.setCategoriaId(mCategoriaSeleccionada.getId());
				mPersonaSeleccionada.setCategoriaNombre(catTemp);
				mPersonaSeleccionada.setCategoriaNombreRelativo(catRelativoTemp);
				cambioCategoriaFlag = false;
			}
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			long idPer = ConstantsAdmin.crearPersona(mPersonaSeleccionada, false, mDBManager);
			if(idPer != -1 && idPer != 0){
			    mPersonaSeleccionada.setId(idPer);
            }
	//		this.getIntent().putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(idPer));

			if(mMostrarTelefonosBoolean) {
				this.registrarTelefonos(idPer);
			}else if(mMostrarEmailsBoolean){
				this.registrarMails(idPer);
			}else if(mMostrarDireccionesBoolean){
				this.registrarDirecciones(idPer);
			}
			/*
			if(ConstantsAdmin.direccionesARegistrar != null && ConstantsAdmin.direccionesARegistrar.size() > 0){
				this.registrarDirecciones(ConstantsAdmin.direccionesARegistrar, idPer);
				ConstantsAdmin.direccionesARegistrar = null;
			}*/

			ConstantsAdmin.hideKeyboard(this);
			mCategoriaSeleccionada = null;
			mPersonaSeleccionada.setId(idPer);
			mNombContact.setVisibility(View.VISIBLE);
			if(mPersonaSeleccionada.getApellido() != null){
                mNombContact.setText(mPersonaSeleccionada.getApellido().toUpperCase() + " " + mPersonaSeleccionada.getNombres());
            }else{
                mNombContact.setText(mPersonaSeleccionada.getNombres());
            }


			//mNombContact.setText(mPersonaSeleccionada.getApellido().toUpperCase() + " " + mPersonaSeleccionada.getNombres());
			ConstantsAdmin.resetPersonasOrganizadas();


		} catch (Exception e) {
			if(mPersonaSeleccionada.getId() == -1){
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorAltaContacto));
			}else{
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorActualizacionContacto));
			}
		}

	}

	private void registrarMails(long idPer){
		ArrayList<HashMap<String,Object>> listdata= new ArrayList<>();
		HashMap<String, Object> hm;
		TipoValorDTO tv = null;
		List<TipoValorDTO> mailsTemp = new ArrayList<>();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		boolean huboCambios = false;

		// VERIFICO SI HAY ALGUN NUEVO TELEFONO A REGISTRAR
		if(mEntryNuevoValMail.getVisibility() == View.VISIBLE && !mEntryNuevoTipoMail.getText().toString().equals("")
				&& !mEntryNuevoValMail.getText().toString().equals("")){
			tv = new TipoValorDTO();
			tv.setTipo(mEntryNuevoTipoMail.getText().toString());
			tv.setValor(mEntryNuevoValMail.getText().toString());
			tv.setIdPersona(String.valueOf(idPer));
			mailsTemp.add(tv);


			mEntryNuevoTipoMail.getText().clear();
			mEntryNuevoTipoMail.append("");

			mEntryNuevoValMail.getText().clear();
			mEntryNuevoValMail.append("");

//			mEntryNuevoTipoMail.setText("");
	//		mEntryNuevoValMail.setText("");
			huboCambios = true;
		}else {

			KNSimpleCustomAdapter adapter = (KNSimpleCustomAdapter) getMailsList().getAdapter();
			listdata = adapter.getData();

			int i = 0;
			LinearLayout vLayout = null;
			EditText eTemp = null;
			String tipoTemp, valTemp = null;

			if (listdata != null) {
				Long idTipoValor = null;
				for (HashMap hmTemp : listdata) {
					hm = hmTemp;
					tipoTemp = (String) mailsAdapter.getData().get(i).get(TIPO);
					idTipoValor = (Long) mailsAdapter.getData().get(i).get(ID_TIPO_VALOR);
					valTemp = mailsAdapter.getDatosEnLista().get(idTipoValor.intValue());
					if (!tipoTemp.equals("") && !valTemp.equals("")) {
						tv = new TipoValorDTO();
						tv.setIdPersona((String) hm.get(ID_PERSONA));
						tv.setId((Long) hm.get(ID_TIPO_VALOR));
						tv.setTipo(tipoTemp);
						tv.setValor(valTemp);
						mailsTemp.add(tv);
						huboCambios = true;
					}
					i++;
				}
			}
		/*	if (listdata != null) {
				for (HashMap hmTemp : listdata) {
					hm = hmTemp;
					vLayout = (LinearLayout) getMailsList().getChildAt(i);
					eTemp = vLayout.findViewById(R.id.rowTipo);
					tipoTemp = eTemp.getText().toString();
					eTemp = vLayout.findViewById(R.id.rowValor);
					valTemp = eTemp.getText().toString();
					if (!tipoTemp.equals("") && !valTemp.equals("")) {
						tv = new TipoValorDTO();
						tv.setIdPersona((String) hm.get(ID_PERSONA));
						tv.setId((Long) hm.get(ID_TIPO_VALOR));
						tv.setTipo(tipoTemp);
						tv.setValor(valTemp);
						mailsTemp.add(tv);
						huboCambios = true;
					}
					i++;

				}
			}*/
/*
			if (listdata != null) {
				for (HashMap hmTemp : listdata) {
					hm = hmTemp;
					vLayout = (LinearLayout) getMailsList().getChildAt(i);
					eTemp = vLayout.findViewById(R.id.rowTipo);
					tipoTemp = eTemp.getText().toString();
					eTemp = vLayout.findViewById(R.id.rowValor);
					valTemp = eTemp.getText().toString();
					if (!tipoTemp.equals("") && !valTemp.equals("")) {
						tv = new TipoValorDTO();
						tv.setIdPersona((String) hm.get(ID_PERSONA));
						tv.setId((Long) hm.get(ID_TIPO_VALOR));
						tv.setTipo(tipoTemp);
						tv.setValor(valTemp);
						mailsTemp.add(tv);
						huboCambios = true;

					}
					i++;

				}
*/

			/*
				for (HashMap hmTemp : listdata) {
					hm = hmTemp;
					tipoTemp = (String)mailsAdapter.getData().get(i).get(TIPO);
					valTemp = mailsAdapter.getDatosEnLista().get(i);
					if (!tipoTemp.equals("") && !valTemp.equals("")) {
						tv = new TipoValorDTO();
						tv.setIdPersona((String) hm.get(ID_PERSONA));
						tv.setId((Long) hm.get(ID_TIPO_VALOR));
						tv.setTipo(tipoTemp);
						tv.setValor(valTemp);
						mailsTemp.add(tv);
						huboCambios = true;
					}
					i++;
				}*/

		}
		ConstantsAdmin.registrarMails(mailsTemp, mDBManager);

		if(ConstantsAdmin.getMailsAEliminar() != null && ConstantsAdmin.getMailsAEliminar().size() > 0){
			ConstantsAdmin.eliminarMails(ConstantsAdmin.getMailsAEliminar(), mDBManager);
			ConstantsAdmin.setMailsAEliminar(null);
			huboCambios = true;
		}


		if(huboCambios){
			this.cargarEmails();
		//	mailsList.setAdapter(obtenerAdapter(mails, mailsData));
			this.configListView(getMailsList());
			this.resetAdapterMails(null, true);
		}
	}



	private void registrarTelefonos(long idPer){
		ArrayList<HashMap<String,Object>> listdata= new ArrayList<>();
		HashMap<String, Object> hm;
    	TipoValorDTO tv = null;
		List<TipoValorDTO> telefonosTemp = new ArrayList<>();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		boolean huboCambios = false;

		// VERIFICO SI HAY ALGUN NUEVO TELEFONO A REGISTRAR
    	if(mEntryNuevoValTel.getVisibility() == View.VISIBLE && !mEntryNuevoTipoTel.getText().toString().equals("")
		&& !mEntryNuevoValTel.getText().toString().equals("")){
    		tv = new TipoValorDTO();
    		tv.setTipo(mEntryNuevoTipoTel.getText().toString());
    		tv.setValor(mEntryNuevoValTel.getText().toString());
    		tv.setIdPersona(String.valueOf(idPer));
			telefonosTemp.add(tv);

			mEntryNuevoTipoTel.getText().clear();
			mEntryNuevoTipoTel.append("");

			mEntryNuevoValTel.getText().clear();
			mEntryNuevoValTel.append("");
			huboCambios = true;
		}else {

			KNSimpleCustomAdapter adapter = (KNSimpleCustomAdapter) getTelefonosList().getAdapter();
			listdata = adapter.getData();

			int i = 0;
			LinearLayout vLayout = null;
			EditText eTemp = null;
			String tipoTemp, valTemp = null;
			if (listdata != null) {
/*
				for (HashMap hmTemp : listdata) {
					hm = hmTemp;
					vLayout = (LinearLayout) getTelefonosList().getChildAt(i);
					if(vLayout != null){
                        eTemp = vLayout.findViewById(R.id.rowTipo);
                        tipoTemp = eTemp.getText().toString();
                        eTemp = vLayout.findViewById(R.id.rowValor);
                        valTemp = eTemp.getText().toString();
                        if (!tipoTemp.equals("") && !valTemp.equals("")) {
                            tv = new TipoValorDTO();
                            tv.setIdPersona((String) hm.get(ID_PERSONA));
                            tv.setId((Long) hm.get(ID_TIPO_VALOR));
                            tv.setTipo(tipoTemp);
                            tv.setValor(valTemp);
                            telefonosTemp.add(tv);
                            huboCambios = true;

                        }
					}
					i++;

				}*/

				Long idTipoValor = null;
				for (HashMap hmTemp : listdata) {
					hm = hmTemp;
                    tipoTemp = (String)telefonosAdapter.getData().get(i).get(TIPO);
                    idTipoValor = (Long)telefonosAdapter.getData().get(i).get(ID_TIPO_VALOR);
					valTemp = telefonosAdapter.getDatosEnLista().get(idTipoValor.intValue());
					if (!tipoTemp.equals("") && !valTemp.equals("")) {
						tv = new TipoValorDTO();
						tv.setIdPersona((String) hm.get(ID_PERSONA));
						tv.setId((Long) hm.get(ID_TIPO_VALOR));
						tv.setTipo(tipoTemp);
						tv.setValor(valTemp);
						telefonosTemp.add(tv);
						huboCambios = true;
					}
					i++;
				}
			}
		}
		ConstantsAdmin.registrarTelefonos(telefonosTemp, mDBManager);

		if(ConstantsAdmin.getTelefonosAEliminar() != null && ConstantsAdmin.getTelefonosAEliminar().size() > 0){
			ConstantsAdmin.eliminarTelefonos(ConstantsAdmin.getTelefonosAEliminar(), mDBManager);
			ConstantsAdmin.setTelefonosAEliminar(null);
			huboCambios = true;
		}


		if(huboCambios){
			this.cargarTelefonos();
			this.configListView(getTelefonosList());
			this.resetAdapterTelefonos(null, true);
			/*
			this.setTerminoCargaListado(false);
			telefonosList.setAdapter(obtenerAdapterTelefono(telefonos));*/


		}




	}


    private void registrarDirecciones(long idPer){
        ArrayList<HashMap<String,Object>> listdata= new ArrayList<>();
        HashMap<String, Object> hm;
        TipoValorDTO tv = null;
        List<TipoValorDTO> dirsTemp = new ArrayList<>();
        DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        boolean huboCambios = false;

        // VERIFICO SI HAY ALGUN NUEVO TELEFONO A REGISTRAR
        if(mEntryNuevoValDir.getVisibility() == View.VISIBLE && !mEntryNuevoTipoDir.getText().toString().equals("")
                && !mEntryNuevoValDir.getText().toString().equals("")){
            tv = new TipoValorDTO();
            tv.setTipo(mEntryNuevoTipoDir.getText().toString());
            tv.setValor(mEntryNuevoValDir.getText().toString());
            tv.setIdPersona(String.valueOf(idPer));
            dirsTemp.add(tv);

			mEntryNuevoTipoDir.getText().clear();
			mEntryNuevoTipoDir.append("");

			mEntryNuevoValDir.getText().clear();
			mEntryNuevoValDir.append("");



          //  mEntryNuevoTipoDir.setText("");
         //   mEntryNuevoValDir.setText("");
            huboCambios = true;
        }else {

            KNSimpleCustomAdapter adapter = (KNSimpleCustomAdapter) getDireccionesList().getAdapter();
            listdata = adapter.getData();

            int i = 0;
            LinearLayout vLayout = null;
            EditText eTemp = null;
            String tipoTemp, valTemp = null;
			if (listdata != null) {
				Long idTipoValor = null;
				for (HashMap hmTemp : listdata) {
					hm = hmTemp;
					tipoTemp = (String) direccionesAdapter.getData().get(i).get(TIPO);
					idTipoValor = (Long) direccionesAdapter.getData().get(i).get(ID_TIPO_VALOR);
					valTemp = direccionesAdapter.getDatosEnLista().get(idTipoValor.intValue());
					if (!tipoTemp.equals("") && !valTemp.equals("")) {
						tv = new TipoValorDTO();
						tv.setIdPersona((String) hm.get(ID_PERSONA));
						tv.setId((Long) hm.get(ID_TIPO_VALOR));
						tv.setTipo(tipoTemp);
						tv.setValor(valTemp);
						dirsTemp.add(tv);
						huboCambios = true;
					}
					i++;
				}
			}
            /*
            if (listdata != null) {
                for (HashMap hmTemp : listdata) {
                    hm = hmTemp;
                    vLayout = (LinearLayout) getDireccionesList().getChildAt(i);
                    eTemp = vLayout.findViewById(R.id.rowTipo);
                    tipoTemp = eTemp.getText().toString();
                    eTemp = vLayout.findViewById(R.id.rowValor);
                    valTemp = eTemp.getText().toString();
                    if (!tipoTemp.equals("") && !valTemp.equals("")) {
                        tv = new TipoValorDTO();
                        tv.setIdPersona((String) hm.get(ID_PERSONA));
                        tv.setId((Long) hm.get(ID_TIPO_VALOR));
                        tv.setTipo(tipoTemp);
                        tv.setValor(valTemp);
                        dirsTemp.add(tv);
                        huboCambios = true;

                    }
                    i++;

                }
            }*/
        }
        ConstantsAdmin.registrarDirecciones(dirsTemp, mDBManager);

        if(ConstantsAdmin.getDireccionesAEliminar() != null && ConstantsAdmin.getDireccionesAEliminar().size() > 0){
            ConstantsAdmin.eliminarDirecciones(ConstantsAdmin.getDireccionesAEliminar(), mDBManager);
            ConstantsAdmin.setDireccionesAEliminar(null);
            huboCambios = true;
        }

        if(huboCambios){
            this.cargarDirecciones();
            this.configListView(getDireccionesList());
			this.resetAdapterDirecciones(null, true);
	    }

    }


    /*
	private void registrarTelefonos(List<TipoValorDTO> telefonos, long idPer){
		Iterator<TipoValorDTO> it = telefonos.iterator();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		TipoValorDTO tv;
		while(it.hasNext()){
			tv = it.next();
			tv.setIdPersona(String.valueOf(idPer));
		}
		ConstantsAdmin.registrarTelefonos(telefonos, mDBManager);
		
	}

	private void registrarMails(List<TipoValorDTO> mails, long idPer){
		Iterator<TipoValorDTO> it = mails.iterator();
		TipoValorDTO tv;
		while(it.hasNext()){
			tv = it.next();
			tv.setIdPersona(String.valueOf(idPer));
		}
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.registrarMails(mails, mDBManager);
		
	}

	private void registrarDirecciones(List<TipoValorDTO> direcciones, long idPer){
		Iterator<TipoValorDTO> it = direcciones.iterator();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		TipoValorDTO tv;
		while(it.hasNext()){
			tv = it.next();
			tv.setIdPersona(String.valueOf(idPer));
		}
		ConstantsAdmin.registrarDirecciones(mails, mDBManager);
		
	}
		
	*/
	
	private void seleccionarPrimerCategoria(){
		mCategoriaSeleccionada = this.mSpinnerAdapt.getItem(0);
	}

	private void configurarMasOMenosDatosPersonales(){

		ImageButton icon = this.findViewById(R.id.menu_icon_identification);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarDatosPersonalesBoolean){
	            	mMostrarDatosPersonalesBoolean = !mMostrarDatosPersonalesBoolean;
	            	mMostrarDatosPorCategoriaBoolean = false;
	            	mMostrarDireccionesBoolean = false;
	            	mMostrarEmailsBoolean = false;
	            	mMostrarTelefonosBoolean = false;
	                mostrarTodo();
            	}
            }
        });
	}

	
	private void configurarMasOMenosTelefonos(){
		ImageButton icon = this.findViewById(R.id.menu_icon_phone);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarTelefonosBoolean){
            		mMostrarTelefonosBoolean = !mMostrarTelefonosBoolean;
                	mMostrarDatosPersonalesBoolean = false;
                	mMostrarDatosPorCategoriaBoolean = false;
                	mMostrarDireccionesBoolean = false;
                	mMostrarEmailsBoolean = false;
                              
                    mostrarTodo();
            	}
                
            }
        });
	}


	
	
	private void configurarMasOMenosEmails(){
		ImageButton icon = this.findViewById(R.id.menu_icon_mail);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarEmailsBoolean){
	                mMostrarEmailsBoolean = !mMostrarEmailsBoolean;
	            	mMostrarDatosPersonalesBoolean = false;
	            	mMostrarDatosPorCategoriaBoolean = false;
	            	mMostrarDireccionesBoolean = false;
	            	mMostrarTelefonosBoolean = false;
	                mostrarTodo();
            	}
            }
        });
	}
	
	private void configurarMasOMenosDirecciones(){
		ImageButton icon = this.findViewById(R.id.menu_icon_addres);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarDireccionesBoolean){
	                mMostrarDireccionesBoolean = !mMostrarDireccionesBoolean;
	            	mMostrarDatosPersonalesBoolean = false;
	            	mMostrarDatosPorCategoriaBoolean = false;
	            	mMostrarEmailsBoolean = false;
	            	mMostrarTelefonosBoolean = false;
	                mostrarTodo();
            	}
            }
        });
	}
	
	private void mostrarTodo(){
		ConstantsAdmin.telefonosARegistrar = null;
		ConstantsAdmin.setTelefonosAEliminar(null);
		ConstantsAdmin.mailsARegistrar = null;
		ConstantsAdmin.setMailsAEliminar(null);
		ConstantsAdmin.direccionesARegistrar = null;
		ConstantsAdmin.setDireccionesAEliminar(null);
		this.opacarBotonGuardar();
		this.mostrarDatosPersonales();
		this.mostrarTelefonos();
		this.mostrarEmails();
		this.mostrarDirecciones();
		this.mostrarDatosPorCategoria();
	//	botonAddTel.setTextColor(Color.WHITE);
	//	botonAddMail.setTextColor(Color.WHITE);
	//	botonAddDir.setTextColor(Color.WHITE);
	//	ConstantsAdmin.hideKeyboard(this);
	}

	private void opacarBotonGuardar(){
		botonGuardar.setTextColor(getResources().getColor(R.color.color_negro));
	}
	
	private void configurarMasOMenosDatosPorCategoria(){
		ImageButton icon = this.findViewById(R.id.menu_icon_category);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarDatosPorCategoriaBoolean){
	                mMostrarDatosPorCategoriaBoolean = !mMostrarDatosPorCategoriaBoolean;
	            	mMostrarDatosPersonalesBoolean = false;
	            	mMostrarDireccionesBoolean = false;
	            	mMostrarEmailsBoolean = false;
	            	mMostrarTelefonosBoolean = false;
	                mostrarTodo();
            	}
            }
        });
	}
	



	private void configListView(ListView lv){
		this.setTerminoCargaListado(false);


		final ViewTreeObserver.OnDrawListener ol = new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                setTerminoCargaListado(true);
            }
        };
		lv.getViewTreeObserver().addOnDrawListener(ol);



	}

	
	private void mostrarDatosPersonales(){
	//	Drawable dbw = res.getDrawable(R.drawable.person_am_icon_bw);
	//	Drawable d = res.getDrawable(R.drawable.person_am_icon);
		ImageButton icon = this.findViewById(R.id.menu_icon_identification);
		TextView text = this.findViewById(R.id.label_identificacion);
		if(!mMostrarDatosPersonalesBoolean){
			this.findViewById(R.id.label_apellido).setVisibility(View.GONE);
			this.findViewById(R.id.label_nombre).setVisibility(View.GONE);
			this.findViewById(R.id.label_cumple).setVisibility(View.GONE);
								
			
			if(text != null) {
				text.setVisibility(View.GONE);
			}
			mEntryApellido.setVisibility(View.GONE);
			mEntryNombre.setVisibility(View.GONE);
			mCheckFechaNac.setVisibility(View.GONE);
//			mDateDisplay.setVisibility(View.GONE);
			mPickDate.setVisibility(View.GONE);

			icon.setBackgroundResource(R.drawable.person_am_icon_bw);
		//	icon.setBackgroundDrawable(dbw);


		}else{
			if(text != null) {
				text.setVisibility(View.VISIBLE);
			}
			sinDatos.setVisibility(View.GONE);
			this.findViewById(R.id.label_apellido).setVisibility(View.VISIBLE);
			this.findViewById(R.id.label_nombre).setVisibility(View.VISIBLE);
			this.findViewById(R.id.label_cumple).setVisibility(View.VISIBLE);
	//		this.findViewById(R.id.labelDateDisplay).setVisibility(View.VISIBLE);
			mEntryApellido.setVisibility(View.VISIBLE);
			mEntryNombre.setVisibility(View.VISIBLE);
			mCheckFechaNac.setVisibility(View.VISIBLE);
	//		mDateDisplay.setVisibility(View.VISIBLE);
			mPickDate.setVisibility(View.VISIBLE);
			mEntryApellido.setPressed(false);
			icon.setBackgroundResource(R.drawable.person_am_icon);
			//icon.setBackgroundDrawable(d);
		}
	}

	/*
	private void mostrarEmails(){
		//Drawable dbw = res.getDrawable(R.drawable.mail_am_icon_bw);
	//	Drawable d = res.getDrawable(R.drawable.mail_am_icon);
		ImageButton icon = this.findViewById(R.id.menu_icon_mail);
		TextView text = this.findViewById(R.id.label_emails);
		if(!mMostrarEmailsBoolean){
			if(text != null){
				text.setVisibility(View.GONE);
			}
			icon.setBackgroundResource(R.drawable.mail_am_icon_bw);
			mailsList.setVisibility(View.GONE);
			botonAddMail.setVisibility(View.GONE);
		}else{
			if(text != null){
				text.setVisibility(View.VISIBLE);
			}
			mailsList.setVisibility(View.VISIBLE);
			if(mailsList.getCount() == 0){
				sinDatos.setVisibility(View.VISIBLE);
			}else{
				sinDatos.setVisibility(View.GONE);
			}
			botonAddMail.setVisibility(View.VISIBLE);			
			telefonosList.setVisibility(View.GONE);
			direccionesList.setVisibility(View.GONE);
			icon.setBackgroundResource(R.drawable.mail_am_icon);
		}
	}
*/


	private void mostrarTelefonos(){
		//	Drawable dbw = res.getDrawable(R.drawable.phone_am_icon_bw);
//		Drawable d = res.getDrawable(R.drawable.phone_am_icon);
		ImageButton icon = this.findViewById(R.id.menu_icon_phone);
		TextView text = this.findViewById(R.id.label_phones);
		mEntryNuevoTipoTel.setVisibility(View.GONE);
		mEntryNuevoValTel.setVisibility(View.GONE);
		if(!mMostrarTelefonosBoolean){
			if(text != null){
				text.setVisibility(View.GONE);
			}
			icon.setBackgroundResource(R.drawable.phone_am_icon_bw);
			//	icon.setBackgroundDrawable(dbw);
			botonAddTel.setVisibility(View.GONE);
            getTelefonosList().setVisibility(View.GONE);

		}else{
			if(text != null){
				text.setVisibility(View.VISIBLE);
			}

	//		telefonosList.setAdapter(this.obtenerAdapter(telefonos, telefonosData));

			getTelefonosList().setVisibility(View.VISIBLE);
			this.resetAdapterTelefonos(null, true);
			this.configListView(getTelefonosList());

			if(getTelefonosList().getCount()== 0){
				sinDatos.setVisibility(View.VISIBLE);
			}else{
				sinDatos.setVisibility(View.GONE);
			}
			botonAddTel.setVisibility(View.VISIBLE);
			getMailsList().setVisibility(View.GONE);
			getDireccionesList().setVisibility(View.GONE);
			icon.setBackgroundResource(R.drawable.phone_am_icon);
			HashMap<Integer,String> datosEnLista = this.cargarDatosTelefonosEnLista();
			telefonosAdapter.setDatosEnLista(datosEnLista);
		}
	}



	private void mostrarEmails(){
		ImageButton icon = this.findViewById(R.id.menu_icon_mail);
		TextView text = this.findViewById(R.id.label_emails);
		mEntryNuevoTipoMail.setVisibility(View.GONE);
		mEntryNuevoValMail.setVisibility(View.GONE);
		if(!mMostrarEmailsBoolean){
			if(text != null){
				text.setVisibility(View.GONE);
			}
			icon.setBackgroundResource(R.drawable.mail_am_icon_bw);
			botonAddMail.setVisibility(View.GONE);
			getMailsList().setVisibility(View.GONE);

		}else{
			if(text != null){
				text.setVisibility(View.VISIBLE);
			}
		//	mailsList.setAdapter(this.obtenerAdapter(mails, mailsData));
			this.configListView(getMailsList());
			this.resetAdapterMails(null, true);
			getMailsList().setVisibility(View.VISIBLE);
			if(getMailsList().getCount()== 0){
				sinDatos.setVisibility(View.VISIBLE);
			}else{
				sinDatos.setVisibility(View.GONE);
			}
			botonAddMail.setVisibility(View.VISIBLE);
            getTelefonosList().setVisibility(View.GONE);
			getDireccionesList().setVisibility(View.GONE);
			icon.setBackgroundResource(R.drawable.mail_am_icon);
			HashMap<Integer,String> datosEnLista = this.cargarDatosMailsEnLista();
			mailsAdapter.setDatosEnLista(datosEnLista);

		}
	}



	private void mostrarDirecciones(){
		ImageButton icon = this.findViewById(R.id.menu_icon_addres);
		TextView text = this.findViewById(R.id.label_address);
		mEntryNuevoTipoDir.setVisibility(View.GONE);
		mEntryNuevoValDir.setVisibility(View.GONE);
		if(!mMostrarDireccionesBoolean){
			if(text != null){
				text.setVisibility(View.GONE);
			}
			icon.setBackgroundResource(R.drawable.home_am_icon_bw);
			botonAddDir.setVisibility(View.GONE);
			getDireccionesList().setVisibility(View.GONE);

		}else{
			if(text != null){
				text.setVisibility(View.VISIBLE);
			}
		//	direccionesList.setAdapter(this.obtenerAdapter(direcciones, direccionesData));
			this.configListView(getDireccionesList());
			this.resetAdapterDirecciones(null, true);
			getDireccionesList().setVisibility(View.VISIBLE);
			if(getDireccionesList().getCount()== 0){
				sinDatos.setVisibility(View.VISIBLE);
			}else{
				sinDatos.setVisibility(View.GONE);
			}
			botonAddDir.setVisibility(View.VISIBLE);
            getTelefonosList().setVisibility(View.GONE);
			getMailsList().setVisibility(View.GONE);
			icon.setBackgroundResource(R.drawable.home_am_icon);
			HashMap<Integer,String> datosEnLista = this.cargarDatosDireccionesEnLista();
			direccionesAdapter.setDatosEnLista(datosEnLista);
		}
	}
	


	private void mostrarDatosPorCategoria(){
		//Drawable dbw = res.getDrawable(R.drawable.categoria_am_icon_bw);
		//Drawable d = res.getDrawable(R.drawable.categoria_am_icon);
		ImageButton icon = this.findViewById(R.id.menu_icon_category);
		TextView text = this.findViewById(R.id.label_category);
		if(!mMostrarDatosPorCategoriaBoolean){
			this.setVisibilityParaViewPorCategorias(View.GONE);
			if(text != null){
				text.setVisibility(View.GONE);
			}
			icon.setBackgroundResource(R.drawable.categoria_am_icon_bw);
		}else{
			sinDatos.setVisibility(View.GONE);
			this.setVisibilityParaViewPorCategorias(View.VISIBLE);
			icon.setBackgroundResource(R.drawable.categoria_am_icon);
			if(text != null){
				text.setVisibility(View.VISIBLE);
			}
		}
		
		this.configurarEntriesPorCategoriaSeleccionada();
	}

	
	private void setVisibilityParaViewPorCategorias(int visibility){
		Spinner spinner = this.findViewById(R.id.spinnerCategorias_alta_persona);
		//ImageButton configCategory = this.findViewById(R.id.configCategory);
		mEntryDatoExtra.setVisibility(visibility);
		mEntryDescripcion.setVisibility(visibility);
		this.findViewById(R.id.label_datoExtra).setVisibility(visibility);
		this.findViewById(R.id.label_descripcion).setVisibility(visibility);
		configCategory.setVisibility(visibility);
		if(spinner != null){
			spinner.setVisibility(visibility);	
		}

		
	}
	
	private void configurarDatePicker(){
    /*    mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID, null);
            }
        });

*/

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		mPickDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DatePickerDialog dpd = new DatePickerDialog(AltaPersonaActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int month, int day) {
								c.set(year, month , day);
						//		String date = new SimpleDateFormat("MM/dd/yyyy").format(c.getTime());
								//mDateDisplay.setText(date);
								mYear = c.get(Calendar.YEAR);
								mMonth = c.get(Calendar.MONTH) + 1;
								mDay = c.get(Calendar.DAY_OF_MONTH);
								updateDisplay();
							}
						}, mYear, mMonth - 1, mDay);
				/*dpd.getDatePicker().setMinDate(System.currentTimeMillis());

				Calendar d = Calendar.getInstance();
				d.add(Calendar.MONTH,1);

				dpd.getDatePicker().setMaxDate(d.getTimeInMillis());*/
				dpd.show();


			}

		});





		mCheckFechaNac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checkedFechaNacimiento(isChecked);
				
			}
		});

        // get the current date
		//c = Calendar.getInstance();
        if(!(mPersonaSeleccionada == null || mPersonaSeleccionada.getFechaNacimiento() == null)){
        	this.cargarVariablesDeFechaNacimiento(mPersonaSeleccionada.getFechaNacimiento());
        }else{
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH) + 1;
			mDay = c.get(Calendar.DAY_OF_MONTH);
		}

	}
	
	private void checkedFechaNacimiento(boolean isChecked){
		if(isChecked){
			mPickDate.setEnabled(true);
			mPickDate.setTextColor(getResources().getColor(R.color.color_azul));
			//this.findViewById(R.id.label_cumple).setVisibility(View.VISIBLE);
	//		mDateDisplay.setVisibility(View.VISIBLE);
			updateDisplay();
		}else{
			mPickDate.setEnabled(false);
			mPickDate.setTextColor(Color.GRAY);
			//this.findViewById(R.id.label_cumple).setVisibility(View.);
//			mDateDisplay.setVisibility(View.INVISIBLE);
			
		}
	}
	
    private void updateDisplay() {
    	CharSequence text = new StringBuilder()
        // Month is 0 based so add 1
        .append(mDay).append(ConstantsAdmin.SEPARADOR_FECHA)
        .append(mMonth).append(ConstantsAdmin.SEPARADOR_FECHA)
        .append(mYear).append(" ");
        //mDateDisplay.setText(formatFechaToView(text.toString()));
        mPickDate.setText(formatFechaToView(text.toString()));
    }
    
 // the callback received when the user "sets" the date in the dialog
/*    private final DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear + 1 ;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
    };*/

 /*   protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth - 1, mDay);
        }
        return null;
    }
*/
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
	    int pos = 0;
	    CategoriaDTO cat;
	    boolean encontrado = false;
	    Iterator<CategoriaDTO> it = categorias.iterator();
	    while(!encontrado && it.hasNext()){
	    	pos++;
	    	cat = it.next();
	    	if(cat.getNombreReal().equals(mPersonaSeleccionada.getCategoriaNombre())){
	    		encontrado = true;
	    		mCategoriaSeleccionada = cat;
	    	}
	    }
	    this.mSpinnerAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);

	    spinner.setAdapter(this.mSpinnerAdapt); 
	    if(encontrado){
	    	spinner.setSelection(pos - 1);
	    }
	    OnItemSelectedListener spinnerListener = new seleccionSpinnerListener();
	    spinner.setOnItemSelectedListener(spinnerListener);
	}


	class seleccionSpinnerListener implements OnItemSelectedListener {


        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	mCategoriaSeleccionada = (CategoriaDTO) parent.getSelectedItem();
        	cambioCategoriaFlag = true;
        	mostrarDatosPorCategoria();
        	if(!inicioActivity){
				inicioActivity = true;
			}else{
				realzarBotonGuardar();
			}

        }

        public void onNothingSelected(AdapterView<?> parent) {


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
		/*	case ConstantsAdmin.CATEGORIA_COMPANIEROS:
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
    
    
    private void configurarEntriesPorCategoriaSeleccionada(){
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
    
   
    protected void onResume() {
        super.onResume();
        if(ConstantsAdmin.mainActivity == null){
        	
        	if(!vieneDesdeDetalle){
            	Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
            	startActivity(LaunchIntent);
        	}
        	this.finish();
        	ConstantsAdmin.cerrarMainActivity = true;
        }else{
        //	this.resetAllMyCursors();
        	this.mostrarTodo();
        }
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
}
