package com.boxico.android.kn.contacts;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;

import com.boxico.android.kn.contacts.util.ConstantsAdmin;


public class InitActivity extends Activity {
	
    protected boolean _active = true;
    protected int _splashTime = 1200;    
    private Activity me = null;
    private ArrayList<Cursor> allMyCursors = null;

	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       CargaDePersonasTest test = new CargaDePersonasTest();
        try{
        	allMyCursors = new ArrayList<Cursor>();
        	inicializarBD();
        	this.resetAllMyCursors();
        	
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
                    } catch(InterruptedException e) {
                        
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

   private void resetAllMyCursors(){
    	Cursor cur = null;
    	Iterator<Cursor> it = allMyCursors.iterator();
    	while(it.hasNext()){
    		cur = it.next();
    		cur.close();
    		this.stopManagingCursor(cur);
    	}
    	allMyCursors = new ArrayList<Cursor>();
    }
	
 /*   private void cargarDatosFicticios(){
		CargaDePersonasTest test = new CargaDePersonasTest();
		DataBaseManager mDB = new DataBaseManager(this);
		ConstantsAdmin.inicializarBD(this);
		test.cargarDatosFicticios(ConstantsAdmin.mDBManager);
		ConstantsAdmin.finalizarBD();
}

*/
	private void inicializarBD(){
    	ConstantsAdmin.inicializarBD(this);
    	ConstantsAdmin.createBD();
    	ConstantsAdmin.actualizarTablaCategorias(this);
    	ConstantsAdmin.cargarCategorias(this);
    	ConstantsAdmin.cargarCategoriasProtegidas(this);
    	ConstantsAdmin.actualizarTablaContrasenia();
    	ConstantsAdmin.cargarContrasenia(this);
    	ConstantsAdmin.finalizarBD();
		
	}
	
    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }
	
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }


}
