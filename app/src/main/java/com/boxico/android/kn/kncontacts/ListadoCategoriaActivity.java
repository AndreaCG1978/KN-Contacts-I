package com.boxico.android.kn.kncontacts;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boxico.android.kn.kncontacts.persistencia.DataBaseManager;
import com.boxico.android.kn.kncontacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.kncontacts.util.ConstantsAdmin;
import com.boxico.android.kn.kncontacts.util.KNCategoryListAdapter;
import com.boxico.android.kn.kncontacts.util.KNListFragment;

public class ListadoCategoriaActivity extends KNListFragment  {


	private TextView labelCategorias = null;
	private TextView labelNombreCategoria = null;
	private TextView labelTipoDatoExtra = null;
	private EditText entryNombreCategoria = null;
	private EditText entryTipoDatoExtra = null;
	private ImageButton addCategoria = null;
	private Button saveCategoria = null;
	private Button cancelSaveCategory = null;
	private int cantActivas = 0;
	private int cantCategorias = 0;
	private String titulo = null;
	private CategoriaDTO categoriaSeleccionada = null;

	public CategoriaDTO getCategoriaSeleccionada() {
		return categoriaSeleccionada;
	}

	public void setCategoriaSeleccionada(CategoriaDTO categoriaSeleccionada) {
		this.categoriaSeleccionada = categoriaSeleccionada;
	}

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  allMyCursors = new ArrayList<>();
        this.setContentView(R.layout.list_categorias);
        this.registrarWidgets();
        //this.configurarList(getListView());
		this.refreshList();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.label_categorias));
        
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

	private void registrarWidgets(){
		labelCategorias = this.findViewById(R.id.categoriaTextView);
		titulo = labelCategorias.getText().toString();
		labelNombreCategoria = this.findViewById(R.id.labelNombreCategoria);
		labelTipoDatoExtra = this.findViewById(R.id.label_tipo_dato_extra);
		entryNombreCategoria = this.findViewById(R.id.entryNombreCategoria);
		entryTipoDatoExtra = this.findViewById(R.id.entryTipoDatoExtra);
		addCategoria = this.findViewById(R.id.addCategoria);
		addCategoria.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				categoriaSeleccionada = null;
				openAltaCategoria();
			}
		});
		saveCategoria = this.findViewById(R.id.saveCategoria);
		saveCategoria.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boolean guardoExitoso = salvarCategoria();
				if(guardoExitoso) {
					refreshList();
					openAltaCategoria();
				}
			}
		});
		cancelSaveCategory = this.findViewById(R.id.cancelSaveCategory);
		cancelSaveCategory.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openAltaCategoria();
			}
		});

	}

	public void refreshList(){
		configurarList(getListView());
	}

	private boolean validarNuevaCategoria(){
		boolean estaOk;
		String nomCatStr = entryNombreCategoria.getText().toString();
		String nomDatoRelStr = entryTipoDatoExtra.getText().toString();
		estaOk = !nomCatStr.equals("") && !nomDatoRelStr.equals("");
		return estaOk;
	}

	private boolean salvarCategoria(){
		String oldNameCat = "-";
		boolean reset = false;
		boolean guardoExitoso = false;
		if(validarNuevaCategoria()){
			if(categoriaSeleccionada == null){
				categoriaSeleccionada = new CategoriaDTO();
			}else{
				oldNameCat = categoriaSeleccionada.getNombreReal();
				reset = true;
			}

			categoriaSeleccionada.setNombreReal(entryNombreCategoria.getText().toString());
			categoriaSeleccionada.setTipoDatoExtra(entryTipoDatoExtra.getText().toString());
			categoriaSeleccionada.setActiva(1);
			categoriaSeleccionada.setCategoriaPersonal(true);
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			ConstantsAdmin.crearCategoriaPersonal(categoriaSeleccionada, oldNameCat, false, mDBManager);
			guardoExitoso = true;
			if(reset){
				ConstantsAdmin.resetPersonasOrganizadas();
			}
			categoriaSeleccionada = null;
		}else{
			Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_categoria_incompleta, Toast.LENGTH_LONG);
			t.show();
			//   		mEntryApellido.clearFocus();

		}
		return guardoExitoso;

	}

	public void openAltaCategoria(){
	//	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if(entryNombreCategoria.getVisibility() == View.VISIBLE){
			this.habilitarLista(true);
			entryTipoDatoExtra.setVisibility(View.GONE);
			entryNombreCategoria.setVisibility(View.GONE);
			labelNombreCategoria.setVisibility(View.GONE);
			labelTipoDatoExtra.setVisibility(View.GONE);
			saveCategoria.setVisibility(View.GONE);
			cancelSaveCategory.setVisibility(View.GONE);
			addCategoria.setVisibility(View.VISIBLE);
			entryTipoDatoExtra.setText("");
			entryNombreCategoria.setText("");
		//	addCategoria.setTextColor(Color.WHITE);
			//opacarBotonGuardar();
			ConstantsAdmin.hideKeyboard(this);
			//imm.hideSoftInputFromWindow(entryNombreCategoria.getWindowToken(), 0);

		}else {
			this.habilitarLista(false);
			entryTipoDatoExtra.setVisibility(View.VISIBLE);
			entryNombreCategoria.setVisibility(View.VISIBLE);
			labelNombreCategoria.setVisibility(View.VISIBLE);
			labelTipoDatoExtra.setVisibility(View.VISIBLE);
			//	realzarBotonGuardar();
			if(categoriaSeleccionada != null){
				entryNombreCategoria.setText(categoriaSeleccionada.getNombreReal());
				entryTipoDatoExtra.setText(categoriaSeleccionada.getTipoDatoExtra());
			}else{
				entryNombreCategoria.setText(getResources().getText(R.string.hint_categoria));
				entryNombreCategoria.setSelectAllOnFocus(true);
			}

			entryNombreCategoria.requestFocus();
			saveCategoria.setVisibility(View.VISIBLE);
			cancelSaveCategory.setVisibility(View.VISIBLE);
            addCategoria.setVisibility(View.GONE);
		//	addCategoria.setTextColor(Color.DKGRAY);
			ConstantsAdmin.showKeyboard(this, entryNombreCategoria);
			//imm.showSoftInput(entryNombreCategoria, InputMethodManager.SHOW_IMPLICIT);


		}

	}
