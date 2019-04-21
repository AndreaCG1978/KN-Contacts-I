package com.boxico.android.kn.contacts;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;

import com.boxico.android.kn.contacts.util.KNCategoryProtectionAdapter;
import com.boxico.android.kn.contacts.util.KNMail;

public class ProteccionCategoriaActivity extends ListActivity {

	private Button botonRegistrarContrasenia = null;
	private ImageButton botonReenviarPass = null;
	private EditText contrasenia1 = null;
	private EditText contrasenia2 = null;
	private EditText mailPassword = null;
	private TextView labelCategorias = null;
	private Button botonActivarDesactivarPass = null;
	//	private ArrayList<Cursor> allMyCursors = null;
	private ProteccionCategoriaActivity me = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//	allMyCursors = new ArrayList<>();
		me = this;
		this.setContentView(R.layout.proteccion_categorias);
		this.inicializarList();
		this.inicializarCategoriasProtegidas();
		this.registrarWidgets();
		this.configurarBotonRegistrarContrasenia();
		this.configurarBotonReenviarPass();
		this.configurarBotonActivarDesactivarPass();
		//	this.configurarBotonDesactivarContrasenia();
		this.habilitarCampos();
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.title_proteccion_categorias));

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

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch (requestCode) {
			case ConstantsAdmin.ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA:
				this.habilitarCampos();
				break;
			default:

				break;
		}


	}

	protected void onResume() {
		super.onResume();
		if(ConstantsAdmin.mainActivity == null){
			Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
			startActivity(LaunchIntent);
			this.finish();
			ConstantsAdmin.cerrarMainActivity = true;
		}

	}

	private void habilitarCampos(){
		if(ConstantsAdmin.contrasenia.isActiva()){
			contrasenia1.setTextColor(Color.BLACK);
			contrasenia2.setTextColor(Color.BLACK);
			mailPassword.setTextColor(Color.BLACK);

			contrasenia1.setEnabled(true);
			contrasenia2.setEnabled(true);
			mailPassword.setEnabled(true);
			//	botonRegistrarContrasenia.setEnabled(true);
			botonRegistrarContrasenia.setVisibility(View.VISIBLE);
			botonReenviarPass.setVisibility(View.GONE);
			botonRegistrarContrasenia.setTextColor(Color.WHITE);
			botonActivarDesactivarPass.setText(R.string.label_bloquear_contrasenia);

		}else{
			contrasenia1.setTextColor(Color.GRAY);
			contrasenia2.setTextColor(Color.GRAY);
			mailPassword.setTextColor(Color.GRAY);

			contrasenia1.setEnabled(false);
			contrasenia2.setEnabled(false);
			mailPassword.setEnabled(false);

			botonRegistrarContrasenia.setVisibility(View.GONE);
			botonReenviarPass.setVisibility(View.VISIBLE);
			botonRegistrarContrasenia.setTextColor(Color.LTGRAY);
			botonActivarDesactivarPass.setText(R.string.label_desbloquear_contrasenia);
		}

		this.findViewById(R.id.leyendaNoSeRegistraPass).setVisibility(View.GONE);
		botonActivarDesactivarPass.setVisibility(View.VISIBLE);
		botonRegistrarContrasenia.setText(R.string.label_actualizar);
		// NO ESTA REGISTRADA EN LA BASE LA CONTRASEÑA
		if(ConstantsAdmin.contrasenia.getId() == -1){
			this.getListView().setVisibility(View.GONE);
			labelCategorias.setVisibility(View.GONE);
			this.findViewById(R.id.leyendaNoSeRegistraPass).setVisibility(View.VISIBLE);
			botonActivarDesactivarPass.setVisibility(View.GONE);
			botonRegistrarContrasenia.setText(R.string.label_registrar_contrasenia);

		}else if(ConstantsAdmin.contrasenia.isActiva()){// ESTA REGISTRADA EN LA BD LA CONTRASEÑA, Y ESTA EN MODO ON
			this.getListView().setVisibility(View.VISIBLE);
			labelCategorias.setVisibility(View.VISIBLE);
			this.mostrarCantidadCategoriasProtegidas();
			this.getListView().setEnabled(true);

		}else{// ESTA REGISTRADA LA CONTRASEÑA, Y ESTA EN MODO OFF
			this.getListView().setVisibility(View.INVISIBLE);

			labelCategorias.setVisibility(View.GONE);

		}

	}

	private void mostrarCantidadCategoriasProtegidas(){
		labelCategorias.setText( this.getString(R.string.label_cantidad_categorias_protegidas) + " (" + ConstantsAdmin.categoriasProtegidas.size() + "/" + this.getListAdapter().getCount() + ")");
	}

	private void registrarWidgets(){
		contrasenia1 = this.findViewById(R.id.contrasenia1);
		contrasenia2 = this.findViewById(R.id.contrasenia2);
		mailPassword = this.findViewById(R.id.mailPassword);
		if(ConstantsAdmin.contrasenia.getContrasenia()!= null && !ConstantsAdmin.contrasenia.getContrasenia().equals("")){
			contrasenia1.setText(ConstantsAdmin.contrasenia.getContrasenia());
			contrasenia2.setText(ConstantsAdmin.contrasenia.getContrasenia());
			mailPassword.setText(ConstantsAdmin.contrasenia.getMail());
		}
		botonRegistrarContrasenia = this.findViewById(R.id.botonRegistrarContrasenia);
		botonActivarDesactivarPass = this.findViewById(R.id.botonActivarDesactivarPass);
		botonReenviarPass = this.findViewById(R.id.botonReenviarPass);
/*
		botonActivarContrasenia = this.findViewById(R.id.botonActivarContrasenia);
		botonDesactivarContrasenia = this.findViewById(R.id.botonDesactivarContrasenia);
		imagen = this.findViewById(R.id.imagenProtegerCategorias);*/


	}

	private void configurarBotonRegistrarContrasenia(){
		botonRegistrarContrasenia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				registrarContrasenia();
			}
		});

	}

	private void configurarBotonReenviarPass(){
		botonReenviarPass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(me);
				builder.setMessage(me.getString(R.string.mensaje_enviar_contrasenia) + ConstantsAdmin.contrasenia.getMail())
						.setCancelable(true)
						.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Long[] params = new Long[1];
								params[0] = 1L;
								new SendMail().execute(params);


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

	private class SendMail extends AsyncTask<Long, Integer, Integer> {

        boolean okSend = false;

		@Override
		protected Integer doInBackground(Long... longs) {
			okSend = reenviarContrasenia();
			return null;
		}

		protected void onPreExecute() {
			//called before doInBackground() is started
		}

		@Override
		protected void onPostExecute(Integer integer) {
			super.onPostExecute(integer);
			if(okSend) {
                ConstantsAdmin.mostrarMensaje(me, me.getString(R.string.mensaje_exito_almacenar_password));
            }else{
                ConstantsAdmin.mostrarMensaje(me,me.getString(R.string.errorMandarMail));
            }
		}
	}



	private void configurarBotonActivarDesactivarPass(){
		botonActivarDesactivarPass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ConstantsAdmin.contrasenia.isActiva()){
					if(ConstantsAdmin.contrasenia.getId() != -1){
						DataBaseManager mDBManager = DataBaseManager.getInstance(me);
						ConstantsAdmin.contrasenia.setActiva(false);
						ConstantsAdmin.actualizarContrasenia(ConstantsAdmin.contrasenia, mDBManager);
						ConstantsAdmin.resetPersonasOrganizadas();
						habilitarCampos();
						contrasenia1.setText(ConstantsAdmin.contrasenia.getContrasenia());
						contrasenia2.setText(ConstantsAdmin.contrasenia.getContrasenia());
						ConstantsAdmin.mostrarMensaje(me,  me.getString(R.string.mensaje_activacion_proteccion));
					}else{
						ConstantsAdmin.mostrarMensaje(me, me.getString(R.string.mensaje_debe_registrar_contrasenia));
					}
				}else{
					openVerActivarContrasenia();
				}

			}
		});

	}

    /*
    private void configurarBotonDesactivarContrasenia(){
    	botonDesactivarContrasenia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ConstantsAdmin.contrasenia.getId() != -1){
					DataBaseManager mDBManager = DataBaseManager.getInstance(me);
					ConstantsAdmin.contrasenia.setActiva(false);
					ConstantsAdmin.actualizarContrasenia(ConstantsAdmin.contrasenia, mDBManager);
					ConstantsAdmin.resetPersonasOrganizadas();
					habilitarCampos();
					contrasenia1.setText(ConstantsAdmin.contrasenia.getContrasenia());
					contrasenia2.setText(ConstantsAdmin.contrasenia.getContrasenia());
					ConstantsAdmin.mostrarMensaje(me,  me.getString(R.string.mensaje_activacion_proteccion));
				}else{
					ConstantsAdmin.mostrarMensaje(me, me.getString(R.string.mensaje_debe_registrar_contrasenia));
				}
			}
		});

    }*/

    /*
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        this.activarODesactivarCategoria(l, position, v);

    }*/

    /*
    private void activarODesactivarCategoria(ListView list, int position, View v){
    	CategoriaDTO catSelected = (CategoriaDTO) list.getItemAtPosition(position);
		LinearLayout ll = (LinearLayout)v;
		TextView tv = (TextView) ll.getChildAt(0);
    	if(this.estaRegistradaCategoria(catSelected)){
    		this.eliminarCategoriaProtegida(catSelected);
    		tv.setTextColor(getResources().getColor(R.color.color_azul));
    	}else{
    		this.agregarCategoriaProtegida(catSelected);
    		tv.setTextColor(getResources().getColor(R.color.color_gris_oscuro));
    	}

    	this.mostrarCantidadCategoriasProtegidas();
    }
*/

	public void activarODesactivarCategoria(CategoriaDTO catSelected){
		if(this.estaRegistradaCategoria(catSelected)){
			this.eliminarCategoriaProtegida(catSelected);
		}else{
			this.agregarCategoriaProtegida(catSelected);
		}

		this.mostrarCantidadCategoriasProtegidas();
	}

	private boolean estaRegistradaCategoria(CategoriaDTO catSelected){
		boolean result = false;
		CategoriaDTO cat;
		Iterator<CategoriaDTO> it = ConstantsAdmin.categoriasProtegidas.iterator();
		while(!result && it.hasNext()){
			cat = it.next();
			result = cat.getNombreReal().equals(catSelected.getNombreReal());
		}
		return result;

	}

	private void eliminarCategoriaProtegida(CategoriaDTO catSelected){
		Iterator<CategoriaDTO> it = ConstantsAdmin.categoriasProtegidas.iterator();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		CategoriaDTO cat;
		boolean encontrada = false;
		ConstantsAdmin.eliminarCategoriaProtegida(catSelected, mDBManager);
		while(it.hasNext() && !encontrada){
			cat = it.next();
			if(cat.getNombreReal().equals(catSelected.getNombreReal())){
				it.remove();
				encontrada = true;
			}
		}
	}

	private void agregarCategoriaProtegida(CategoriaDTO catSelected){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.categoriasProtegidas.add(catSelected);
		ConstantsAdmin.crearCategoriaProtegida(catSelected, mDBManager);
	}



	private void openVerActivarContrasenia() {
		Intent i = new Intent(this, ActivarContraseniaActivity.class);
		this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA);
	}

	private void cargarDatosContrasenia(String pass, String mail){
		long id;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.contrasenia.setActiva(true);
		ConstantsAdmin.contrasenia.setContrasenia(pass);
		ConstantsAdmin.contrasenia.setMail(mail);
		id = ConstantsAdmin.crearContrasenia(ConstantsAdmin.contrasenia, mDBManager);
		ConstantsAdmin.contrasenia.setId(id);
		ConstantsAdmin.resetPersonasOrganizadas();

		this.habilitarCampos();
	}



	private boolean reenviarContrasenia(){
	    boolean okSend = false;
		KNMail m = new KNMail(ConstantsAdmin.kn_mail, ConstantsAdmin.kn_mail_pass);

		String[] toArr = {ConstantsAdmin.contrasenia.getMail()};
		m.setTo(toArr);
		m.setFrom(ConstantsAdmin.kn_mail);
		m.setSubject(this.getString(R.string.app_name) + " - " + this.getString(R.string.title_proteccion_categorias));
		m.setBody(this.getString(R.string.label_contrasenia) + ": " + ConstantsAdmin.contrasenia.getContrasenia());

		try {
            okSend = m.send();
		} catch(Exception e) {
			//Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
			ConstantsAdmin.mostrarMensaje(me, me.getString(R.string.errorMandarMail));
		}
		return okSend;

	}

	private void registrarContrasenia(){
		String c1 = contrasenia1.getText().toString();
		String c2 = contrasenia2.getText().toString();
		String c3 = mailPassword.getText().toString();
		String mensaje;
		if(c1.trim().equals("")){
			mensaje = this.getString(R.string.mensaje_complete_contrasenia);
		}else if(c2.trim().equals("")){
			mensaje = this.getString(R.string.mensaje_complete_repeticion_contrasenia);
		}else if(!c1.equals(c2)){
			mensaje = this.getString(R.string.mensaje_contrasenia_no_coinciden);
		}else if(c3.trim().equals("")){
			mensaje =  this.getString(R.string.mensaje_debe_ingresar_mail);
		}else{
			mensaje =  this.getString(R.string.mensaje_contrasenia_registrada_ok);
			cargarDatosContrasenia(c1, c3);
			try {
				//	ConstantsAdmin.almacenarContraseniaEnArchivo(this);
				ConstantsAdmin.enviarMailContraseniaCategoriasProtegidas(this);
			} catch (Exception e) {
				ConstantsAdmin.mostrarMensajeDialog(this, e.getMessage());
			}
			mailPassword.clearFocus();



		}
		ConstantsAdmin.mostrarMensaje(this, mensaje);
	}

	private void inicializarList(){
		this.getListView().setItemsCanFocus(false);
		this.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		this.getListView().setFastScrollEnabled(true);
		labelCategorias = this.findViewById(R.id.tituloCategorias);
	}

	private void cambiarNombreCategorias(List<CategoriaDTO> categorias){
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
	}


	private void inicializarCategoriasProtegidas(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		List<CategoriaDTO> categoriasActivas = ConstantsAdmin.obtenerCategoriasActivas(this, null, mDBManager);
		List<CategoriaDTO> categoriasPersonalesActivas = ConstantsAdmin.obtenerCategoriasActivasPersonales(this, mDBManager);
		categoriasActivas.addAll(categoriasPersonalesActivas);
		this.cambiarNombreCategorias(categoriasActivas);
		Collections.sort(categoriasActivas);

//        setListAdapter(new KNCategoryListAdapter(this,R.layout.categoria_row, categoriasActivas));
		setListAdapter(new KNCategoryProtectionAdapter(this, R.layout.categoria_row, R.id.text1, categoriasActivas, true));


	}


}
