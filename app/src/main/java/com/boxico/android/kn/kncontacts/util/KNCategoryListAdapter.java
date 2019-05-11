package com.boxico.android.kn.kncontacts.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boxico.android.kn.kncontacts.ListadoCategoriaActivity;
import com.boxico.android.kn.kncontacts.R;
import com.boxico.android.kn.kncontacts.persistencia.DataBaseManager;
import com.boxico.android.kn.kncontacts.persistencia.dtos.CategoriaDTO;

import java.util.List;

public class KNCategoryListAdapter extends ArrayAdapter<CategoriaDTO> {

	private ListadoCategoriaActivity activity = null;
 	private boolean paraProteccion;

	public KNCategoryListAdapter(Context context, int resourceId, int textViewResourceId,
								 List<CategoriaDTO> objects, boolean paraProtec) {
		// TODO Auto-generated constructor stub
		super(context, resourceId, textViewResourceId, objects);
		paraProteccion = paraProtec;

        activity = (ListadoCategoriaActivity) context;

	}

	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);
		ListView lv = (ListView)parent;
		final CategoriaDTO cat = (CategoriaDTO) lv.getAdapter().getItem(position);
		final TextView tv;
		LinearLayout ll;
		ll = (LinearLayout)v;
		tv = ll.findViewById(R.id.text1);
		final int pos = position;
		ImageButton btnR = ll.findViewById(R.id.removeButton);

		if(!cat.isCategoriaPersonal()){
			btnR.setVisibility(View.GONE);
		}else{
			btnR.setVisibility(View.VISIBLE);
		}

		btnR.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mostrarEliminarCategoria(cat);
			}
		});



		ImageButton btnE = ll.findViewById(R.id.editButton);

		if(!cat.isCategoriaPersonal()){
			btnE.setVisibility(View.GONE);
		}else{
			btnE.setVisibility(View.VISIBLE);
		}

		btnE.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mostrarEditarCategoria(cat);
			}
		});

		final CheckBox cb = ll.findViewById(R.id.checkActivada);
	//	final ListadoCategoriaActivity act = (ListadoCategoriaActivity) activity;

		cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cb.isChecked()){
					tv.setTextColor(Color.WHITE);
				}else{
					tv.setTextColor(Color.LTGRAY);
				}
				activity.activarODesactivarCategoria(pos);
			}
		});

		if(!paraProteccion){// SE USA PARA MOSTRAR LAS CATEGORIAS ACTIVAS
			if(cat.getActiva() == 1){
				cb.setChecked(true);
				tv.setTextColor(Color.WHITE);
			}else{
				cb.setChecked(false);
				tv.setTextColor(Color.LTGRAY);
			}
		}
		return ll;
	}

	private void mostrarEditarCategoria(CategoriaDTO cat){
		activity.setCategoriaSeleccionada(cat);
		activity.openAltaCategoria();
	}

	private void mostrarEliminarCategoria(CategoriaDTO cat){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		final CategoriaDTO myCat = cat;
		builder.setMessage(R.string.mensaje_borrar_categoria)
				.setCancelable(false)
				.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						eliminarCategoriaSeleccionada(myCat);
						activity.refreshList();

					}
				})
				.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
	}

	private void eliminarCategoriaSeleccionada(CategoriaDTO cat){
		try {
			DataBaseManager mDBManager = DataBaseManager.getInstance(activity);
			ConstantsAdmin.eliminarCategoriaPersonal(cat, mDBManager);
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(activity, activity.getString(R.string.errorEliminacionCategoria));
		}

	}
	
	

}
