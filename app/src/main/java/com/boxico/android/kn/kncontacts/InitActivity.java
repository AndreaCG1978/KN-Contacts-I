package com.boxico.android.kn.kncontacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.boxico.android.kn.kncontacts.persistencia.DataBaseManager;
import com.boxico.android.kn.kncontacts.util.ConstantsAdmin;


public class InitActivity extends Activity {
	
    private boolean _active = true;
    private final int _splashTime = 1200;
    private Activity me = null;
  //  private ArrayList<Cursor> allMyCursors = null;



	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       CargaDePersonasTest test = new CargaDePersonasTest();
        try{
    //    	allMyCursors = new ArrayList<>();
        	inicializarBD();
     //   	this.resetAllMyCursors();
        	
   //     	this.cargarDatosFicticios();
        	
        	this.setContentView(R.layout.splash);
        	me = this;
        	
            Thread splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        while(_active && (waited < _splashTime)) {
                            sleep(100);
                            if(_active) {
                                waited += 100;
                            }
                        }
                    } catch(InterruptedException ignored) {
                        
                    } finally {
                        finish();
                        try {
                        	Intent i = new Intent(me, ListadoPersonaActivity.class);
                        	startActivity(i);	
						} catch (Exception e2) {
							e2.getMessage();
						}

                    }
                }
            };
            splashTread.start();
        }catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorInicioAplicacion));
		}
    }

  /* private void resetAllMyCursors(){
    	Cursor cur;
       for (Cursor allMyCursor : allMyCursors) {
           cur = allMyCursor;
           cur.close();
           this.stopManagingCursor(cur);
       }
    	allMyCursors = new ArrayList<>();
    }*/
	
 /*   private void cargarDatosFicticios(){
		CargaDePersonasTest test = new CargaDePersonasTest();
		DataBaseManager mDB = new DataBaseManager(this);
		ConstantsAdmin.inicializarBD(this);
		test.cargarDatosFicticios(ConstantsAdmin.mDBManager);
		ConstantsAdmin.finalizarBD();
}

*/



	private void inicializarBD(){

        DataBaseManager mDBManager = DataBaseManager.getInstance(this);
            // GUARDAR EL ssmDBManager en algun lugar

    	ConstantsAdmin.inicializarBD(mDBManager);
    	ConstantsAdmin.createBD(mDBManager);
    	ConstantsAdmin.actualizarTablaCategorias(this, mDBManager);
    	ConstantsAdmin.cargarCategorias(this, mDBManager);
    	ConstantsAdmin.cargarCategoriasProtegidas(this, mDBManager);
   // 	ConstantsAdmin.actualizarTablaContrasenia(mDBManager);
    	ConstantsAdmin.cargarContrasenia(this, mDBManager);
  //  	ConstantsAdmin.actualizarTablaPersona( mDBManager);
    	ConstantsAdmin.finalizarBD(mDBManager);
		
	}
/*
    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }
	*/
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }


}
