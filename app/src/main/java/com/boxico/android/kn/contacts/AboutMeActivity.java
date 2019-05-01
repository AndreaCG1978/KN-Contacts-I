package com.boxico.android.kn.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.boxico.android.kn.contacts.util.ConstantsAdmin;


public class AboutMeActivity extends Activity {
	
	private  TextView mail = null;
	private  TextView politicaPrivacidad = null;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       	this.setContentView(R.layout.acerca_de);
        this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.title_acerca_de));
       	this.configurarMailYLinkPagEmpresa();
		getActionBar().setDisplayHomeAsUpEnabled(true);
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

    protected void onResume() {
        super.onResume();
        if(ConstantsAdmin.mainActivity == null){
        	Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        	startActivity(LaunchIntent);
        	this.finish();
        	ConstantsAdmin.cerrarMainActivity = true;
        }
    }
    
    private void configurarMailYLinkPagEmpresa(){
		final AboutMeActivity me = this;
    	mail = this.findViewById(R.id.textAcercaDeMail);
    	mail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enviarMailGenerico(mail.getText().toString());
			}
		});
    	politicaPrivacidad = this.findViewById(R.id.textPoliticaPrivacidad);
    	politicaPrivacidad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(me);
				builder.setMessage(R.string.mensaje_politica_privacidad)
						.setCancelable(true)
						.setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				builder.show();
			}
		});



    	
    }
    
    
// --Commented out by Inspection START (10/4/2019 08:41):
//    private void abrirNavegadorConLink(){
//    	try {
//    		Intent intent = new Intent(Intent.ACTION_VIEW);
//    		intent.setData(Uri.parse("market://search?q=pub:" + getString(R.string.text_acerca_de_empresa)));
//    		startActivity(intent);
//
//		} catch (Exception e) {
//			ConstantsAdmin.mostrarMensaje(this, getString(R.string.error_exportar_csv));
//		}
//
//    }
// --Commented out by Inspection STOP (10/4/2019 08:41)

    private void enviarMailGenerico(String text){
    	try {
    		Intent email_intent =  new Intent(android.content.Intent.ACTION_SEND);
        	email_intent.setType("plain/text");
        	email_intent.putExtra(android.content.Intent.EXTRA_EMAIL, new
        	String[]{text});
        	email_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        	email_intent.putExtra(android.content.Intent.EXTRA_TEXT, " ");
        	this.startActivity(Intent.createChooser(email_intent,""));   
			
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorMandarMail));
		}
    	 	
    }


}
