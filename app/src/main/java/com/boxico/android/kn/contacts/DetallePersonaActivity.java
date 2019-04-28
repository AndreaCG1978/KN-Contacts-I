package com.boxico.android.kn.contacts;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.TipoValorDTO;
import com.boxico.android.kn.contacts.util.Asociacion;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;
import com.boxico.android.kn.contacts.util.KNSimpleCustomDetailsAdapter;


public class DetallePersonaActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //private ArrayList<Cursor> allMyCursors = null;

  //  private BroadcastReceiver mReceiverConecta = null;

    private static final String VALOR = "VALOR";
    private static final String TIPO = "TIPO";

    private int mPersonaSeleccionadaId = -1;
    private String mPersonaSeleccionadaIdAgenda = null;
    private static DetallePersonaActivity me = null;
    private TextView mFechaNacimiento = null;
    private TextView mApellido = null;
    private TextView mNombres = null;
    private TextView mCategoria = null;
    private TextView mFechaNacimientoLabel = null;
    private TextView mTituloTelefonos = null;
    private TextView mTituloEmails = null;
    private TextView mTituloDirecciones = null;

    private ImageButton mImagenPreferido = null;
    private ImageButton importarContacto = null;
    private ImageView photo = null;
    private TextView sinDatos = null;

    //Drawable shapeLight = null;
    //Drawable shapeDark = null;

    //	private final int colorSeleccionado = Color.parseColor("#874312");
    private final int colorSeleccionado = Color.parseColor("#FFFFFF");
    //	private final int colorDeseleccionado = Color.parseColor("#41289C");
    private final int colorDeseleccionado = Color.DKGRAY;

    private boolean mMostrarTelefonos = false;
    private boolean mMostrarEmails = false;
    private boolean mMostrarDirecciones = false;
    private boolean mEsPreferido = false;

    private ListView telefonosList = null;
    private ListView mailsList = null;
    private ListView direccionesList = null;

    private List<Asociacion> telefonos = null;
    private List<Asociacion> mails = null;
    private List<Asociacion> direcciones = null;

    private Drawable iconBig = null;

    private final int PERSONAS_CURSOR = 1;
    private final int PREFERIDO_CURSOR = 2;
    private final int PERSONA_EXTRA_ID_CURSOR = 3;
    private final int PERSONA_EMAIL_CURSOR = 4;
    private final int PERSONA_PHONE_CURSOR = 5;
    private final int PERSONA_DIR_CURSOR = 6;

    private final int PERMISSIONS_CALL_PHONE = 103;
//	private final int PERMISSIONS_READ_CONTACT = 104;


    public DetallePersonaActivity() {
        super();

        // TODO Auto-generated constructor stub
    }

	public boolean ismMostrarTelefonos() {
		return mMostrarTelefonos;
	}

	public void setmMostrarTelefonos(boolean mMostrarTelefonos) {
		this.mMostrarTelefonos = mMostrarTelefonos;
	}

	public boolean ismMostrarEmails() {
		return mMostrarEmails;
	}

	public void setmMostrarEmails(boolean mMostrarEmails) {
		this.mMostrarEmails = mMostrarEmails;
	}

	public boolean ismMostrarDirecciones() {
		return mMostrarDirecciones;
	}

	public void setmMostrarDirecciones(boolean mMostrarDirecciones) {
		this.mMostrarDirecciones = mMostrarDirecciones;
	}

    /*
        private void resetAllMyCursors() {
            Cursor cur;
            for (Cursor allMyCursor : allMyCursors) {
                cur = allMyCursor;
                cur.close();
                this.stopManagingCursor(cur);
            }
            allMyCursors = new ArrayList<>();
        }
    */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
		// then it has handled the app icon touch event
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

    private void cargarLoaders() {
        this.getSupportLoaderManager().initLoader(PERSONAS_CURSOR, null, this);
        this.getSupportLoaderManager().initLoader(PREFERIDO_CURSOR, null, this);
        this.getSupportLoaderManager().initLoader(PERSONA_EXTRA_ID_CURSOR, null, this);
        this.getSupportLoaderManager().initLoader(PERSONA_EMAIL_CURSOR, null, this);
        this.getSupportLoaderManager().initLoader(PERSONA_PHONE_CURSOR, null, this);
        this.getSupportLoaderManager().initLoader(PERSONA_DIR_CURSOR, null, this);

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = this;
        //	allMyCursors = new ArrayList<>();
        Intent intent = getIntent();
        setContentView(R.layout.details_personas);
        this.registrarViews();
        mMostrarTelefonos = true;
        this.cargarLoaders();
        this.configurarBotonEditar();
        this.configurarBotonEliminar();
		this.configurarBotonEnviarInfo();
        this.configurarMostrarTelefonos();
        this.configurarMostrarMails();
        this.configurarMostrarDirecciones();
        this.configurarSeleccionarPreferido();
        this.configurarImportarContacto();
        this.configurarBorrarPhoto();
   //     this.compruebaCuandoConecta();
        this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.title_detallePersona).toUpperCase());
        mPersonaSeleccionadaId = Integer.valueOf((String) intent.getExtras().get(ConstantsAdmin.PERSONA_SELECCIONADA));
		getActionBar().setDisplayHomeAsUpEnabled(true);

        //	this.mostrarFoto();
    }
