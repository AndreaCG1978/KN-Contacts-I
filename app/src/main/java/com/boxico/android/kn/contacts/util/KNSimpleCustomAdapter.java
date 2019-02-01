package com.boxico.android.kn.contacts.util;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.boxico.android.kn.contacts.R;
import com.boxico.android.kn.contacts.AltaPersonaActivity;


import java.util.ArrayList;
import java.util.HashMap;

public class KNSimpleCustomAdapter extends SimpleAdapter {

	private AltaPersonaActivity localContext = null;
	private ArrayList<HashMap<String,Object>> lista = null;
	private static final String ID_TIPO_VALOR = "ID_TIPO_VALOR";
	private static final String ID_PERSONA = "ID_PERSONA";
	private boolean habilitado = true;

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	/**
	 * Constructor
	 *
	 * @param context  The context where the View associated with this SimpleAdapter is running
	 * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
	 *                 Maps contain the data for each row, and should include all the entries specified in
	 *                 "from"
	 * @param resource Resource identifier of a view layout that defines the views for this list
	 *                 item. The layout file should include at least those named views defined in "to"
	 * @param from     A list of column names that will be added to the Map associated with each
	 *                 item.
	 * @param to       The views that should display column in the "from" parameter. These should all be
	 *                 TextViews. The first N views in this list are given the values of the first N columns
	 */
	public KNSimpleCustomAdapter(Context context, ArrayList<HashMap<String,Object>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		localContext = (AltaPersonaActivity) context;
		lista = data;
	}


	public ArrayList<HashMap<String,Object>> getLista() {
		return lista;
	}

	public void setLista(ArrayList<HashMap<String,Object>> lista) {
		this.lista = lista;
	}


	/*
	@Override
	public boolean isEnabled(int position) {
	//	return super.isEnabled(position);
		return habilitado;
	}

	@Override
	public boolean areAllItemsEnabled() {
	//	return super.areAllItemsEnabled();
		return habilitado;
	}
*/



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);
		Button btn = view.findViewById(R.id.removeButton);
		EditText etxt = view.findViewById(R.id.rowValor);
		final int pos = position;
		final KNSimpleCustomAdapter me = this;
		if(localContext.ismMostrarTelefonosBoolean()){
		    etxt.setInputType(InputType.TYPE_CLASS_PHONE);
        }else if(localContext.ismMostrarEmailsBoolean()){
		    etxt.setInputType(InputType.TYPE_CLASS_TEXT);
        }else if(localContext.ismMostrarDireccionesBoolean()){
		    etxt.setInputType(InputType.TYPE_CLASS_TEXT);
        }
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Object idTipoValor = (lista.get(pos)).get(ID_TIPO_VALOR);
				String idTVString = idTipoValor.toString();
				long idTV = Long.valueOf(idTVString);
                if(localContext.ismMostrarTelefonosBoolean()){
                    ConstantsAdmin.getTelefonosAEliminar().add(new Long(idTV));
                }else if(localContext.ismMostrarEmailsBoolean()){
                    ConstantsAdmin.getMailsAEliminar().add(new Long(idTV));
                }else if(localContext.ismMostrarDireccionesBoolean()){
                    ConstantsAdmin.getDireccionesAEliminar().add(new Long(idTV));
                }
				lista.remove(pos);
				me.notifyDataSetChanged();
				localContext.realzarBotonGuardar();
			}
		});
		etxt.addTextChangedListener(new TextWatcher() {

			private String temp = null;

			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				String t = null;
				if(s != null){
					t = s.toString();
				}
				if(t!= null && !(t.equals(temp.toString()))) {
					localContext.realzarBotonGuardar();//do your work here
					temp = t;
				}

			}



			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				temp = s.toString();
			}

			public void afterTextChanged(Editable s) {


			}
		});



		return view;
	}
}
