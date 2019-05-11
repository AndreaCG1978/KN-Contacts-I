package com.boxico.android.kn.kncontacts.util;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import com.boxico.android.kn.kncontacts.R;
import com.boxico.android.kn.kncontacts.AltaPersonaActivity;


import java.util.ArrayList;
import java.util.HashMap;

public class KNSimpleCustomAdapter extends SimpleAdapter {

	private AltaPersonaActivity localContext = null;
	private ArrayList<HashMap<String,Object>> data = null;
	private static final String ID_TIPO_VALOR = "ID_TIPO_VALOR";
//	private static final String ID_PERSONA = "ID_VALOR";
	private boolean habilitado = true;
	//private static final String VALOR = "VALOR";
	//private static final String TIPO = "TIPO";
	private HashMap<Integer,String> datosEnLista = null;
  //  private static final String ID_CAMPO = "idCampo";

    public HashMap<Integer,String> getDatosEnLista() {
        return datosEnLista;
    }

    public void setDatosEnLista(HashMap<Integer,String> datosEnLista) {
        this.datosEnLista = datosEnLista;
    }

    public ArrayList<HashMap<String, Object>> getData() {
        return data;
    }

	public void setData(ArrayList<HashMap<String, Object>> data) {
		this.data = data;
	}

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
		this.data = data;
		//this.cargarDatosEnLista();
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
		final EditText etxt = view.findViewById(R.id.rowValor);
		final int pos = position;
	//	final KNSimpleCustomAdapter me = this;
		if(localContext.ismMostrarTelefonosBoolean()){
		    etxt.setInputType(InputType.TYPE_CLASS_PHONE );
		}else if(localContext.ismMostrarEmailsBoolean()){
		    etxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }else if(localContext.ismMostrarDireccionesBoolean()){
		    etxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }
		btn.setTag(position);
		//etxt.setTag(position);

		Object idTipoValor = (data.get(position)).get(ID_TIPO_VALOR);
		Long intValue = (Long)idTipoValor;
		etxt.setTag(intValue.intValue());

		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//Object element = (lista.get(pos));
				int position = (int) v.getTag();
				localContext.realzarBotonGuardar();
				Object idTipoValor = (data.get(position)).get(ID_TIPO_VALOR);
				String idTVString = idTipoValor.toString();
				long idTV = Long.valueOf(idTVString);
                if(localContext.ismMostrarTelefonosBoolean()){
                    ConstantsAdmin.getTelefonosAEliminar().add(idTV);
                }else if(localContext.ismMostrarEmailsBoolean()){
                    ConstantsAdmin.getMailsAEliminar().add(idTV);
                }else if(localContext.ismMostrarDireccionesBoolean()){
                    ConstantsAdmin.getDireccionesAEliminar().add(idTV);
                }
            //   lista.remove(element);
				//data.remove(pos);

               // data.remove(position);
				Long intValue = (Long)idTipoValor;
				localContext.eliminarItemListView(data, position);
                datosEnLista.remove(intValue.intValue());
          //      localContext.resetAdapter(data);

		//		localContext.realzarBotonGuardar();
        //        localContext.resetAdapter(data);


			}
		});

	/*	etxt.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				HashMap<String, String> hm = null;
				int position = (int) etxt.getTag();
				hm = datosEnLista.get(position);
				hm.put(VALOR, etxt.getText().toString());
				return true;
			}
		});
*/

		etxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
				boolean hasFocus = etxt.hasFocus();
				if(hasFocus){
					datosEnLista.put((Integer)etxt.getTag(), s.toString());

				}

            }

            @Override
            public void afterTextChanged(Editable s) {
            	if(localContext.isTerminoCargaListado()){
					localContext.realzarBotonGuardar();
				}
            }
        });

		localContext.setTerminoCargaListado(false);
		return view;
	}
}