/*
    private void compruebaCuandoConecta() {
        mReceiverConecta = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case Intent.ACTION_MEDIA_MOUNTED:
                        finish();
                        break;
                    case Intent.ACTION_MEDIA_UNMOUNTED:
                        break;
                    case Intent.ACTION_MEDIA_SCANNER_STARTED:
                        break;
                    case Intent.ACTION_MEDIA_SCANNER_FINISHED:
                        break;
                    case Intent.ACTION_MEDIA_EJECT:
                        break;

                    default:
                        break;
                }
            }
        };
        String SOME_ACTION = Intent.ACTION_MEDIA_MOUNTED;
        IntentFilter intentFilter = new IntentFilter(SOME_ACTION);
        registerReceiver(mReceiverConecta, intentFilter);


    }
*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + ConstantsAdmin.phoneNumberTemp));

        if (requestCode == PERMISSIONS_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivityForResult(callIntent, ConstantsAdmin.ACTIVITY_LLAMAR_CONTACTO);
			}
		}
	}

/*	protected void onDestroy()
	{
	    this.unregisterReceiver(mReceiverConecta);
		super.onDestroy();
	}*/
/*
	@Override
	public void startManagingCursor(Cursor c) {
		allMyCursors.add(c);
		super.startManagingCursor(c);
	}
*/
	private void configurarListaTelefonos() {
		ArrayList<HashMap<String, Object>> listdata = new ArrayList<>();
		HashMap<String, Object> hm;
		Asociacion asoc;
		for (Asociacion telefono : telefonos) {
			asoc = telefono;
			hm = new HashMap<>();
			hm.put(TIPO, asoc.getKey());
			hm.put(VALOR, asoc.getValue());
			listdata.add(hm);

		}

		String[] from = {TIPO, VALOR};
		int[] to = {R.id.rowTipo, R.id.rowValor};
//		SimpleAdapter adapter = new SimpleAdapter(this, listdata, R.layout.row_item, from, to);

		KNSimpleCustomDetailsAdapter adapter = new KNSimpleCustomDetailsAdapter(this, listdata, R.layout.row_item, from, to);
		telefonosList.setAdapter(adapter);
		telefonosList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				efectuarLlamadaPos(arg2);

			}
		});


	}

	private void efectuarLlamadaPos(int pos) {
		SimpleAdapter adapt = (SimpleAdapter) telefonosList.getAdapter();
		String numero;
		HashMap<String, String> map = (HashMap<String, String>) adapt.getItem(pos);
		numero = map.get(VALOR);
		ConstantsAdmin.phoneNumberTemp = numero;
		efectuarLlamadaGenerico(numero);


	}


	private void configurarListaMails() {
		ArrayList<HashMap<String, Object>> listdata = new ArrayList<>();
		HashMap<String, Object> hm;
		Asociacion asoc;
		for (Asociacion mail : mails) {
			asoc = mail;
			hm = new HashMap<>();
			hm.put(TIPO, asoc.getKey());
			hm.put(VALOR, asoc.getValue());
			listdata.add(hm);

		}

		String[] from = {TIPO, VALOR};
		int[] to = {R.id.rowTipo, R.id.rowValor};
		KNSimpleCustomDetailsAdapter adapter = new KNSimpleCustomDetailsAdapter(this, listdata, R.layout.row_item, from, to);
		//SimpleAdapter adapter = new SimpleAdapter(this, listdata, R.layout.row_item, from, to);
		mailsList.setAdapter(adapter);
		mailsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				enviarMailPos(arg2);

			}
		});
	}

	private void enviarMailPos(int pos) {
		SimpleAdapter adapt = (SimpleAdapter) mailsList.getAdapter();
		String mail;
		HashMap<String, String> map = (HashMap<String, String>) adapt.getItem(pos);
		mail = map.get(VALOR);
		ConstantsAdmin.enviarMailGenerico(this, mail, "", "");


	}

    private void abrirMaps(int pos){
        SimpleAdapter adapt = (SimpleAdapter) direccionesList.getAdapter();
        String direccion;
        HashMap<String, String> map = (HashMap<String, String>) adapt.getItem(pos);
        direccion = map.get(VALOR);
        abrirMapsGenerico(direccion);
    }

    private void abrirMapsGenerico(String adderess){
		Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + adderess);

		//Uri gmmIntentUri = Uri.parse(String.format(Locale.ENGLISH,"geo:%f,%f", Float.valueOf(-1), Float.valueOf(-1)));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }


    }

	private void configurarListaDirecciones() {
		ArrayList<HashMap<String, Object>> listdata = new ArrayList<>();
		HashMap<String, Object> hm;
		Asociacion asoc;
		for (Asociacion direccione : direcciones) {
			asoc = direccione;
			hm = new HashMap<>();
			hm.put(TIPO, asoc.getKey());
			hm.put(VALOR, asoc.getValue());
			listdata.add(hm);

		}


		String[] from = {TIPO, VALOR};
		int[] to = {R.id.rowTipo, R.id.rowValor};
		KNSimpleCustomDetailsAdapter adapter = new KNSimpleCustomDetailsAdapter(this, listdata, R.layout.row_item, from, to);
	//	SimpleAdapter adapter = new SimpleAdapter(this, listdata, R.layout.row_item, from, to);
		direccionesList.setAdapter(adapter);
        direccionesList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                abrirMaps(arg2);

            }
        });
	}

	private void configurarMostrarTelefonos() {
		mTituloTelefonos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMostrarTelefonos = !mMostrarTelefonos;
				mostrarUOcultarTelefonos();
			}
		});

	}

	private void configurarMostrarMails() {
		mTituloEmails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMostrarEmails = !mMostrarEmails;
				mostrarUOcultarEmails();
			}
		});

	}

	private void configurarMostrarDirecciones() {
		mTituloDirecciones.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMostrarDirecciones = !mMostrarDirecciones;
				mostrarUOcultarDirecciones();
			}
		});

	}

	private void mostrarUOcultarTelefonos() {
		if (!mMostrarTelefonos) {
			mTituloTelefonos.setBackground(getResources().getDrawable(R.drawable.custom_button));
		//	mTituloTelefonos.setBackgroundDrawable(drwColorDeseleccionado);
			mTituloTelefonos.setTextColor(colorDeseleccionado);
			telefonosList.setVisibility(View.GONE);
		} else {
			telefonosList.setVisibility(View.VISIBLE);
			if (telefonos.size() == 0) {
				sinDatos.setVisibility(View.VISIBLE);
			} else {
				sinDatos.setVisibility(View.GONE);
			}
			mailsList.setVisibility(View.GONE);
			direccionesList.setVisibility(View.GONE);
			mTituloTelefonos.setTextColor(colorSeleccionado);
			mTituloEmails.setTextColor(colorDeseleccionado);
			mTituloDirecciones.setTextColor(colorDeseleccionado);
			mTituloTelefonos.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mTituloEmails.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mTituloDirecciones.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mMostrarDirecciones = false;
			mMostrarEmails = false;

		}

	}

	private void mostrarUOcultarEmails() {
		if (!mMostrarEmails) {
			mailsList.setVisibility(View.GONE);
			mTituloEmails.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mTituloEmails.setTextColor(colorDeseleccionado);
		} else {
			mailsList.setVisibility(View.VISIBLE);
			if (mails.size() == 0) {
				sinDatos.setVisibility(View.VISIBLE);
			} else {
				sinDatos.setVisibility(View.GONE);
			}
			telefonosList.setVisibility(View.GONE);
			direccionesList.setVisibility(View.GONE);
			mTituloEmails.setTextColor(colorSeleccionado);
			mTituloDirecciones.setTextColor(colorDeseleccionado);
			mTituloTelefonos.setTextColor(colorDeseleccionado);
			mTituloEmails.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mTituloDirecciones.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mTituloTelefonos.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mMostrarDirecciones = false;
			mMostrarTelefonos = false;

		}

	}

	private void mostrarUOcultarDirecciones() {
		if (!mMostrarDirecciones) {
			direccionesList.setVisibility(View.GONE);
			mTituloDirecciones.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mTituloDirecciones.setTextColor(colorDeseleccionado);
		} else {
			direccionesList.setVisibility(View.VISIBLE);
			if (direcciones.size() == 0) {
				sinDatos.setVisibility(View.VISIBLE);
			} else {
				sinDatos.setVisibility(View.GONE);
			}
			telefonosList.setVisibility(View.GONE);
			mailsList.setVisibility(View.GONE);
			mTituloDirecciones.setTextColor(colorSeleccionado);
			mTituloEmails.setTextColor(colorDeseleccionado);
			mTituloTelefonos.setTextColor(colorDeseleccionado);
			mTituloDirecciones.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mTituloEmails.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mTituloTelefonos.setBackground(getResources().getDrawable(R.drawable.custom_button));
			mMostrarEmails = false;
			mMostrarTelefonos = false;
		}

	}


	private void registrarViews() {

		mApellido = this.findViewById(R.id.detalle_apellido);
		mNombres = this.findViewById(R.id.detalle_nombres);
		mFechaNacimiento = this.findViewById(R.id.detalle_fechaNacimiento);
		mFechaNacimientoLabel = this.findViewById(R.id.label_fechaNacimiento);
		mCategoria = this.findViewById(R.id.detalle_categoria);

		mTituloTelefonos = this.findViewById(R.id.label_telefonos);
		mTituloEmails = this.findViewById(R.id.label_emails);
		mTituloDirecciones = this.findViewById(R.id.label_direcciones);
		sinDatos = this.findViewById(R.id.label_sinDatos);


		mImagenPreferido = this.findViewById(R.id.imagenPreferido);
		importarContacto = this.findViewById(R.id.importarContacto);

		photo = this.findViewById(R.id.photo);
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon);
		Drawable camara = new BitmapDrawable(getResources(), b);
		mApellido.setCompoundDrawablesWithIntrinsicBounds(null, null, camara,null);
		mApellido.setCompoundDrawablePadding(7);
		mApellido.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					//ConstantsAdmin.showFotoPopUp(iconBig, me);
					sacarPhoto();
					return true;
				}
				return true;
			}
		});

	//	Drawable drwPrefColor = res.getDrawable(R.drawable.pref_detalle_icon);
	//	Drawable drwPrefBN = res.getDrawable(R.drawable.pref_detalle_bn_icon);

	//	Drawable drwColorSeleccionado = res.getDrawable(R.drawable.custom_text_view_categoria_desc);
	//	Drawable drwColorDeseleccionado = res.getDrawable(R.drawable.custom_text_view_categoria);

		telefonosList = this.findViewById(R.id.listaTelefonos);
		mailsList = this.findViewById(R.id.listaMails);
		direccionesList = this.findViewById(R.id.listaDirecciones);

	}


	private void populateFields() {
		String temp;
		Asociacion asoc;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if (mPersonaSeleccionadaId != -1) {
			CursorLoader loaderPersona;
			CursorLoader loaderPreferido;
			Cursor perCursor;
			Cursor prefCursor;
			ConstantsAdmin.inicializarBD(mDBManager);
			loaderPersona = mDBManager.cursorLoaderPersonaPorId(mPersonaSeleccionadaId, this);
			//Cursor perCursor = mDBManager.fetchPersonaPorId(mPersonaSeleccionadaId);h
			perCursor = loaderPersona.loadInBackground();
			if (perCursor != null) {
				telefonos = new ArrayList<>();
				mails = new ArrayList<>();
				direcciones = new ArrayList<>();
			//	startManagingCursor(perCursor);
                loaderPreferido = mDBManager.cursorLoaderPreferidoPorId(mPersonaSeleccionadaId, this);
			//	Cursor prefCursor = mDBManager.fetchPreferidoPorId(mPersonaSeleccionadaId);
                prefCursor = loaderPreferido.loadInBackground();
				//startManagingCursor(prefCursor);
				mEsPreferido = prefCursor.getCount() > 0;
				prefCursor.close();
				//stopManagingCursor(prefCursor);
				String tempApe = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_APELLIDO));
				if(tempApe != null && !tempApe.equals("")){
					mApellido.setText(tempApe.toUpperCase());
				}
                String tempNom = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRES));
				if (tempNom != null && !tempNom.equals("") && tempApe != null && !tempApe.equals("")) {
					mNombres.setText(tempNom);
				}else if(tempNom == null || tempNom.equals("")){
				    mApellido.setText(tempApe.toUpperCase());
                }else if(tempApe == null || tempApe.equals("")) {
                    mApellido.setText(tempNom.toUpperCase());
                }

				/*
				temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_TEL_PARTICULAR));
				if (temp != null && !temp.equals("")) {
					asoc = new Asociacion(this.getString(R.string.hint_particular), temp);
					telefonos.add(asoc);
				}

				temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_TEL_CELULAR));
				if (temp != null && !temp.equals("")) {
					asoc = new Asociacion(this.getString(R.string.hint_telMovil), temp);
					telefonos.add(asoc);
				}


				temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_TEL_LABORAL));
				if (temp != null && !temp.equals("")) {
					asoc = new Asociacion(this.getString(R.string.hint_laboral), temp);
					telefonos.add(asoc);
				}*/

				// CARGO LOS TELEFONOS MOVILES

				List<TipoValorDTO> nuevosTV = ConstantsAdmin.obtenerTelefonosIdPersona( mPersonaSeleccionadaId, mDBManager);
				Iterator<TipoValorDTO> it = nuevosTV.iterator();
				TipoValorDTO tv;
				while (it.hasNext()) {
					tv = it.next();
					asoc = new Asociacion(tv.getTipo(), tv.getValor());
					telefonos.add(asoc);
				}

