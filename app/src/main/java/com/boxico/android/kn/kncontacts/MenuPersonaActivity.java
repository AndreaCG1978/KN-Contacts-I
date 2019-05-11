package com.boxico.android.kn.kncontacts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;

import com.boxico.android.kn.kncontacts.persistencia.DataBaseManager;
import com.boxico.android.kn.kncontacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.kncontacts.util.ConstantsAdmin;



public class MenuPersonaActivity extends Activity {
	
	private Dialog dialog = null;
	private int mPersonaSeleccionadaId = -1;
	private boolean mEsPreferido = false;
	private ImageButton mImagenPreferido = null;
	private final int PERMISSIONS_READ_CONTACTS = 101;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        mPersonaSeleccionadaId = Integer.valueOf((String)getIntent().getExtras().get(ConstantsAdmin.PERSONA_SELECCIONADA));
        mEsPreferido = (Boolean)getIntent().getExtras().get(ConstantsAdmin.KEY_MUESTRA_PREFERIDOS);
        this.configurarDialog();
        this.registrarWidgets(dialog);
        dialog.show();

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {

		if (requestCode == PERMISSIONS_READ_CONTACTS) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				this.openVerDetallePersonaPrivado();
			}
		}
	}



	private void configurarDialog(){
		dialog = new Dialog(this);
        dialog.setContentView(R.layout.menu_persona);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new Dialog.OnCancelListener() {
        	public void onCancel(DialogInterface dialog) {
        		finish();
        	}
        });
	}
	
	
	private void registrarWidgets(Dialog dialog){
		PersonaDTO per;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		per = ConstantsAdmin.obtenerPersonaId(mPersonaSeleccionadaId, mDBManager);
		String nombre = "";
		if(per.getApellido() != null && (per.getNombres() == null || per.getNombres().equals(""))){
			nombre = per.getApellido();
		}else if(per.getApellido()!= null && !per.getApellido().equals("") && per.getNombres() != null && !per.getNombres().equals("")){
    		nombre = per.getApellido() + ", " + per.getNombres();
    	}else{
			nombre = per.getNombres();
		}
		dialog.setTitle(nombre);
		ImageButton btn = dialog.findViewById(R.id.buttonVerPersona);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
				askForReadContactsPermission();
            }
        });
		btn = dialog.findViewById(R.id.buttonEditarPersona);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	abrirEditarPersona();
            }
        });
		btn = dialog.findViewById(R.id.buttonBorrarPersona);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	eliminarPersonaDialog();
            }
        });
		btn = dialog.findViewById(R.id.buttonEnviarInfoPersona);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	sendInfoByMail();
            }
        });
		mImagenPreferido = dialog.findViewById(R.id.buttonPreferido);
		mImagenPreferido.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setearPreferido();

			}
		});
		if(mEsPreferido){
			mImagenPreferido.setBackground(getResources().getDrawable(R.drawable.pref_icon));
		}else{
			mImagenPreferido.setBackground(getResources().getDrawable(R.drawable.pref_icon_bw));
		}
	}

	
    private void setearPreferido(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(mEsPreferido && mImagenPreferido!= null){
			mEsPreferido = false;
			mImagenPreferido.setBackground(getResources().getDrawable(R.drawable.pref_icon_bw));
			try {
				ConstantsAdmin.eliminarPreferido(mPersonaSeleccionadaId, mDBManager);
			} catch (Exception e) {
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorEliminacionPreferido));
			}
		}else if(mImagenPreferido!= null){
			mEsPreferido = true;
			mImagenPreferido.setBackground(getResources().getDrawable(R.drawable.pref_icon));
			try {
				ConstantsAdmin.crearPreferido(mPersonaSeleccionadaId, mDBManager);
			} catch (Exception e) {
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorAgregarPreferido));
			}

		}
		dialog.cancel();

	}

	private void sendInfoByMail(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	String infoContacto = ConstantsAdmin.recuperarInfoContacto(this, mPersonaSeleccionadaId, mDBManager);
    	ConstantsAdmin.enviarMailGenerico(this, "", infoContacto, "");
    	dialog.cancel();
    	this.finish();
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
	
	private void abrirEditarPersona(){
        Intent i = new Intent(this, AltaPersonaActivity.class);
        i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(mPersonaSeleccionadaId));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_PERSONA);
    	dialog.cancel();
    	this.finish();
	}




	private void askForReadContactsPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
					!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_CONTACTS},
						PERMISSIONS_READ_CONTACTS);


			} else {//Ya tiene el permiso...
				this.openVerDetallePersonaPrivado();
			}
		} else {
			this.openVerDetallePersonaPrivado();
		}


	}

    private void openVerDetallePersonaPrivado(){
        Intent i = new Intent(this, DetallePersonaActivity.class);
        i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(mPersonaSeleccionadaId));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_DETALLE_PERSONA);
    	dialog.cancel();
    	this.finish();
    }


}