/*

	public View getViewByPosition(int position, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
        boolean val = position < firstListItemPosition || position > lastListItemPosition;
        if (val) {
			return listView.getAdapter().getView(position, null, listView);
		} else {
			final int childIndex = position - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

*/

	private void habilitarLista(boolean b){
	/*	LinearLayout ly1, ly2 = null;
		CheckBox chk = null;
		Button btn = null;
		TextView txt = null;*/

		if(b){
			this.getListView().setVisibility(View.VISIBLE);
		}else{
			this.getListView().setVisibility(View.GONE);
		}



	/*
		int size = getListView().getAdapter().getCount();
		for (int j = 0; j < size; j++) {
		//	ly1 = (LinearLayout)getListView().getChildAt(j);


			ly1 = (LinearLayout) this.getViewByPosition(j, getListView());
			txt = (TextView) ly1.findViewById(R.id.text1);
			txt.setEnabled(b);
			ly2 = (LinearLayout)ly1.findViewById(R.id.linearDatos);
			chk = ly2.findViewById(R.id.checkActivada);
			chk.setEnabled(b);
			//eTemp.setTextColor(color);
			btn = ly2.findViewById(R.id.removeButton);
			btn.setEnabled(b);
		}

		*/

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
	
    
    private void configurarList(ListView listView){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setFastScrollEnabled(true);

        List<CategoriaDTO> categorias = ConstantsAdmin.obtenerCategorias(this, null, mDBManager);
		List<CategoriaDTO> categoriasPersonales = ConstantsAdmin.obtenerCategoriasPersonales(this, mDBManager);
		this.cambiarNombreCategorias(categorias);
		this.cambiarNombreCategorias(categoriasPersonales);
		categorias.addAll(categoriasPersonales);
		Collections.sort(categorias);
        setListAdapter(new KNCategoryListAdapter(this, R.layout.categoria_row, R.id.text1, categorias, false));
        CategoriaDTO cat;
        int pos = 0;
        cantActivas = 0;
        cantCategorias = categorias.size();
        while(pos < cantCategorias){
        	cat = (CategoriaDTO) listView.getItemAtPosition(pos);
        	if(cat.getActiva() == 1){
        		cantActivas++;
        	}
        	pos++;
        }
        labelCategorias.setText(titulo + " (" + cantActivas + "/" + cantCategorias + ")");

      //  this.configurarBotonMisCategorias();
		
	}
	/*
	private void configurarBotonMisCategorias(){
		Button misCat;
		misCat = this.findViewById(R.id.botonMisCategorias);
		misCat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	verMisCategorias();
            }
        });
	}
	*/


	/*
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        this.activarODesactivarCategoria(l, position, v);
        //this.configurarList(l);
    }
    */
/*    private long cantidadCategoriasActivas(ListView list){
    	long result = 0;
    	int iterar = 0;
    	CategoriaDTO catTemp = null;
    	while(iterar < list.getAdapter().getCount()){
    		catTemp = (CategoriaDTO) list.getItemAtPosition(iterar);
    		result = result + catTemp.getActiva();
    		iterar ++;
    	}
    	return result;
    }
  */

/*
    private void activarODesactivarCategoria(ListView list, int position, View v){
		LinearLayout ll = (LinearLayout)v;
		TextView tv = (TextView) ll.getChildAt(0);
//    	long cantCatActivas = this.cantidadCategoriasActivas(list);
    	CategoriaDTO catSelected = (CategoriaDTO) list.getItemAtPosition(position);
    	if(!(cantActivas == 1 && catSelected.getActiva() == 1)){
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
	    	if(catSelected.getActiva()==1){
	    		catSelected.setActiva(0);
	    		cantActivas--;
	    		//list.setItemChecked(position, false);
	    		tv.setTextColor(getResources().getColor(R.color.color_azul));	    		
	    	}else{
	    		catSelected.setActiva(1);
	    		cantActivas++;
	    		//list.setItemChecked(position, true);
	    		tv.setTextColor(getResources().getColor(R.color.color_gris_oscuro));
	    	}
	    	ConstantsAdmin.actualizarCategoria(catSelected, mDBManager);
	        labelCategorias.setText(titulo + " (" + cantActivas + "/" + cantCategorias + ")");
    	
    	}else{
    		
    		ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorDesactivandoCategorias));
    		list.setItemChecked(position, true);
    	}
    }
*/
	public void activarODesactivarCategoria(int position){
		CategoriaDTO catSelected = (CategoriaDTO) getListView().getItemAtPosition(position);
		if(!(cantActivas == 1 && catSelected.getActiva() == 1)){
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			if(catSelected.getActiva()==1){
				catSelected.setActiva(0);
				cantActivas--;
			}else{
				catSelected.setActiva(1);
				cantActivas++;
			}
			if(!catSelected.isCategoriaPersonal()){
                ConstantsAdmin.actualizarCategoria(catSelected, mDBManager);
            }else {
                ConstantsAdmin.actualizarCategoriaPersonal(catSelected, mDBManager);
            }

			labelCategorias.setText(titulo + " (" + cantActivas + "/" + cantCategorias + ")");

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
        /*else{
        	this.resetAllMyCursors();	
        }
        */
    }

/*
    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }

*/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	//	super.onListItemClick(l, v, position, id);
	//	this.activarODesactivarCategoria(this.getListView(), position, view);
	}


}