/*
				temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_EMAIL_PARTICULAR));
				if (temp != null && !temp.equals("")) {
					asoc = new Asociacion(this.getString(R.string.hint_particular), temp);
					mails.add(asoc);
				}

				temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_EMAIL_LABORAL));
				if (temp != null && !temp.equals("")) {
					asoc = new Asociacion(this.getString(R.string.hint_laboral), temp);
					mails.add(asoc);
				}

				temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_EMAIL_OTRO));
				if (temp != null && !temp.equals("")) {
					asoc = new Asociacion(this.getString(R.string.hint_otro), temp);
					mails.add(asoc);
				}
*/

				// CARGO LOS MAILS MOVILES

				nuevosTV = ConstantsAdmin.obtenerEmailsIdPersona(mPersonaSeleccionadaId, mDBManager);
				it = nuevosTV.iterator();
				while (it.hasNext()) {
					tv = it.next();
					asoc = new Asociacion(tv.getTipo(), tv.getValor());
					mails.add(asoc);
				}
/*

				temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DIRECCION_PARTICULAR));
				if (temp != null && !temp.equals("")) {
					asoc = new Asociacion(this.getString(R.string.hint_particular), temp);
					direcciones.add(asoc);
				}

				temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DIRECCION_LABORAL));
				if (temp != null && !temp.equals("")) {
					asoc = new Asociacion(this.getString(R.string.hint_laboral), temp);
					direcciones.add(asoc);
				}*/

				// CARGO LAS DIRECCIONES MOVILES

				nuevosTV = ConstantsAdmin.obtenerDireccionesIdPersona(mPersonaSeleccionadaId, mDBManager);
				it = nuevosTV.iterator();
				while (it.hasNext()) {
					tv = it.next();
					asoc = new Asociacion(tv.getTipo(), tv.getValor());
					direcciones.add(asoc);
				}


				temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_FECHA_NACIMIENTO));
				temp = formatFechaToView(temp);

				View cumple = this.findViewById(R.id.cumple);
				this.actualizarView(mFechaNacimiento, mFechaNacimientoLabel, temp);

				if(temp == null || temp.equals("")){
					cumple.setVisibility(View.GONE);
				}else{
					cumple.setVisibility(View.VISIBLE);
				}

				String tempCategoria = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO));
				String tempDatoExtra = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DATO_EXTRA));
				String tempDesc = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DESCRIPCION));

				tempCategoria = tempCategoria.toUpperCase();
				if (tempDatoExtra != null && !tempDatoExtra.equals("")) {
					tempDatoExtra = tempDatoExtra.toUpperCase();
				}

				if (tempDatoExtra != null && !tempDatoExtra.equals("") && tempDesc != null && !tempDesc.equals("")) {
					tempCategoria = tempCategoria + "(" + tempDatoExtra + "-" + tempDesc + ")" ;
				} else if (tempDatoExtra != null && !tempDatoExtra.equals("")) {
					tempCategoria = tempCategoria + "(" + tempDatoExtra + ")";
				} else if (tempDesc != null && !tempDesc.equals("")) {
					tempCategoria = tempCategoria + "(" + tempDesc + ")";

				}

				String idPerAgenda = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_ID_PERSONA_AGENDA));
				if(idPerAgenda == null || idPerAgenda.equals("")){
				    importarContacto.setVisibility(View.GONE);
                }else{
				    mPersonaSeleccionadaIdAgenda = idPerAgenda;
				    importarContacto.setVisibility(View.VISIBLE);
                }

				this.actualizarView(mCategoria, null, tempCategoria);
				this.actualizarPreferido();
				this.configurarListaTelefonos();
				this.configurarListaMails();
				this.configurarListaDirecciones();
				perCursor.close();
			//	stopManagingCursor(perCursor);
			}

			ConstantsAdmin.finalizarBD(mDBManager);
		}
	}

	private void actualizarPreferido() {
		if (mEsPreferido && mImagenPreferido != null) {
			mImagenPreferido.setBackground(getResources().getDrawable(R.drawable.pref_detalle_icon));
		} else if (mImagenPreferido != null) {
			mImagenPreferido.setBackground(getResources().getDrawable(R.drawable.pref_detalle_bn_icon));
		}
	}


	private String formatFechaToView(String text) {
		String result = null;
		if (text != null) {
			Locale locale = this.getResources().getConfiguration().locale;
			String lang = locale.getLanguage();
			String temp;
			int mes;
			String[] array = text.split(ConstantsAdmin.SEPARADOR_FECHA);
			temp = array[1];
			mes = Integer.valueOf(temp);

			if (lang.equals(Locale.ENGLISH.toString())) {
				result = textToFechaIngles(array[0], mes, array[2]);
			} else if (lang.equals(ConstantsAdmin.LANG_ESPANOL)) {
				result = textToFechaEspanol(array[0], mes, array[2]);
			} else {
				result = textToFechaGenerica(array[0], mes, array[2]);
			}

		}
		return result;
	}

	private String textToFechaGenerica(String diaText, int mesInt, String anioText) {
		return diaText + "/" + String.valueOf(mesInt) + "/" + anioText;
	}

	private String textToFechaEspanol(String diaText, int mesInt, String anioText) {
		return diaText + " de " + obtenerMes(mesInt) + " de " + anioText;
	}

	private String textToFechaIngles(String diaText, int mesInt, String anioText) {
		return obtenerMes(mesInt) + " " + diaText + ", " + anioText;
	}


	private String obtenerMes(int mesInt) {
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


	private void actualizarView(TextView viewDetalle, TextView viewLabel, String text) {
		if (text != null && !text.equals("")) {
			viewDetalle.setText(text);
			viewDetalle.setVisibility(View.VISIBLE);
			if (viewLabel != null) {
				viewLabel.setVisibility(View.VISIBLE);
			}
		} else {
			viewDetalle.setVisibility(View.GONE);
			if (viewLabel != null) {
				viewLabel.setVisibility(View.GONE);
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	//	this.resetAllMyCursors();
		switch (requestCode) {
			case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_PERSONA:
			//	this.populateFields();
				break;
			case ConstantsAdmin.ACTIVITY_EJECUTAR_SACAR_PHOTO:
				this.mostrarFoto();
				break;


			default:
				break;
		}
	}

	private void mostrarFoto() {
		try {
/*			Asociacion puedeCargar = ConstantsAdmin.comprobarSDCard(this);
			boolean puede = (Boolean) puedeCargar.getKey();*/
			photo.setVisibility(View.GONE);
		//	if (puede) {
			String path = ConstantsAdmin.obtenerPathImagen() + "." + String.valueOf(mPersonaSeleccionadaId) + ".jpg";
			if(ConstantsAdmin.existeArchivo(path)){
				Bitmap b = BitmapFactory.decodeFile(path);
				iconBig = new BitmapDrawable(getResources(), b);
				if (b != null) {
					photo.setVisibility(View.VISIBLE);
					photo.setImageBitmap(b);
				}
			}
			//}

		} catch (Exception e) {
			photo.setVisibility(View.GONE);
		}


	}


	private void efectuarLlamadaGenerico(String numero) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final String numeroTemp = numero;
		final CharSequence[] charSequence = new CharSequence[] {
				this.getString(R.string.label_llamada),
				this.getString(R.string.label_sms),
				this.getString(R.string.label_wsp)};
		builder.setTitle(this.getString(R.string.mensaje_seleccione_llamada_sms) + " " + numero)
				.setSingleChoiceItems(charSequence, -1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which) {
							case 0:
								efectuarLlamada(numeroTemp);
								break; // optional
							case 1:
								enviarSMS(numeroTemp);
								break; // optional
							case 2:
								enviarWhatsApp(numeroTemp);
								break; // optional

							default : // Optional
						}


					}
				});
		builder.create().show();
	}

	private void enviarWhatsApp(String tel) {

		try {
/*
			String smsNumber = "7****"; // E164 format without '+' sign
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("text/plain");
			sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
			sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
			sendIntent.setPackage("com.whatsapp");
			if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
				Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
				return;
			}
			startActivity(sendIntent);
*/





			PackageManager packageManager = this.getPackageManager();
			Intent i = new Intent(Intent.ACTION_VIEW);
			String url = ConstantsAdmin.url_whatsapp + "send?phone=" + tel + "&text=" + URLEncoder.encode("", "UTF-8");
			i.setPackage("com.whatsapp");
			i.setData(Uri.parse(url));
			if (i.resolveActivity(packageManager) != null) {
				this.startActivity(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enviarSMS(String tel) {



		try {
			Intent smsIntent = new Intent(Intent.ACTION_VIEW);
			smsIntent.setType("vnd.android-dir/mms-sms");
			smsIntent.putExtra("address", tel);
			smsIntent.putExtra("sms_body", "");
			startActivity(smsIntent);

		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorMandarMensaje));
		}


	}

	private void efectuarLlamada(String tel) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + tel));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling

					ActivityCompat.requestPermissions(this,
							new String[]{Manifest.permission.CALL_PHONE},
							PERMISSIONS_CALL_PHONE);
				}else{
					startActivityForResult(callIntent, ConstantsAdmin.ACTIVITY_LLAMAR_CONTACTO);
				}
			}else{
				startActivityForResult(callIntent, ConstantsAdmin.ACTIVITY_LLAMAR_CONTACTO);
			}


	    } catch (Exception e) {
	    	ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorEfectuarLlamada));
   	    }
   	}
    	
    	/*
    private void enviarMailGenerico(String mail){
    	try {
    		Intent email_intent =  new Intent(android.content.Intent.ACTION_SEND);
        	email_intent.setType("plain/text");
        	email_intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mail});
        	email_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        	email_intent.putExtra(android.content.Intent.EXTRA_TEXT, " ");
			this.startActivity(Intent.createChooser(email_intent,mail));
			
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorMandarMail));
		}
    	 	
    }*/
       
	private void configurarBotonEditar(){
		ImageButton boton = this.findViewById(R.id.buttonIrAEditarPersona);
		if(boton != null){
			boton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	abrirEditarPersona();
	            }
	        });
		}
	}
	
	private void configurarSeleccionarPreferido(){
		if(mImagenPreferido != null){
			mImagenPreferido.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	seleccionarPreferido();
	            }
	        });
		}
	}
	
	private void configurarImportarContacto(){
		if(importarContacto != null){
			importarContacto.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(me);
					builder.setMessage(getResources().getString(R.string.mensaje_pregunta_importar_contacto))
							.setCancelable(false)
							.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									importarContacto();
									resetearVista();

								}
							})
							.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
					builder.show();

	            }
	        });
		}
	}
	
	private void configurarBorrarPhoto(){
		if(photo != null){
			photo.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {

					ConstantsAdmin.showFotoPopUp(iconBig, me);
	            }
	        });
			photo.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					eliminarPhotoDialog();
					return false;
				}
			});
		}
	}

	private void importarContacto(){
	//	boolean ok = false;

        Cursor nameCur;
        CursorLoader nameCurLoader;

        String given = null;
        String family = null;
		Bitmap foto = null;

		DataBaseManager mDBManager = DataBaseManager.getInstance(this);

		PersonaDTO perTemp = ConstantsAdmin.obtenerPersonaId(mPersonaSeleccionadaId, mDBManager);

		// SE ACTUALIZAN LOS DATOS BASICOS (APELLIDO Y NOMBRE)
        nameCurLoader = ConstantsAdmin.cursorPersonaExtra;
        nameCurLoader.setSelection(ConstantsAdmin.querySelectionContactsById + mPersonaSeleccionadaIdAgenda);
        nameCurLoader.reset();
        nameCur = nameCurLoader.loadInBackground();


        if (nameCur != null) {
            if (nameCur.moveToNext()) {
                given = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                family = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
				perTemp.setApellido(family);
				perTemp.setNombres(given);
            }
            nameCur.close();

	/*		if (perTemp.getNombres() != null && (perTemp.getApellido() == null || perTemp.getApellido().equals(""))) {
				perTemp.setApellido(perTemp.getNombres());
				perTemp.setNombres(null);
			}*/
        }

		// OBTENGO LOS TELEFONOS, EMAILS Y DIRECCIONES DE LA AGENDA

		ArrayList<TipoValorDTO> nuevosTelefonos = ConstantsAdmin.importarTelDeContacto(mPersonaSeleccionadaIdAgenda, this.getResources());
		ArrayList<TipoValorDTO> nuevosMails = ConstantsAdmin.importarMailDeContacto(mPersonaSeleccionadaIdAgenda, this.getResources());
		ArrayList<TipoValorDTO> nuevasDirecciones = ConstantsAdmin.importarDirDeContacto(mPersonaSeleccionadaIdAgenda, this.getResources());

		ConstantsAdmin.inicializarBD(mDBManager);

		long idP = mDBManager.createPersona(perTemp, false);
		if (idP != -1) {
			InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(this.getContentResolver(),
					ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(mPersonaSeleccionadaIdAgenda)));
			if (inputStream != null) {
				foto = BitmapFactory.decodeStream(inputStream);
			}
			if(foto != null) {
				try {
					ConstantsAdmin.almacenarImagen(this, ConstantsAdmin.folderCSV + File.separator + ConstantsAdmin.imageFolder, "." + String.valueOf(idP) + ".jpg", foto);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ConstantsAdmin.crearTelefonos(nuevosTelefonos, idP, mDBManager);
			ConstantsAdmin.crearEmails(nuevosMails, idP, mDBManager);
			ConstantsAdmin.crearDirecciones(nuevasDirecciones, idP, mDBManager);
		//	ok = true;

		}
		ConstantsAdmin.finalizarBD(mDBManager);
	}
	
	private void eliminarPhoto(){
	/*	Asociacion asoc = ConstantsAdmin.comprobarSDCard(this);
		boolean puede = (Boolean)asoc.getKey();
		String msg = (String) asoc.getValue();*/
	//	if(puede){
	       	File file = new File(ConstantsAdmin.obtenerPathImagen() + "." + String.valueOf(mPersonaSeleccionadaId) + ".jpg");
	       	if(file.exists()){
	       		file.delete();
	       	}
	/*	}else{
			ConstantsAdmin.mostrarMensajeDialog(this, msg);
		}*/

	}

	
	private void eliminarPhotoDialog(){
		try {
			
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(getString(R.string.mensaje_borrar_foto))
	    	       .setCancelable(false)
	    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   eliminarPhoto();
	    	        	   mostrarFoto();
	    	           }
	    	       })
	    	       .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dialog.cancel();
	    	           }
	    	       });
	    	builder.show();
			
		} catch (Exception ignored) {

		}
	}
	
	private void sacarPhoto(){
	/*	Asociacion asoc = ConstantsAdmin.comprobarSDCard(this);
		boolean bool = (Boolean)asoc.getKey();
		String msg = (String) asoc.getValue();*/
	//	if(bool){
	        Intent i = new Intent(this, PhotoActivity.class);
	        i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(mPersonaSeleccionadaId));
	        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_SACAR_PHOTO);			
	/*	}else{
			ConstantsAdmin.mostrarMensajeDialog(this, msg);
		}*/


	}
	
	private void seleccionarPreferido(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(mEsPreferido && mImagenPreferido!= null){
			mEsPreferido = false;
			mImagenPreferido.setBackground(getResources().getDrawable(R.drawable.pref_detalle_bn_icon));
			try {
				ConstantsAdmin.eliminarPreferido(mPersonaSeleccionadaId, mDBManager);
			} catch (Exception e) {
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorEliminacionPreferido));
			}			
		}else if(mImagenPreferido!= null){
			mEsPreferido = true;
			mImagenPreferido.setBackground(getResources().getDrawable(R.drawable.pref_detalle_icon));
			try {
				ConstantsAdmin.crearPreferido(mPersonaSeleccionadaId, mDBManager);
			} catch (Exception e) {
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorAgregarPreferido));
			}			
			
		}
		this.actualizarPreferido();
	}
	
	private void abrirEditarPersona(){
        Intent i = new Intent(this, AltaPersonaActivity.class);
        i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(mPersonaSeleccionadaId));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_PERSONA);

	}

	private void configurarBotonEliminar(){
		ImageButton boton = this.findViewById(R.id.buttonBorrarPersona);
		if(boton != null){
			boton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	eliminarPersonaDialog();
	            }
	        });
		}
	}

	private void configurarBotonEnviarInfo(){
		ImageButton boton = this.findViewById(R.id.buttonEnviarInfo);
		if(boton != null){
			boton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					enviarInfo();
				}
			});
		}
	}

	private void enviarInfo(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		String infoContacto = ConstantsAdmin.recuperarInfoContacto(this, mPersonaSeleccionadaId, mDBManager);
		ConstantsAdmin.enviarMailGenerico(this, "", infoContacto, "");
	}

	private void eliminarPersonaDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(R.string.mensaje_borrar_persona)
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   eliminarPersonaSeleccionada();
    	        	   ConstantsAdmin.resetPersonasOrganizadas();
    	        	   finish();
    	           }
    	       })
    	       .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	builder.show();
	}
	
	private void eliminarPersonaSeleccionada(){
		try {
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			ConstantsAdmin.eliminarPersona(mPersonaSeleccionadaId, mDBManager);

		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorEliminacionContacto));
		}

	}
	
    private void limpiarDatos(){
    	mApellido.setText("");
    	mFechaNacimiento.setText("");
    	mNombres.setText("");
    }
     
    protected void onResume() {
        super.onResume();
        if(ConstantsAdmin.mainActivity == null){
        	Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        	startActivity(LaunchIntent);
        	this.finish();
        	ConstantsAdmin.cerrarMainActivity = true;
        }else{
	     //   this.resetAllMyCursors();
            this.resetearVista();
        }
    }

    private void resetearVista(){
        this.limpiarDatos();
        this.populateFields();
        if(!mMostrarDirecciones && !mMostrarEmails && !mMostrarTelefonos){
            mMostrarDirecciones = false;
            mMostrarEmails = false;
            mMostrarTelefonos = true;
        }
        this.mostrarUOcultarDirecciones();
        this.mostrarUOcultarEmails();
        this.mostrarUOcultarTelefonos();
        this.mostrarFoto();
    }


	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        //   ConstantsAdmin.inicializarBD( mDBManager);
        CursorLoader cl = null;
        boolean noActivaContrasenia = ConstantsAdmin.contrasenia != null && !ConstantsAdmin.contrasenia.isActiva();
        switch(id) {
            case PERSONAS_CURSOR:
                cl = mDBManager.cursorLoaderPersonaPorId(0, this);
                //ConstantsAdmin.cursorCategorias = cl;
                break; // optional
            case PREFERIDO_CURSOR:
                cl = mDBManager.cursorLoaderPreferidos(noActivaContrasenia, ConstantsAdmin.categoriasProtegidas, this);
                //ConstantsAdmin.cursorCategoriasPersonales = cl;
                break; // optional
            case PERSONA_EXTRA_ID_CURSOR:
                cl = mDBManager.cursorLoaderPersonaExtraId("0", this, getContentResolver());
                ConstantsAdmin.cursorPersonaExtra = cl;
                break;
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


            default : // Optional
                // Statements
        }

        return cl;
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {

	}
}
